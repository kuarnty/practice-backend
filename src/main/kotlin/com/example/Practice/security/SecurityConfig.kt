package com.example.practice.security

import com.example.practice.security.jwt.JwtAuthenticationManager
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter

@Configuration
class SecurityConfig {
    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        val authenticationWebFilter = AuthenticationWebFilter(JwtAuthenticationManager())
        authenticationWebFilter.setSecurityContextRepository(WebSessionServerSecurityContextRepository())

        return http
            .csrf { it.disable() }
            .authorizeExchange {
                it.pathMatchers("/", "/static/**", "/index.html", "/graphql").permitAll()
                it.anyExchange().authenticated()
            }
            .authenticationManager(JwtAuthenticationManager())
            .securityContextRepository(WebSessionServerSecurityContextRepository())
            // .addFilterAt(authenticationWebFilter, org.springframework.security.web.server.SecurityWebFiltersOrder.AUTHENTICATION) // Removed due to unresolved reference
            .build()
    }
}