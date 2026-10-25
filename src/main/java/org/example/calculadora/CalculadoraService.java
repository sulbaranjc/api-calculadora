package org.example.calculadora;

import org.springframework.stereotype.Service;

@Service
public class CalculadoraService {

    public double calcular(double a, double b, String operacion) {
        if (operacion == null || operacion.isBlank()) {
            throw new IllegalArgumentException(
                    "Debe indicar una operación: suma, resta, multiplicacion o division");
        }

        // Se ignoran mayúsculas y espacios para que "Suma" o " suma " también funcionen.
        return switch (operacion.trim().toLowerCase()) {
            case "suma" -> a + b;
            case "resta" -> a - b;
            case "multiplicacion" -> a * b;
            case "division" -> {
                if (b == 0) {
                    throw new IllegalArgumentException("No se puede dividir entre cero");
                }
                yield a / b;
            }
            default -> throw new IllegalArgumentException(
                    "Operación no soportada: '" + operacion + "'. Use suma, resta, multiplicacion o division");
        };
    }
}
