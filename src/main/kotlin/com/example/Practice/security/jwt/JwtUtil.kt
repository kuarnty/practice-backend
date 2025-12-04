package com.example.practice.security.jwt

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import java.util.*

object JwtUtil {
    private val secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256)
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