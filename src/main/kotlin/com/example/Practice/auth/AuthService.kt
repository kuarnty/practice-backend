package com.example.practice.auth

import com.example.practice.user.repository.UserRepository

import com.example.practice.security.jwt.JwtUtil
import com.example.practice.user.model.User
import com.example.practice.auth.AuthPayload
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AuthService(private val userRepository: UserRepository) {
    fun authenticate(email: String, password: String): Mono<AuthPayload> {
        return userRepository.findByEmail(email)
            .mapNotNull { user ->
                if (user != null && user.password == password) {
                    val token = JwtUtil.generateToken(email)
                    AuthPayload(token, user)
                } else null
            }
    }
}