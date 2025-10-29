package com.example.practice.user.repository

import com.example.practice.user.model.User

import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono

interface UserRepository : ReactiveMongoRepository<User, String> {
    fun existsByUsername(username: String): Mono<Boolean>
    fun existsByEmail(email: String): Mono<Boolean>
    fun existsByUsernameAndIdNot(username: String, id: String): Mono<Boolean>
    fun existsByEmailAndIdNot(email: String, id: String): Mono<Boolean>
}