package com.example.practice.article.service

import com.example.practice.article.model.Article
import com.example.practice.article.repository.ArticleRepository
import com.example.practice.article.validation.ArticleValidationService

import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant

@Service
class ArticleService(
    private val articleRepository: ArticleRepository,
    private val articleValidationService: ArticleValidationService
) {
    
    fun findAll(): Flux<Article> = articleRepository.findAll()

    fun findById(id: String): Mono<Article> = articleRepository.findById(id)

    fun createArticle(title: String, description: String?, authorId: String, content: String?): Mono<Article> {
        return articleValidationService.validateArticleForCreate(title, description, authorId)
            .flatMap { vr ->
                if (!vr.isValid) return@flatMap Mono.error(IllegalArgumentException(vr.errorMessage ?: "validation failed"))

                val now = Instant.now()
                val article = Article(
                    id = null,
                    title = title,
                    description = description,
                    authorId = authorId,
                    content = content,
                    createdAt = now,
                    updatedAt = now
                )

                articleRepository.save(article)
                    .onErrorResume { ex ->
                        val isDuplicate = ex is DuplicateKeyException ||
                                (ex.cause is com.mongodb.MongoWriteException &&
                                        (ex.cause as com.mongodb.MongoWriteException).error.category == com.mongodb.ErrorCategory.DUPLICATE_KEY)
                        if (isDuplicate) Mono.error(IllegalArgumentException("Article with same title already exists."))
                        else Mono.error(ex)
                    }
            }
    }

    fun updateArticle(id: String, title: String?, description: String?, authorId: String?, content: String?): Mono<Article> {
        return articleValidationService.validateArticleForUpdate(id, title, description, authorId)
            .flatMap { vr ->
                if (!vr.isValid) return@flatMap Mono.error(IllegalArgumentException(vr.errorMessage ?: "validation failed"))

                articleRepository.findById(id).flatMap { existing ->
                    val updated = existing.copy(
                        title = title ?: existing.title,
                        description = description ?: existing.description,
                        authorId = authorId ?: existing.authorId,
                        content = content ?: existing.content,
                        updatedAt = Instant.now()
                    )
                    articleRepository.save(updated)
                        .onErrorResume { ex ->
                            val isDuplicate = ex is DuplicateKeyException ||
                                    (ex.cause is com.mongodb.MongoWriteException &&
                                            (ex.cause as com.mongodb.MongoWriteException).error.category == com.mongodb.ErrorCategory.DUPLICATE_KEY)
                            if (isDuplicate) Mono.error(IllegalArgumentException("Another article with same title exists."))
                            else Mono.error(ex)
                        }
                }
            }
    }

    fun deleteArticle(id: String): Mono<Boolean> {
        return articleRepository.findById(id)
            .flatMap { _ -> articleRepository.deleteById(id).then(Mono.just(true)) }
            .defaultIfEmpty(false)
    }
}