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
    // val lessons: List<String> = emptyList(),    // List of lesson IDs associated with the article
    // TODO: attachments
    val createdAt: Instant = Instant.now(),     // Timestamp when the article was created
    val updatedAt: Instant = Instant.now()      // Timestamp for the last update to the article record
)

data class Lesson(
    val id: String? = null,                     // Unique identifier for the lesson
    val title: String,                          // Title of the lesson
    val description: String?,                   // Description of the lesson content
    //TODO: markdown and LaTeX support for content
    val content: String,                        // Content content of the lesson
    //TODO: attachments
    val createdAt: Instant = Instant.now(),     // Timestamp when the lesson was created
    val updatedAt: Instant = Instant.now()      // Timestamp for the last update to the lesson record
)