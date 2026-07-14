package com.transandina.sigepro.validation;

import com.transandina.sigepro.dto.ProyectoRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FechaCoherenteValidatorTest {

    private final Validator validator;

    FechaCoherenteValidatorTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    @DisplayName("Fecha fin posterior a inicio - valido")
    void fechaFinPosterior_Valido() {
        ProyectoRequest req = new ProyectoRequest();
        req.setNombre("Test");
        req.setFechaInicio(LocalDate.of(2026, 1, 1));
        req.setFechaFin(LocalDate.of(2026, 6, 30));
        req.setIdJefeProyecto(1);
        req.setDescripcion("Test desc");

        Set<ConstraintViolation<ProyectoRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().noneMatch(v -> v.getMessage().contains("fecha de fin")));
    }

    @Test
    @DisplayName("Fecha fin igual a inicio - valido")
    void fechaFinIgual_Valido() {
        ProyectoRequest req = new ProyectoRequest();
        req.setNombre("Test");
        req.setFechaInicio(LocalDate.of(2026, 1, 1));
        req.setFechaFin(LocalDate.of(2026, 1, 1));
        req.setIdJefeProyecto(1);

        Set<ConstraintViolation<ProyectoRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().noneMatch(v -> v.getMessage().contains("fecha de fin")));
    }

    @Test
    @DisplayName("Fecha fin anterior a inicio - invalido")
    void fechaFinAnterior_Invalido() {
        ProyectoRequest req = new ProyectoRequest();
        req.setNombre("Test");
        req.setFechaInicio(LocalDate.of(2026, 6, 30));
        req.setFechaFin(LocalDate.of(2026, 1, 1));
        req.setIdJefeProyecto(1);

        Set<ConstraintViolation<ProyectoRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("fecha de fin")));
    }

    @Test
    @DisplayName("Porcentaje avance negativo - invalido")
    void porcentajeAvanceNegativo_Invalido() {
        com.transandina.sigepro.dto.TareaRequest req = new com.transandina.sigepro.dto.TareaRequest();
        req.setIdProyecto(1);
        req.setNombre("Test Tarea");
        req.setFechaInicio(LocalDate.of(2026, 1, 1));
        req.setFechaFin(LocalDate.of(2026, 6, 30));
        req.setIdResponsable(1);
        req.setPorcentajeAvance(-10);

        Set<ConstraintViolation<com.transandina.sigepro.dto.TareaRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("no puede ser negativo")));
    }

    @Test
    @DisplayName("Porcentaje avance mayor a 100 - invalido")
    void porcentajeAvanceMayor100_Invalido() {
        com.transandina.sigepro.dto.TareaRequest req = new com.transandina.sigepro.dto.TareaRequest();
        req.setIdProyecto(1);
        req.setNombre("Test Tarea");
        req.setFechaInicio(LocalDate.of(2026, 1, 1));
        req.setFechaFin(LocalDate.of(2026, 6, 30));
        req.setIdResponsable(1);
        req.setPorcentajeAvance(150);

        Set<ConstraintViolation<com.transandina.sigepro.dto.TareaRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("no puede superar")));
    }

    @Test
    @DisplayName("Presupuesto estimado negativo - invalido")
    void presupuestoEstimadoNegativo_Invalido() {
        com.transandina.sigepro.dto.TareaRequest req = new com.transandina.sigepro.dto.TareaRequest();
        req.setIdProyecto(1);
        req.setNombre("Test Tarea");
        req.setFechaInicio(LocalDate.of(2026, 1, 1));
        req.setFechaFin(LocalDate.of(2026, 6, 30));
        req.setIdResponsable(1);
        req.setPresupuestoEstimado(new java.math.BigDecimal("-100.00"));

        Set<ConstraintViolation<com.transandina.sigepro.dto.TareaRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("no puede ser negativo")));
    }
}
