package com.example.practice.security.jwt

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.authentication.ReactiveAuthenticationManager
import reactor.core.publisher.Mono

class JwtAuthenticationManager : ReactiveAuthenticationManager {
    override fun authenticate(authentication: Authentication): Mono<Authentication> {
        val token = authentication.credentials as? String ?: return Mono.empty()
        // JWT 검증 로직 (예: 서명, 만료, 클레임 등)
        // 검증 성공 시 Authentication 객체 반환
        val user = User("username", "", listOf(SimpleGrantedAuthority("ROLE_USER")))
        return Mono.just(UsernamePasswordAuthenticationToken(user, token, user.authorities))
    }
}