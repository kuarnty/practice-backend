package com.example.practice.controller

import com.example.practice.model.User
import com.example.practice.repository.UserRepository
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.stereotype.Controller
import reactor.core.publisher.Mono
import reactor.core.publisher.Flux
import java.time.Instant

//GraphQL controller for User queries.
@Controller
class UserController(
    private val userRepository: UserRepository
) {
    /**
     * Returns a list of all users. Never returns null.
     */
    @QueryMapping
    fun users(): Flux<User> = userRepository.findAll()
}

// Mutation controller for User entityt
@Controller
class UserMutationController(
    private val userRepository: UserRepository
) {
    // Create a new User
    @MutationMapping
    fun createUser(
        @Argument username: String,
        @Argument email: String,
        @Argument password: String
    ): Mono<User> {
        val user = User(
            username = username,
            email = email,
            password = password,
            createdAt = Instant.now()
        )
        return userRepository.save(user)
    }
}