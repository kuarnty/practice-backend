package com.example.practice.security.jwt

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.authentication.ReactiveAuthenticationManager
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import reactor.core.publisher.Mono

class JwtAuthenticationManager : ReactiveAuthenticationManager {

    private val secret: String = System.getenv("JWT_SECRET") ?: "testtesttesttesttesttesttesttest"
    private val secretKey = Keys.hmacShaKeyFor(secret.toByteArray())

    override fun authenticate(authentication: Authentication): Mono<Authentication> {
        val token = authentication.credentials as? String ?: run {
            
            // println("\n[JwtAuthenticationManager] No JWT token found in credentials.\n")
            
            return Mono.empty()
        }

        // println("[JwtAuthenticationManager] Received JWT token: $token")

        try {
            val claimsJws = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
            val claims = claimsJws.body

            // println("\n[JwtAuthenticationManager] Parsed claims: subject=${claims.subject}, issuedAt=${claims.issuedAt}, expiration=${claims.expiration}\n")
            
            val email = claims.subject // usually the subject contains the user identifier
            val authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))
            val user = User(email, "", authorities) // be the principal parameter for user identification in controllers
            return Mono.just(UsernamePasswordAuthenticationToken(user, token, authorities))
        } catch (e: Exception) {

            // println("\n[JwtAuthenticationManager] JWT parsing/validation failed: ${e.message}\n")

            return Mono.empty()
        }
    }
}