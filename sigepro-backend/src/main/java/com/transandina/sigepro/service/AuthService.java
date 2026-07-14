package com.transandina.sigepro.service;

import com.transandina.sigepro.config.JwtUtil;
import com.transandina.sigepro.dto.LoginRequest;
import com.transandina.sigepro.dto.LoginResponse;
import com.transandina.sigepro.entity.Usuario;
import com.transandina.sigepro.exception.ResourceNotFoundException;
import com.transandina.sigepro.repository.UsuarioRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authenticationManager,
                       UsuarioRepository usuarioRepository,
                       JwtUtil jwtUtil,
                       PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (Exception e) {
            throw new BadCredentialsException("Credenciales invalidas");
        }

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (!usuario.getActivo()) {
            throw new BadCredentialsException("Usuario desactivado");
        }

        String token = jwtUtil.generateToken(
                usuario.getEmail(),
                usuario.getRol().name(),
                usuario.getId()
        );

        return new LoginResponse(
                token,
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol().name()
        );
    }
}
