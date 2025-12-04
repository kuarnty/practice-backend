package com.example.practice.security.jwt

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

class JwtAuthentication : AuthenticationWebFilter(ReactiveAuthenticationManager { authentication ->
    val token = authentication.credentials as String
    val email = JwtUtil.getEmail(token)
    Mono.just(UsernamePasswordAuthenticationToken(email, null, emptyList()))
}) {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val authHeader = exchange.request.headers.getFirst("Authorization")
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            val token = authHeader.substring(7)
            if (JwtUtil.validateToken(token)) {
                val email = JwtUtil.getEmail(token)
                val auth = UsernamePasswordAuthenticationToken(email, null, emptyList())
                val context = SecurityContextImpl(auth)
                return chain.filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)))
            }
        }
        return chain.filter(exchange)
    }
}