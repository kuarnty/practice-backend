package com.example.practice.article.service

import com.example.practice.article.model.Article
import com.example.practice.article.model.ArticleSummary
import com.example.practice.article.model.CreateArticleInput
import com.example.practice.article.model.UpdateArticleInput
import com.example.practice.article.repository.ArticleRepository
import com.example.practice.article.validation.ArticleValidationService
import com.example.practice.user.repository.UserRepository

import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant

@Service
class ArticleService(
    private val articleRepository: ArticleRepository,
    private val articleValidationService: ArticleValidationService,
    private val userRepository: UserRepository
) {
    

    fun findAll(): Flux<Article> {
        return articleRepository.findAll()
            .switchIfEmpty(Flux.error(NoSuchElementException("find error - No articles found")))
            .onErrorResume { ex -> Mono.error(ex) }
    }

    fun findById(id: String): Mono<Article> {
        return articleRepository.findById(id)
            .switchIfEmpty(Mono.error(NoSuchElementException("find error - Article not found with id: $id")))
            .onErrorResume { ex -> Mono.error(ex) }
    }

    fun findAllArticleSummaries(): Flux<ArticleSummary> {
        return articleRepository.findAll()
            .flatMap { article -> 
                userRepository.findById(article.authorId)
                    .map { user -> ArticleSummary(requireNotNull(article.id), article.title, user.name) }
                    .switchIfEmpty(Mono.just(ArticleSummary(requireNotNull(article.id), article.title, "user not found")))
            }
            .switchIfEmpty(Flux.error(NoSuchElementException("find error - No articles found")))
            .onErrorResume { ex -> Mono.error(ex) }
    }

    fun findArticleSummaryById(id: String): Mono<ArticleSummary> {
        return articleRepository.findById(id)
            .flatMap { article -> 
                userRepository.findById(article.authorId)
                .flatMap { user -> Mono.just(ArticleSummary(requireNotNull(article.id), article.title, user.name)) }
                .switchIfEmpty(Mono.error(NoSuchElementException("find error - Article not found with id: $id")))
            }
            .switchIfEmpty(Mono.error(NoSuchElementException("find error - Article not found with id: $id")))
            .onErrorResume { ex -> Mono.error(ex) }
    }

    fun createArticle(createArticleInput: CreateArticleInput): Mono<Boolean> {
        return articleValidationService.validateArticleForCreate(createArticleInput)
            .flatMap { vr ->
                if (!vr.isValid) return@flatMap Mono.error(IllegalArgumentException(vr.errorMessage ?: "validation failed"))

                val now = Instant.now()
                val article = Article(
                    id = null,
                    title = createArticleInput.title,
                    description = createArticleInput.description ?: "",
                    authorId = createArticleInput.authorId,
                    content = createArticleInput.content ?: "",
                    createdAt = now,
                    updatedAt = now
                )

                articleRepository.save(article)
                    .then(Mono.just(true))
                    .onErrorResume { ex -> Mono.error(ex) }
            }
    }

    fun updateArticle(updateArticleInput: UpdateArticleInput): Mono<Article> {
        return articleValidationService.validateArticleForUpdate(updateArticleInput)
            .flatMap { vr ->
                if (!vr.isValid) return@flatMap Mono.error(IllegalArgumentException(vr.errorMessage ?: "validation failed"))

                articleRepository.findById(updateArticleInput.id)
                .flatMap { existing ->
                    val updated = existing.copy(
                        title = updateArticleInput.title ?: existing.title,
                        description = updateArticleInput.description ?: existing.description,
                        authorId = updateArticleInput.authorId ?: existing.authorId,
                        content = updateArticleInput.content ?: existing.content,
                        updatedAt = Instant.now()
                        )
                        articleRepository.save(updated)
                            .onErrorResume { ex -> Mono.error(ex) }
                }
                .switchIfEmpty(Mono.error(NoSuchElementException("update error - Article not found with id: ${updateArticleInput.id}")))
                .onErrorResume { ex -> Mono.error(ex) }
            }
    }

    fun deleteArticle(id: String): Mono<Boolean> {
        return articleRepository.deleteById(id)
            .then(Mono.just(true))
            .switchIfEmpty(Mono.error(NoSuchElementException("delete error - Article not found with id: $id")))
            .onErrorResume { ex -> Mono.error(ex) }
    }
}