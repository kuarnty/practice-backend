package com.example.practice.article.api

import com.example.practice.article.model.Article
import com.example.practice.article.service.ArticleService

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

    @MutationMapping
    fun createArticle(
        @Argument title: String,
        @Argument description: String?,
        @Argument authorId: String,
        @Argument content: String?
    ): Mono<Article> = articleService.createArticle(title, description, authorId, content)

    @MutationMapping
    fun updateArticle(
        @Argument id: String,
        @Argument title: String?,
        @Argument description: String?,
        @Argument authorId: String?,
        @Argument content: String?
    ): Mono<Article> = articleService.updateArticle(id, title, description, authorId, content)

    @MutationMapping
    fun deleteArticle(@Argument id: String): Mono<Boolean> = articleService.deleteArticle(id)
}