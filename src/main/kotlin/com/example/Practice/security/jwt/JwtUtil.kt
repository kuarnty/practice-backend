package com.example.practice.security.jwt

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import java.util.*

object JwtUtil {
    private val secret: String = System.getenv("JWT_SECRET") ?: "testtesttesttesttesttesttesttest"
    private val secretKey = Keys.hmacShaKeyFor(secret.toByteArray())

    private const val EXPIRATION_TIME = 60 * 60 * 1000 // 1 hour

    fun generateToken(email: String): String =
        Jwts.builder()
            .setSubject(email)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .signWith(secretKey)
            .compact()

    fun validateToken(token: String): Boolean =
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token)
            true
        } catch (e: Exception) {
            false
        }

    fun getEmail(token: String): String =
        Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).body.subject
}