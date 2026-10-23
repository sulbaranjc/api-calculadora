# API Calculadora

API REST sencilla hecha con Spring Boot que expone un único endpoint para sumar, restar, multiplicar o dividir dos números.

## Requisitos

- Java 21
- Maven 3.8 o superior

## Cómo arrancarla

```bash
mvn spring-boot:run
```

La aplicación queda escuchando en `http://localhost:8081` (el puerto se configura en `src/main/resources/application.properties`).

Para ejecutar los tests:

```bash
mvn test
```

## Endpoint

```
GET /c?a={número}&b={número}&operacion={carácter}
```

| Parámetro   | Tipo   | Descripción                                   |
|-------------|--------|-----------------------------------------------|
| `a`         | número | Primer operando (admite decimales, p. ej. `2.5`) |
| `b`         | número | Segundo operando                              |
| `operacion` | string | Un solo carácter: `+`, `-`, `*` o `/`          |

### Respuesta correcta (`200 OK`)

```json
{ "resultado": 14.0 }
```

### Respuestas de error (`400 Bad Request`)

| Caso                         | Respuesta                                                      |
|------------------------------|----------------------------------------------------------------|
| División entre cero          | `{"error":"No se puede dividir entre cero"}`                   |
| Operación no soportada       | `{"error":"Operación no soportada: 'x'. Use +, -, * o /"}`     |
| `a` o `b` no son numéricos   | `{"error":"El parámetro 'a' debe ser numérico"}`               |
| Falta un parámetro           | `{"error":"Falta el parámetro obligatorio 'b'"}`               |

### CORS

El endpoint tiene CORS abierto a cualquier origen (`Access-Control-Allow-Origin: *`), así que se puede llamar con `fetch` desde una página web alojada en cualquier dominio:

```javascript
fetch("http://localhost:8081/c?a=10&b=4&operacion=%2B")
  .then(r => r.json())
  .then(data => console.log(data.resultado)); // 14
```

> **Nota sobre el `+`:** en una URL, el carácter `+` significa "espacio". Lo correcto es enviarlo codificado como `%2B`, aunque la API también interpreta el espacio como suma, así que `operacion=+` funciona igualmente.

## Uso con curl

```bash
# Suma
curl "http://localhost:8081/c?a=10&b=4&operacion=%2B"
# {"resultado":14.0}

# Resta
curl "http://localhost:8081/c?a=10&b=4&operacion=-"
# {"resultado":6.0}

# Multiplicación
curl "http://localhost:8081/c?a=10&b=4&operacion=*"
# {"resultado":40.0}

# División
curl "http://localhost:8081/c?a=10&b=4&operacion=/"
# {"resultado":2.5}

# Error: división entre cero
curl "http://localhost:8081/c?a=10&b=0&operacion=/"
# {"error":"No se puede dividir entre cero"}
```

Las comillas alrededor de la URL son importantes: sin ellas, la shell interpreta `&` como "ejecutar en segundo plano" y `*` como un comodín de archivos.

Si prefieres que curl codifique los parámetros por ti, usa `-G` con `--data-urlencode`:

```bash
curl -G "http://localhost:8081/c" \
  --data-urlencode "a=10" \
  --data-urlencode "b=4" \
  --data-urlencode "operacion=+"
# {"resultado":14.0}
```

Para ver también el código de estado HTTP, añade `-i`:

```bash
curl -i "http://localhost:8081/c?a=10&b=0&operacion=/"
```

## Uso con Postman

1. Arranca la API con `mvn spring-boot:run`.
2. En Postman, pulsa **New → HTTP Request** (o el botón **+** para abrir una pestaña nueva).
3. Selecciona el método **GET**.
4. Escribe la URL: `http://localhost:8081/c`
5. Abre la pestaña **Params** y añade los tres parámetros:

   | Key         | Value |
   |-------------|-------|
   | `a`         | `10`  |
   | `b`         | `4`   |
   | `operacion` | `*`   |

   Postman los añade automáticamente a la URL: `http://localhost:8081/c?a=10&b=4&operacion=*`
6. Pulsa **Send**. En el panel inferior verás el estado `200 OK` y el cuerpo:

   ```json
   {
       "resultado": 40.0
   }
   ```

Para probar las demás operaciones basta con cambiar el valor de `operacion` a `+`, `-` o `/`. Para la suma puedes escribir `%2B` o `+` (ambos funcionan).

Para probar los errores, prueba por ejemplo `b = 0` con `operacion = /`, o `operacion = x`: Postman mostrará el estado `400 Bad Request` y el mensaje de error en JSON.

**Consejo:** guarda la petición en una colección (**Save → New Collection**) y crea una variable `baseUrl = http://localhost:8081` para no repetir la dirección en cada petición (`{{baseUrl}}/c`).
