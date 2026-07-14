package com.transandina.sigepro.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FechaCoherenteValidator.class)
@Documented
public @interface FechaCoherente {
    String message() default "La fecha de fin no puede ser anterior a la fecha de inicio";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String fechaInicio();
    String fechaFin();
}
