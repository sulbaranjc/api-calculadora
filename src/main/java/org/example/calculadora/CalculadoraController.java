package org.example.calculadora;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestController
@CrossOrigin(origins = "*")
public class CalculadoraController {

    private final CalculadoraService calculadoraService;

    public CalculadoraController(CalculadoraService calculadoraService) {
        this.calculadoraService = calculadoraService;
    }

    @GetMapping("/c")
    public Map<String, Double> calcular(@RequestParam double a,
                                        @RequestParam double b,
                                        @RequestParam String operacion) {
        return Map.of("resultado", calculadoraService.calcular(a, b, operacion));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> manejarErrorDeOperacion(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, String>> manejarParametroFaltante(MissingServletRequestParameterException e) {
        return ResponseEntity.badRequest()
                .body(Map.of("error", "Falta el parámetro obligatorio '" + e.getParameterName() + "'"));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> manejarTipoInvalido(MethodArgumentTypeMismatchException e) {
        return ResponseEntity.badRequest()
                .body(Map.of("error", "El parámetro '" + e.getName() + "' debe ser numérico"));
    }
}
