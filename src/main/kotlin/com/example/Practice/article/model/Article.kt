package com.example.practice.article.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

/**
 * Article entity for storing 
 */
@Document(collection = "articles")
data class Article(
    @Id val id: String? = null,                 // Unique identifier for the article
    val title: String,                          // Title of the article
    val description: String?,                   // Description of the article content
    val authorId: String,                       // ID of the author(user)
    val content: String?,                        // Content content of the article
    // TODO: attachments
    val createdAt: Instant = Instant.now(),     // Timestamp when the article was created
    val updatedAt: Instant = Instant.now()      // Timestamp for the last update to the article record
)

data class ArticleSummary(
    val id: String,
    val title: String,
    val authorName: String
)

data class CreateArticleInput(
    val title: String,
    val description: String?,
    val authorId: String,
    val content: String?
)

data class UpdateArticleInput(
    val id: String,
    val title: String?,
    val description: String?,
    val authorId: String?,
    val content: String?
)