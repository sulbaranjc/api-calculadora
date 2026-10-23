package org.example.calculadora;

import org.springframework.stereotype.Service;

@Service
public class CalculadoraService {

    public double calcular(double a, double b, String operacion) {
        if (operacion == null || operacion.length() != 1) {
            throw new IllegalArgumentException("La operación debe ser un único carácter: +, -, * o /");
        }

        return switch (operacion.charAt(0)) {
            // En una query string un '+' sin codificar llega como espacio, así que se acepta también.
            case '+', ' ' -> a + b;
            case '-' -> a - b;
            case '*' -> a * b;
            case '/' -> {
                if (b == 0) {
                    throw new IllegalArgumentException("No se puede dividir entre cero");
                }
                yield a / b;
            }
            default -> throw new IllegalArgumentException(
                    "Operación no soportada: '" + operacion + "'. Use +, -, * o /");
        };
    }
}
