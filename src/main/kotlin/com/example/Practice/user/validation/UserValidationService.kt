package com.example.practice.user.validation

import com.example.practice.user.repository.UserRepository

import com.example.practice.common.validation.CommonValidation
import com.example.practice.common.validation.ValidationResult

import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class UserValidationService(
    private val userRepository: UserRepository
) {
    private val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun validateUserForCreate(username: String?, email: String?, password: String?): Mono<ValidationResult> {
        // synchronous checks using CommonValidation
        CommonValidation.requireNonBlank(username, "Username")?.let {
            return Mono.just(ValidationResult.fail(it, mapOf("username" to it), "USERNAME_REQUIRED"))
        }
        CommonValidation.lengthBetween(username, "Username", 2, 20)?.let {
            return Mono.just(ValidationResult.fail(it, mapOf("username" to it), "USERNAME_LENGTH"))
        }

        CommonValidation.requireNonBlank(email, "Email")?.let {
            return Mono.just(ValidationResult.fail(it, mapOf("email" to it), "EMAIL_REQUIRED"))
        }
        CommonValidation.maxLength(email, "Email", 50)?.let {
            return Mono.just(ValidationResult.fail(it, mapOf("email" to it), "EMAIL_LENGTH"))
        }
        CommonValidation.matchesRegex(email, "Email", emailRegex, "Email format is invalid.")?.let {
            return Mono.just(ValidationResult.fail(it, mapOf("email" to it), "EMAIL_FORMAT"))
        }

        CommonValidation.requireNonBlank(password, "Password")?.let {
            return Mono.just(ValidationResult.fail(it, mapOf("password" to it), "PASSWORD_REQUIRED"))
        }
        CommonValidation.lengthBetween(password, "Password", 8, 50)?.let {
            return Mono.just(ValidationResult.fail(it, mapOf("password" to it), "PASSWORD_LENGTH"))
        }

        // asynchronous uniqueness checks
        return userRepository.existsByUsername(username!!).flatMap { existsByUsername ->
            if (existsByUsername) {
                Mono.just(ValidationResult.fail("Username already exists.", mapOf("username" to "exists"), "USERNAME_EXISTS"))
            } else {
                userRepository.existsByEmail(email!!).map { existsByEmail ->
                    if (existsByEmail) ValidationResult.fail("Email already exists.", mapOf("email" to "exists"), "EMAIL_EXISTS")
                    else ValidationResult.OK
                }
            }
        }
    }

    fun validateUserForUpdate(currentId: String, username: String?, email: String?, password: String?): Mono<ValidationResult> {
        CommonValidation.requireNonBlank(username, "Username")?.let {
            return Mono.just(ValidationResult.fail(it, mapOf("username" to it), "USERNAME_REQUIRED"))
        }
        CommonValidation.lengthBetween(username, "Username", 2, 20)?.let {
            return Mono.just(ValidationResult.fail(it, mapOf("username" to it), "USERNAME_LENGTH"))
        }

        CommonValidation.requireNonBlank(email, "Email")?.let {
            return Mono.just(ValidationResult.fail(it, mapOf("email" to it), "EMAIL_REQUIRED"))
        }
        CommonValidation.maxLength(email, "Email", 50)?.let {
            return Mono.just(ValidationResult.fail(it, mapOf("email" to it), "EMAIL_LENGTH"))
        }
        CommonValidation.matchesRegex(email, "Email", emailRegex, "Email format is invalid.")?.let {
            return Mono.just(ValidationResult.fail(it, mapOf("email" to it), "EMAIL_FORMAT"))
        }

        CommonValidation.requireNonBlank(password, "Password")?.let {
            return Mono.just(ValidationResult.fail(it, mapOf("password" to it), "PASSWORD_REQUIRED"))
        }
        CommonValidation.lengthBetween(password, "Password", 8, 50)?.let {
            return Mono.just(ValidationResult.fail(it, mapOf("password" to it), "PASSWORD_LENGTH"))
        }

        // asynchronous uniqueness checks excluding currentId
        return userRepository.existsByUsernameAndIdNot(username!!, currentId).flatMap { usernameConflict ->
            if (usernameConflict) {
                Mono.just(ValidationResult.fail("Username already exists.", mapOf("username" to "exists"), "USERNAME_EXISTS"))
            } else {
                userRepository.existsByEmailAndIdNot(email!!, currentId).map { emailConflict ->
                    if (emailConflict) ValidationResult.fail("Email already exists.", mapOf("email" to "exists"), "EMAIL_EXISTS")
                    else ValidationResult.OK
                }
            }
        }
    }
}