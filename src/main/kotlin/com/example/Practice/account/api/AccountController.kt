package com.example.practice.account.api

import com.example.practice.account.model.Account
import com.example.practice.account.service.AccountService

import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Controller
class AccountController(private val accountService: AccountService) {

    @QueryMapping(name = "accounts")
    fun accounts(): Flux<Account> = accountService.findAll()

    @QueryMapping(name = "account")
    fun findById(@Argument id: String): Mono<Account> = accountService.findById(id)

    @MutationMapping
    fun createAccount(
        @Argument name: String,
        @Argument email: String,
        @Argument password: String
    ): Mono<Account> = accountService.createAccount(name, email, password)

    @MutationMapping
    fun updateAccount(
        @Argument id: String,
        @Argument name: String?,
        @Argument email: String?,
        @Argument password: String?,
    ): Mono<Account> = accountService.updateAccount(id, name, email, password)

    @MutationMapping
    fun deleteAccount(@Argument id: String): Mono<Boolean> = accountService.deleteAccount(id)
}