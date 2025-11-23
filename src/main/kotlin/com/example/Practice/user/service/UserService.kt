package com.example.practice.user.service

import com.example.practice.user.model.User
import com.example.practice.user.repository.UserRepository
import com.example.practice.user.validation.UserValidationService

import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant

@Service
class UserService(
    private val userRepository: UserRepository,
    private val userValidationService: UserValidationService
) {
    
    fun findAll(): Flux<User> = userRepository.findAll()
    
    fun findById(id: String): Mono<User> = userRepository.findById(id)

    fun createUser(name: String, email: String, password: String): Mono<User> {
        return userValidationService.validateUserForCreate(name, email, password)
            .flatMap { vr ->
                if (!vr.isValid) {
                    return@flatMap Mono.error(IllegalArgumentException(vr.errorMessage ?: "validation failed"))
                }

                val user = User(
                    id = null,
                    name = name,
                    email = email,
                    password = password,
                    createdAt = Instant.now()
                )

                userRepository.save(user)
                    .onErrorResume { ex ->
                        val isDuplicate = ex is DuplicateKeyException ||
                                (ex.cause is com.mongodb.MongoWriteException &&
                                        (ex.cause as com.mongodb.MongoWriteException).error.category == com.mongodb.ErrorCategory.DUPLICATE_KEY)
                        if (isDuplicate) Mono.error(IllegalArgumentException("User with the same email already exists."))
                        else Mono.error(ex)
                    }
            }
    }

    fun updateUser(id: String, name: String?, email: String?, password: String?): Mono<User> {
        return userValidationService.validateUserForUpdate(id, name, email, password)
            .flatMap { vr ->
                if (!vr.isValid) return@flatMap Mono.error(IllegalArgumentException(vr.errorMessage ?: "validation failed"))

                userRepository.findById(id).flatMap { existing ->
                    val updated = existing.copy(
                        name = name ?: existing.name,
                        email = email ?: existing.email,
                        password = password ?: existing.password
                    )
                    userRepository.save(updated)
                        .onErrorResume { ex ->
                            val isDuplicate = ex is DuplicateKeyException ||
                                    (ex.cause is com.mongodb.MongoWriteException &&
                                            (ex.cause as com.mongodb.MongoWriteException).error.category == com.mongodb.ErrorCategory.DUPLICATE_KEY)
                            if (isDuplicate) Mono.error(IllegalArgumentException("Another user with same email exists."))
                            else Mono.error(ex)
                        }
                }
            }
    }

    fun deleteUser(id: String): Mono<Boolean> {
        return userRepository.findById(id)
            .flatMap { _ -> userRepository.deleteById(id).then(Mono.just(true)) }
            .defaultIfEmpty(false)
    }
}