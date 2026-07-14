package com.transandina.sigepro.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Method;
import java.time.LocalDate;

public class FechaCoherenteValidator implements ConstraintValidator<FechaCoherente, Object> {

    private String fechaInicioField;
    private String fechaFinField;

    @Override
    public void initialize(FechaCoherente constraint) {
        this.fechaInicioField = constraint.fechaInicio();
        this.fechaFinField = constraint.fechaFin();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        try {
            Method getInicio = value.getClass().getMethod("get" + capitalize(fechaInicioField));
            Method getFin = value.getClass().getMethod("get" + capitalize(fechaFinField));
            LocalDate inicio = (LocalDate) getInicio.invoke(value);
            LocalDate fin = (LocalDate) getFin.invoke(value);
            if (inicio == null || fin == null) return true;
            return !fin.isBefore(inicio);
        } catch (Exception e) {
            return true;
        }
    }

    private String capitalize(String s) {
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
