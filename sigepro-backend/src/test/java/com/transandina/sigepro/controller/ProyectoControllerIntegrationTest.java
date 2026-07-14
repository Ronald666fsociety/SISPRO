package com.transandina.sigepro.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.security.test.context.support.WithMockUser;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProyectoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/proyectos sin token retorna 403")
    void listarProyectos_SinToken_Retorna403() throws Exception {
        mockMvc.perform(get("/api/proyectos"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    @DisplayName("POST /api/proyectos con fecha fin anterior a inicio retorna 400")
    void crearProyecto_FechaIncoherente_Retorna400() throws Exception {
        mockMvc.perform(post("/api/proyectos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\"Test\",\"fechaInicio\":\"2026-06-30\",\"fechaFin\":\"2026-01-01\",\"idJefeProyecto\":1}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    @DisplayName("POST /api/proyectos con nombre vacio retorna 400")
    void crearProyecto_NombreVacio_Retorna400() throws Exception {
        mockMvc.perform(post("/api/proyectos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\"\",\"fechaInicio\":\"2026-01-01\",\"fechaFin\":\"2026-06-30\",\"idJefeProyecto\":1}"))
                .andExpect(status().isBadRequest());
    }
}
