package com.is2ecommerce.userprofile.repository;

import com.is2ecommerce.userprofile.model.UserProfile;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface UserProfileRepository extends ReactiveMongoRepository<UserProfile, String> {
    Mono<UserProfile> findByCorreo(String correo);
}

