package com.example.practice.security

import com.example.practice.security.jwt.JwtAuthenticationManager

import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import reactor.core.publisher.Mono

@Configuration
class SecurityConfig {
    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        val authenticationWebFilter = AuthenticationWebFilter(JwtAuthenticationManager())

        return http
            .csrf { it.disable() }
            .authorizeExchange {
                // Extract JWT from Authorization header
                authenticationWebFilter.setServerAuthenticationConverter { exchange ->
                    val authHeader = exchange.request.headers.getFirst("Authorization")
                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        val token = authHeader.substring(7)
                        Mono.just(UsernamePasswordAuthenticationToken(token, token))
                    } else {
                        Mono.empty()
                    }
                }
                
                it.pathMatchers("/", "/static/**", "/index.html", "/graphql").permitAll()
                it.anyExchange().authenticated()
            }
            // .authenticationManager(JwtAuthenticationManager())
            .addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .build()
    }
}