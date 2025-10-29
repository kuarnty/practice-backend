package com.example.practice.user.api

import com.example.practice.user.model.User
import com.example.practice.user.service.UserService

import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Controller
class UserController(private val userService: UserService) {

    @QueryMapping(name = "users")
    fun users(): Flux<User> = userService.findAll()

    @QueryMapping(name = "user")
    fun findById(@Argument id: String): Mono<User> = userService.findById(id)

    @MutationMapping
    fun createUser(
        @Argument name: String,
        @Argument email: String,
        @Argument password: String
    ): Mono<User> = userService.createUser(name, email, password)

    @MutationMapping
    fun updateUser(
        @Argument id: String,
        @Argument name: String?,
        @Argument email: String?,
        @Argument password: String?,
    ): Mono<User> = userService.updateUser(id, name, email, password)

    @MutationMapping
    fun deleteUser(@Argument id: String): Mono<Boolean> = userService.deleteUser(id)
}