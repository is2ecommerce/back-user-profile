package com.is2ecommerce.userprofile.service;

import com.is2ecommerce.userprofile.model.UserProfile;
import com.is2ecommerce.userprofile.repository.UserProfileRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;

@Service
public class UserProfileService {

    private final UserProfileRepository repo;

    public UserProfileService(UserProfileRepository repo) {
        this.repo = repo;
    }

    public Flux<UserProfile> findAll() {
        return repo.findAll();
    }

    public Mono<UserProfile> findById(String id) {
        return repo.findById(id);
    }

    public Mono<UserProfile> findByCorreo(String correo) {
        return repo.findByCorreo(correo);
    }

    public Mono<UserProfile> create(UserProfile perfil) {
        perfil.setId(null);
        if (perfil.getFechaRegistro() == null) {
            perfil.setFechaRegistro(LocalDateTime.now());
        }
        return repo.save(perfil);
    }

    public Mono<UserProfile> update(String id, UserProfile perfil) {
        return repo.findById(id)
                .flatMap(existing -> {
                    if (perfil.getNombre() != null) existing.setNombre(perfil.getNombre());
                    if (perfil.getCorreo() != null) existing.setCorreo(perfil.getCorreo());
                    if (perfil.getTelefono() != null) existing.setTelefono(perfil.getTelefono());
                    if (perfil.getDireccion() != null) existing.setDireccion(perfil.getDireccion());
                    return repo.save(existing);
                });
    }

    
    public Mono<Boolean> delete(String id) {
        return repo.findById(id)
                .flatMap(existing -> repo.delete(existing).thenReturn(Boolean.TRUE))
                .defaultIfEmpty(Boolean.FALSE);
    }
}

