package com.example.practice.article.validation

import com.example.practice.article.repository.ArticleRepository

import com.example.practice.common.validation.CommonValidation
import com.example.practice.common.validation.ValidationResult

import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ArticleValidationService(
    private val articleRepository: ArticleRepository
) {
    fun validateArticleForCreate(title: String?, description: String?, userId: String?): Mono<ValidationResult> {
        CommonValidation.requireNonBlank(title, "Article title")?.let {
            return Mono.just(ValidationResult.fail(it, mapOf("title" to it), "TITLE_REQUIRED"))
        }
        CommonValidation.lengthBetween(title, "Article title", 2, 100)?.let {
            return Mono.just(ValidationResult.fail(it, mapOf("title" to it), "TITLE_LENGTH"))
        }

        CommonValidation.maxLength(description, "Description", 500)?.let {
            return Mono.just(ValidationResult.fail(it, mapOf("description" to it), "DESCRIPTION_LENGTH"))
        }

        CommonValidation.requireNonBlank(userId, "User ID")?.let {
            return Mono.just(ValidationResult.fail(it, mapOf("user" to it), "USER_ID_REQUIRED"))
        }
        CommonValidation.lengthBetween(userId, "User ID", 2, 30)?.let {
            return Mono.just(ValidationResult.fail(it, mapOf("user" to it), "USER_ID_LENGTH"))
        }

        return articleRepository.existsByTitle(title!!).map { exists ->
            if (exists) ValidationResult.fail("Article title already exists.", mapOf("title" to "exists"), "TITLE_EXISTS")
            else ValidationResult.OK
        }
    }

    fun validateArticleForUpdate(currentId: String, title: String?, description: String?, userId: String?): Mono<ValidationResult> {
        CommonValidation.requireNonBlank(title, "Article title")?.let {
            return Mono.just(ValidationResult.fail(it, mapOf("title" to it), "TITLE_REQUIRED"))
        }
        CommonValidation.lengthBetween(title, "Article title", 2, 100)?.let {
            return Mono.just(ValidationResult.fail(it, mapOf("title" to it), "TITLE_LENGTH"))
        }

        CommonValidation.maxLength(description, "Description", 500)?.let {
            return Mono.just(ValidationResult.fail(it, mapOf("description" to it), "DESCRIPTION_LENGTH"))
        }

        CommonValidation.requireNonBlank(userId, "User ID")?.let {
            return Mono.just(ValidationResult.fail(it, mapOf("user" to it), "USER_ID_REQUIRED"))
        }
        CommonValidation.lengthBetween(userId, "User ID", 2, 30)?.let {
            return Mono.just(ValidationResult.fail(it, mapOf("user" to it), "USER_ID_LENGTH"))
        }

        return articleRepository.existsByTitleAndIdNot(title!!, currentId).map { conflict ->
            if (conflict) ValidationResult.fail("Article title already exists.", mapOf("title" to "exists"), "TITLE_EXISTS")
            else ValidationResult.OK
        }
    }
}