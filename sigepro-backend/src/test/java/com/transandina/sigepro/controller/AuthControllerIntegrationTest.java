package com.transandina.sigepro.controller;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Test
    @DisplayName("Login con credenciales invalidas retorna 401")
    void login_CredencialesInvalidas_Retorna401() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"invalido@test.com\",\"password\":\"wrong\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Login con JSON mal formado retorna 400")
    void login_JsonMalFormado_Retorna400() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{email: \"test@test.com\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Login con email vacio retorna 400")
    void login_EmailVacio_Retorna400() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"\",\"password\":\"123456\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Intento de inyeccion SQL en login retorna 401 (no 500)")
    void login_InyeccionSQL_NoExponeError() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"admin@test.com' OR '1'='1\",\"password\":\"' OR '1'='1\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Token JWT expirado retorna 403")
    void tokenExpirado_Retorna403() throws Exception {
        Date pasado = new Date(System.currentTimeMillis() - 100000);
        String expiredToken = Jwts.builder()
                .subject("test@test.com")
                .claim("rol", "ADMINISTRADOR")
                .claim("usuarioId", 1)
                .issuedAt(new Date(System.currentTimeMillis() - 200000))
                .expiration(pasado)
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                .compact();

        mockMvc.perform(get("/api/proyectos")
                .header("Authorization", "Bearer " + expiredToken))
                .andExpect(status().isForbidden());
    }
}
