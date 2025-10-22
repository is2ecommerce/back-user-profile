package com.is2ecommerce.userprofile.controller;

import com.is2ecommerce.userprofile.model.UserProfile;
import com.is2ecommerce.userprofile.service.UserProfileService;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/perfiles")
@CrossOrigin(origins = "http://localhost:4200")
public class UserProfileController {

    private static final Logger log = LoggerFactory.getLogger(UserProfileController.class);
    private final UserProfileService service;

    public UserProfileController(UserProfileService service) {
        this.service = service;
    }

    @GetMapping
    public Flux<UserProfile> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<UserProfile>> getById(@PathVariable String id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    @GetMapping("/perfil/{correo}")
    public Mono<ResponseEntity<UserProfile>> getPerfilByCorreo(@PathVariable String correo) {
    log.info("GET /api/perfiles/perfil/{}", correo);
    return service.findByCorreo(correo)
            .map(perfil -> {
                log.info("Perfil encontrado: {}", perfil);
                return ResponseEntity.ok(perfil);
            })
            .defaultIfEmpty(ResponseEntity.notFound().build())
            .onErrorResume(e -> {
                log.error("Error al buscar perfil por correo: {}", correo, e);
                return Mono.just(ResponseEntity.internalServerError().build());
            });
    }

    @PostMapping
    public Mono<ResponseEntity<UserProfile>> create(@RequestBody UserProfile perfil) {
        log.info("POST /api/perfiles body={}", perfil);
        if (perfil.getId() == null || perfil.getId().isBlank()) perfil.setId(null);
        if (perfil.getFechaRegistro() == null) perfil.setFechaRegistro(LocalDateTime.now());

        return service.create(perfil)
                .map(saved -> {
                    log.info("Perfil creado id={}", saved.getId());
                    return ResponseEntity.ok(saved);
                })
                .onErrorResume(e -> {
                    log.error("Error creando perfil", e);
                    return Mono.just(ResponseEntity.badRequest().build());
                });
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<UserProfile>> update(@PathVariable String id, @RequestBody UserProfile updatedProfile) {
    log.info("PUT /api/perfiles/{} - {}", id, updatedProfile);

    return service.update(id, updatedProfile)
            .flatMap(profile -> {
                log.info("Perfil actualizado: {}", profile);
                return Mono.just(ResponseEntity.ok(profile));
            })
            .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
            .onErrorResume(e -> {
                log.error("Error actualizando perfil id=" + id, e);
                return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
            });
}


    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String id) {
    log.info("DELETE /api/perfiles/{}", id);
    return service.delete(id)
            .flatMap(deleted -> {
                if (deleted) {
                    log.info("Perfil eliminado id={}", id);
                    return Mono.just(ResponseEntity.noContent().<Void>build());
                } else {
                    log.warn("Intento eliminar perfil no encontrado id={}", id);
                    ResponseEntity<Void> response = ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                    return Mono.just(response);
                }
            })
            .onErrorResume(e -> {
                log.error("Error eliminando perfil id=" + id, e);
                ResponseEntity<Void> response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                return Mono.just(response);
            });
}



}




