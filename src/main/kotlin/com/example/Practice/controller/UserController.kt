package com.example.practice.controller

import com.example.practice.model.User
import com.example.practice.repository.UserRepository
import com.example.practice.validation.ValidationService

import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.stereotype.Controller
import reactor.core.publisher.Mono
import reactor.core.publisher.Flux

// GraphQL controller for User queries.
@Controller
class UserController(
    private val userRepository: UserRepository
) {
    @QueryMapping
    fun users(): Flux<User> = userRepository.findAll()
}

// Mutation controller for User entity
@Controller
class UserMutationController(
    private val userRepository: UserRepository,
    private val validationService: ValidationService
) {
    @MutationMapping
    fun createUser(
        @Argument username: String,
        @Argument email: String,
        @Argument password: String
    ): Mono<User> {
        return validationService.validateUser(username, email, password).flatMap { validationResult ->
            if (!validationResult.isValid) {
                return@flatMap Mono.error<User>(IllegalArgumentException(validationResult.errorMessage))
            }
            val user = User(username = username, email = email, password = password)
            userRepository.save(user)
        }
    }

    @MutationMapping
    fun updateUser(
        @Argument id: String,
        @Argument username: String?,
        @Argument email: String?,
        @Argument password: String?
    ): Mono<User> {
        return userRepository.findById(id).flatMap { existingUser ->
            val newUsername = username ?: existingUser.username
            val newEmail = email ?: existingUser.email
            val newPassword = password ?: existingUser.password
            validationService.validateUser(newUsername, newEmail, newPassword).flatMap { validationResult ->
                if (!validationResult.isValid) {
                    return@flatMap Mono.error<User>(IllegalArgumentException(validationResult.errorMessage))
                }
                val updatedUser = existingUser.copy(
                    username = newUsername,
                    email = newEmail,
                    password = newPassword
                )
                userRepository.save(updatedUser)
            }
        }
    }

    @MutationMapping
    fun deleteUser(
        @Argument id: String
    ): Mono<Boolean> {
        return userRepository.existsById(id).flatMap { exists ->
            if (!exists) return@flatMap Mono.just(false)
            userRepository.deleteById(id).thenReturn(true)
        }
    }
}