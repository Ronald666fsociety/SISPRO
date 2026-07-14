package com.transandina.sigepro.service;

import com.transandina.sigepro.dto.UsuarioRequest;
import com.transandina.sigepro.dto.UsuarioResponse;
import com.transandina.sigepro.entity.Usuario;
import com.transandina.sigepro.exception.ResourceNotFoundException;
import com.transandina.sigepro.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditoriaService auditoriaService;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder,
                          AuditoriaService auditoriaService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditoriaService = auditoriaService;
    }

    private Usuario getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (Usuario) auth.getPrincipal();
    }

    public List<UsuarioResponse> listarTodos() {
        return usuarioRepository.findByActivoTrue()
                .stream()
                .map(UsuarioResponse::fromEntity)
                .toList();
    }

    public UsuarioResponse buscarPorId(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
        return UsuarioResponse.fromEntity(usuario);
    }

    @Transactional
    public UsuarioResponse crear(UsuarioRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("El email " + request.getEmail() + " ya esta registrado");
        }

        Usuario usuario = new Usuario(
                request.getNombre(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getRol()
        );

        usuario = usuarioRepository.save(usuario);
        auditoriaService.registrar(getCurrentUser(), "CREAR", "Usuario", usuario.getId());
        return UsuarioResponse.fromEntity(usuario);
    }

    @Transactional
    public UsuarioResponse actualizar(Integer id, UsuarioRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

        if (!usuario.getEmail().equals(request.getEmail())
                && usuarioRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("El email " + request.getEmail() + " ya esta registrado");
        }

        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        usuario.setRol(request.getRol());

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        usuario = usuarioRepository.save(usuario);
        auditoriaService.registrar(getCurrentUser(), "ACTUALIZAR", "Usuario", usuario.getId());
        return UsuarioResponse.fromEntity(usuario);
    }

    @Transactional
    public void eliminar(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
        auditoriaService.registrar(getCurrentUser(), "ELIMINAR", "Usuario", id);
    }
}
