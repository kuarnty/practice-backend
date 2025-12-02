package com.example.practice.article.repository

import com.example.practice.article.model.Article

import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono

interface ArticleRepository : ReactiveMongoRepository<Article, String> {
    fun existsByTitle(title: String): Mono<Boolean>
    fun existsByTitleAndIdNot(title: String, id: String): Mono<Boolean>
}