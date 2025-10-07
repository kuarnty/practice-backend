package com.example.practice.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

/**
 * Lecture entity for storing course/lecture information.
 */
@Document(collection = "lectures")
data class Lecture(
    @Id val id: String? = null,                // Unique identifier for the lecture
    val title: String,                         // Title of the lecture
    val description: String,                   // Description of the lecture content
    val instructor: String,                    // Name of the instructor
    val createdAt: Instant = Instant.now()     // Timestamp when the lecture was created
)