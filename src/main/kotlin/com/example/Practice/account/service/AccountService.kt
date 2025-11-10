package com.example.practice.account.service

import com.example.practice.account.model.Account
import com.example.practice.account.repository.AccountRepository
import com.example.practice.account.validation.AccountValidationService

import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant

@Service
class AccountService(
    private val accountRepository: AccountRepository,
    private val accountValidationService: AccountValidationService
) {
    
    fun findAll(): Flux<Account> = accountRepository.findAll()
    
    fun findById(id: String): Mono<Account> = accountRepository.findById(id)

    fun createAccount(name: String, email: String, password: String): Mono<Account> {
        return accountValidationService.validateAccountForCreate(name, email, password)
            .flatMap { vr ->
                if (!vr.isValid) {
                    return@flatMap Mono.error(IllegalArgumentException(vr.errorMessage ?: "validation failed"))
                }

                val account = Account(
                    id = null,
                    name = name,
                    email = email,
                    password = password,
                    createdAt = Instant.now()
                )

                accountRepository.save(account)
                    .onErrorResume { ex ->
                        val isDuplicate = ex is DuplicateKeyException ||
                                (ex.cause is com.mongodb.MongoWriteException &&
                                        (ex.cause as com.mongodb.MongoWriteException).error.category == com.mongodb.ErrorCategory.DUPLICATE_KEY)
                        if (isDuplicate) Mono.error(IllegalArgumentException("Account with the same email already exists."))
                        else Mono.error(ex)
                    }
            }
    }

    fun updateAccount(id: String, name: String?, email: String?, password: String?): Mono<Account> {
        return accountValidationService.validateAccountForUpdate(id, name, email, password)
            .flatMap { vr ->
                if (!vr.isValid) return@flatMap Mono.error(IllegalArgumentException(vr.errorMessage ?: "validation failed"))

                accountRepository.findById(id).flatMap { existing ->
                    val updated = existing.copy(
                        name = name ?: existing.name,
                        email = email ?: existing.email,
                        password = password ?: existing.password
                    )
                    accountRepository.save(updated)
                        .onErrorResume { ex ->
                            val isDuplicate = ex is DuplicateKeyException ||
                                    (ex.cause is com.mongodb.MongoWriteException &&
                                            (ex.cause as com.mongodb.MongoWriteException).error.category == com.mongodb.ErrorCategory.DUPLICATE_KEY)
                            if (isDuplicate) Mono.error(IllegalArgumentException("Another account with same email exists."))
                            else Mono.error(ex)
                        }
                }
            }
    }

    fun deleteAccount(id: String): Mono<Boolean> {
        return accountRepository.findById(id)
            .flatMap { _ -> accountRepository.deleteById(id).then(Mono.just(true)) }
            .defaultIfEmpty(false)
    }
}