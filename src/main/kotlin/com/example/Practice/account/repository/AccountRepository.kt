package com.example.practice.account.repository

import com.example.practice.account.model.Account

import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono

interface AccountRepository : ReactiveMongoRepository<Account, String> {
    fun existsByName(name: String): Mono<Boolean>
    fun existsByEmail(email: String): Mono<Boolean>
    fun existsByNameAndIdNot(name: String, id: String): Mono<Boolean>
    fun existsByEmailAndIdNot(email: String, id: String): Mono<Boolean>
}