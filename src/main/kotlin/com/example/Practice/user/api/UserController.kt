package com.example.practice.user.api

import com.example.practice.user.model.User
import com.example.practice.user.repository.UserRepository
import com.example.practice.user.validation.UserValidationService

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
    private val userValidationService: UserValidationService
) {
    @MutationMapping
    fun createUser(
        @Argument username: String,
        @Argument email: String,
        @Argument password: String
    ): Mono<User> {
        return userValidationService.validateUserForCreate(username, email, password).flatMap { validationResult ->
            if (!validationResult.isValid) {
                Mono.error<User>(IllegalArgumentException(validationResult.errorMessage))
            }
            else {
                val user = User(username = username, email = email, password = password)
                userRepository.save(user)
            }
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
            userValidationService.validateUserForUpdate(id, newUsername, newEmail, newPassword).flatMap { validationResult ->
                if (!validationResult.isValid) {
                    Mono.error<User>(IllegalArgumentException(validationResult.errorMessage))
                }
                else {
                    val updatedUser = existingUser.copy(
                        username = newUsername,
                        email = newEmail,
                        password = newPassword
                    )
                    userRepository.save(updatedUser)
                }
            }
        }
    }

    @MutationMapping
    fun deleteUser(
        @Argument id: String
    ): Mono<Boolean> {
        return userRepository.existsById(id).flatMap { exists ->
            if (!exists) Mono.just(false)
            else userRepository.deleteById(id).thenReturn(true)
        }
    }
}