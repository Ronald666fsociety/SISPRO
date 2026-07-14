package com.transandina.sigepro.controller;

import com.transandina.sigepro.dto.LoginRequest;
import com.transandina.sigepro.dto.LoginResponse;
import com.transandina.sigepro.dto.MensajeResponse;
import com.transandina.sigepro.security.LoginRateLimiter;
import com.transandina.sigepro.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final LoginRateLimiter loginRateLimiter;

    public AuthController(AuthService authService, LoginRateLimiter loginRateLimiter) {
        this.authService = authService;
        this.loginRateLimiter = loginRateLimiter;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        String ip = httpRequest.getRemoteAddr();
        if (loginRateLimiter.isBlocked(ip)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(new MensajeResponse("Demasiados intentos. Intente nuevamente en 5 minutos."));
        }
        try {
            LoginResponse response = authService.login(request);
            loginRateLimiter.reset(ip);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            loginRateLimiter.registerFailed(ip);
            throw e;
        }
    }
}
