package com.example.practice.article.api

import com.example.practice.article.model.Article
import com.example.practice.article.service.ArticleService
import com.example.practice.article.model.ArticleSummary
import com.example.practice.article.model.CreateArticleInput
import com.example.practice.article.model.UpdateArticleInput

import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Controller
class ArticleController(private val articleService: ArticleService) {

    @QueryMapping(name = "articles")
    fun articles(): Flux<Article> = articleService.findAll()

    @QueryMapping(name = "article")
    fun findById(@Argument id: String): Mono<Article> = articleService.findById(id)

    @QueryMapping(name = "articleSummaries")
    fun findArticleSummaries(): Flux<ArticleSummary> = articleService.findAllArticleSummaries()

    @QueryMapping(name = "articleSummary")
    fun findArticleSummaryById(@Argument id: String): Mono<ArticleSummary> = articleService.findArticleSummaryById(id)

    @MutationMapping
    fun createArticle(@Argument createArticleInput: CreateArticleInput): Mono<Boolean> = articleService.createArticle(createArticleInput)

    @MutationMapping
    fun updateArticle(
        @Argument updateArticleInput: UpdateArticleInput,
        principal: org.springframework.security.core.Authentication?
        ): Mono<Article> {
            if (principal == null || !principal.isAuthenticated)
                return Mono.error(IllegalAccessException("Authentication required"))

            return articleService.updateArticle(updateArticleInput)
        }

    @MutationMapping
    fun deleteArticle(
        @Argument id: String,
        principal: org.springframework.security.core.Authentication?
    ): Mono<Boolean> {        
        if (principal == null || !principal.isAuthenticated)
            return Mono.error(IllegalAccessException("Authentication required"))

        return articleService.deleteArticle(id)
    }
}