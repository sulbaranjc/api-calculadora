# Contrato de la API Calculadora

Documento de referencia para el equipo de frontend. Describe cómo llamar a la API, qué devuelve y cómo tratar los errores.

| Dato              | Valor                          |
|-------------------|--------------------------------|
| Versión           | 1.0                            |
| URL base (local)  | `http://localhost:8081`        |
| Formato           | JSON (`application/json`)      |
| Autenticación     | Ninguna                        |
| CORS              | Abierto a cualquier origen (`Access-Control-Allow-Origin: *`) |

---

## 1. Endpoint: calcular

```
GET /c?a={número}&b={número}&operacion={operación}
```

Realiza una operación aritmética entre dos números.

### 1.1 Parámetros (query string)

Los tres parámetros son **obligatorios**.

| Parámetro   | Tipo   | Descripción | Ejemplos |
|-------------|--------|-------------|----------|
| `a`         | número | Primer operando. Admite enteros, decimales con punto y notación científica. | `10`, `-2.5`, `1e3` |
| `b`         | número | Segundo operando. Mismo formato que `a`. | `4`, `0.5` |
| `operacion` | string | Operación a realizar. Valores admitidos en la tabla siguiente. | `suma` |

Valores admitidos para `operacion`:

| Valor            | Cálculo  |
|------------------|----------|
| `suma`           | `a + b`  |
| `resta`          | `a - b`  |
| `multiplicacion` | `a * b`  |
| `division`       | `a / b`  |

- No distingue mayúsculas/minúsculas (`Suma`, `SUMA` y `suma` son equivalentes) y se ignoran espacios al inicio y al final.
- Los valores van **sin tilde**: `multiplicacion` y `division`. Con tilde se responde como operación no soportada.
- El separador decimal es el punto (`2.5`). La coma (`2,5`) se considera no numérica.

### 1.2 Respuesta correcta — `200 OK`

```json
{
  "resultado": 14.0
}
```

| Campo       | Tipo   | Descripción |
|-------------|--------|-------------|
| `resultado` | número | Resultado de la operación. Siempre se serializa como decimal (`14.0`), aunque sea entero. |

> **Caso límite:** si el resultado desborda el rango de un `double` (por ejemplo `a=1.5e308&b=10&operacion=multiplicacion`), la API devuelve `{"resultado":"Infinity"}` como **string**. El frontend debería comprobar `typeof data.resultado === "number"` antes de usarlo.

### 1.3 Respuestas de error — `400 Bad Request`

Todos los errores de validación tienen la misma forma:

```json
{
  "error": "Mensaje legible para el usuario"
}
```

| Caso | Ejemplo de petición | Mensaje en `error` |
|------|---------------------|--------------------|
| División entre cero | `a=7&b=0&operacion=division` | `No se puede dividir entre cero` |
| Operación no soportada | `a=7&b=2&operacion=potencia` | `Operación no soportada: 'potencia'. Use suma, resta, multiplicacion o division` |
| Operación vacía | `a=7&b=2&operacion=` | `Debe indicar una operación: suma, resta, multiplicacion o division` |
| `a` o `b` no numérico o vacío | `a=abc&b=2&operacion=suma` | `El parámetro 'a' debe ser numérico` |
| Falta un parámetro | `a=1&operacion=suma` | `Falta el parámetro obligatorio 'b'` |

Los mensajes están en español y pensados para poder mostrarse directamente al usuario.

### 1.4 Otros códigos HTTP

Estos no los genera la lógica de la calculadora sino Spring Boot, y su cuerpo **no** sigue el formato `{ "error": ... }`:

| Código | Cuándo ocurre |
|--------|---------------|
| `404 Not Found` | La ruta no existe (p. ej. `/calcular`). |
| `405 Method Not Allowed` | Se usa un método distinto de `GET` (p. ej. `POST /c`). |
| `500 Internal Server Error` | Error inesperado del servidor. |

---

## 2. Tipos TypeScript

```typescript
export type Operacion = "suma" | "resta" | "multiplicacion" | "division";

export interface CalculoOk {
  resultado: number;
}

export interface CalculoError {
  error: string;
}
```

---

## 3. Ejemplo de consumo

```typescript
const BASE_URL = "http://localhost:8081";

export async function calcular(a: number, b: number, operacion: Operacion): Promise<number> {
  const params = new URLSearchParams({
    a: String(a),
    b: String(b),
    operacion,
  });

  const respuesta = await fetch(`${BASE_URL}/c?${params}`);

  if (respuesta.status === 400) {
    const { error }: CalculoError = await respuesta.json();
    throw new Error(error);
  }
  if (!respuesta.ok) {
    throw new Error(`Error inesperado del servidor (${respuesta.status})`);
  }

  const { resultado }: CalculoOk = await respuesta.json();
  if (typeof resultado !== "number") {
    throw new Error("El resultado está fuera de rango");
  }
  return resultado;
}

// Uso
calcular(10, 4, "division")
  .then((r) => console.log(r))            // 2.5
  .catch((e) => console.error(e.message)); // p. ej. "No se puede dividir entre cero"
```

Se recomienda construir la URL con `URLSearchParams` para que los valores se codifiquen correctamente.

---

## 4. Ejemplos rápidos

| Petición | Código | Respuesta |
|----------|--------|-----------|
| `GET /c?a=10&b=4&operacion=suma` | 200 | `{"resultado":14.0}` |
| `GET /c?a=10&b=4&operacion=resta` | 200 | `{"resultado":6.0}` |
| `GET /c?a=10&b=4&operacion=multiplicacion` | 200 | `{"resultado":40.0}` |
| `GET /c?a=10&b=4&operacion=division` | 200 | `{"resultado":2.5}` |
| `GET /c?a=10&b=0&operacion=division` | 400 | `{"error":"No se puede dividir entre cero"}` |
