package com.example.practice.repository

import com.example.practice.model.User
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono

interface UserRepository : ReactiveMongoRepository<User, String> {
    fun existsByUsername(username: String): Mono<Boolean>
    fun existsByEmail(email: String): Mono<Boolean>
}