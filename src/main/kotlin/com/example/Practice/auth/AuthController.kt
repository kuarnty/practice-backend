package com.example.practice.auth

import com.example.practice.auth.AuthService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Mono
import com.example.practice.user.model.User

data class LoginInput(val email: String, val password: String)

data class AuthPayload(val token: String, val user: User)

@Controller
class AuthMutation(private val authService: AuthService) {
    @MutationMapping
    fun login(@Argument input: LoginInput): Mono<AuthPayload> {
        return authService.authenticate(input.email, input.password)
            .map { it ?: throw IllegalStateException("Authentication failed: AuthPayload is null") }
    }
}