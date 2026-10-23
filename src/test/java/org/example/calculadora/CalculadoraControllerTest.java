package org.example.calculadora;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CalculadoraController.class)
@Import(CalculadoraService.class)
class CalculadoraControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void suma() throws Exception {
        mockMvc.perform(get("/c").param("a", "5").param("b", "3").param("operacion", "+"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultado").value(8.0));
    }

    @Test
    void resta() throws Exception {
        mockMvc.perform(get("/c").param("a", "5").param("b", "3").param("operacion", "-"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultado").value(2.0));
    }

    @Test
    void multiplicacion() throws Exception {
        mockMvc.perform(get("/c").param("a", "5").param("b", "3").param("operacion", "*"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultado").value(15.0));
    }

    @Test
    void division() throws Exception {
        mockMvc.perform(get("/c").param("a", "7").param("b", "2").param("operacion", "/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultado").value(3.5));
    }

    @Test
    void divisionEntreCero() throws Exception {
        mockMvc.perform(get("/c").param("a", "7").param("b", "0").param("operacion", "/"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("No se puede dividir entre cero"));
    }

    @Test
    void operacionNoSoportada() throws Exception {
        mockMvc.perform(get("/c").param("a", "7").param("b", "2").param("operacion", "%"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void parametroNoNumerico() throws Exception {
        mockMvc.perform(get("/c").param("a", "abc").param("b", "2").param("operacion", "+"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("El parámetro 'a' debe ser numérico"));
    }

    @Test
    void parametroFaltante() throws Exception {
        mockMvc.perform(get("/c").param("a", "1").param("operacion", "+"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Falta el parámetro obligatorio 'b'"));
    }
}
