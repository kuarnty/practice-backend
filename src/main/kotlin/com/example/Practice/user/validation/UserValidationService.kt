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

    fun validateUserForCreate(name: String?, email: String?, password: String?): Mono<ValidationResult> {
        // synchronous checks using CommonValidation
        CommonValidation.requireNonBlank(name, "Name")?.let {
            return Mono.just(ValidationResult.fail(it, mapOf("name" to it), "NAME_REQUIRED"))
        }
        CommonValidation.lengthBetween(name, "Name", 2, 20)?.let {
            return Mono.just(ValidationResult.fail(it, mapOf("name" to it), "NAME_LENGTH"))
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
        return userRepository.existsByName(name!!).flatMap { existsByName ->
            if (existsByName) {
                Mono.just(ValidationResult.fail("Name already exists.", mapOf("name" to "exists"), "NAME_EXISTS"))
            } else {
                userRepository.existsByEmail(email!!).map { existsByEmail ->
                    if (existsByEmail) ValidationResult.fail("Email already exists.", mapOf("email" to "exists"), "EMAIL_EXISTS")
                    else ValidationResult.OK
                }
            }
        }
    }

    fun validateUserForUpdate(currentId: String, name: String?, email: String?, password: String?): Mono<ValidationResult> {
        CommonValidation.requireNonBlank(name, "Name")?.let {
            return Mono.just(ValidationResult.fail(it, mapOf("name" to it), "NAME_REQUIRED"))
        }
        CommonValidation.lengthBetween(name, "Name", 2, 20)?.let {
            return Mono.just(ValidationResult.fail(it, mapOf("name" to it), "NAME_LENGTH"))
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
        return userRepository.existsByNameAndIdNot(name!!, currentId).flatMap { nameConflict ->
            if (nameConflict) {
                Mono.just(ValidationResult.fail("Name already exists.", mapOf("name" to "exists"), "NAME_EXISTS"))
            } else {
                userRepository.existsByEmailAndIdNot(email!!, currentId).map { emailConflict ->
                    if (emailConflict) ValidationResult.fail("Email already exists.", mapOf("email" to "exists"), "EMAIL_EXISTS")
                    else ValidationResult.OK
                }
            }
        }
    }
}