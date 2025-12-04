package com.example.practice.common.error

import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebExceptionHandler
import reactor.core.publisher.Mono
import reactor.core.publisher.Flux

@Component
class GlobalErrorHandler : WebExceptionHandler {
    override fun handle(exchange: ServerWebExchange, ex: Throwable): Mono<Void> {
        return Mono.fromRunnable {
            //TODO: Specific error handling logic
            //TODO: Logging the error
            println("Exception caught in GlobalErrorHandler: ${ex.message}")
        }
    }
}