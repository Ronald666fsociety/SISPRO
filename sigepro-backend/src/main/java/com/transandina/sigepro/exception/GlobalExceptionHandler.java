package com.transandina.sigepro.exception;

import com.transandina.sigepro.dto.MensajeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<MensajeResponse> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new MensajeResponse(ex.getMessage()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<MensajeResponse> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new MensajeResponse("Credenciales invalidas"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<MensajeResponse> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new MensajeResponse("No tienes permiso para acceder a este recurso"));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<MensajeResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new MensajeResponse(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<MensajeResponse> handleDataIntegrity(DataIntegrityViolationException ex) {
        log.error("Violacion de integridad de datos", ex);
        String msg = ex.getRootCause() != null ? ex.getRootCause().getMessage() : "";
        if (msg.contains("Duplicate entry")) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new MensajeResponse("El registro ya existe (valor duplicado)"));
        }
        if (msg.contains("Cannot delete or update a parent row")) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new MensajeResponse("No se puede eliminar porque tiene registros asociados"));
        }
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new MensajeResponse("Error de integridad de datos"));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<MensajeResponse> handleMessageNotReadable(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new MensajeResponse("Solicitud mal formada. Verifique el formato de los datos enviados."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<MensajeResponse> handleGeneral(Exception ex) {
        log.error("Error interno no controlado", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MensajeResponse("Error interno del servidor. Si el problema persiste, contacte al administrador."));
    }
}
