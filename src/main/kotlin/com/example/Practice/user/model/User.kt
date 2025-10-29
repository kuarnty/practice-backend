package com.example.practice.user.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

/**
 * User entity for storing user information in MongoDB.
 */
@Document(collection = "users")
data class User(
    @Id val id: String? = null,                // Unique identifier for the user
    val username: String,                      // Username for login and display
    val email: String,                         // User's email address
    val password: String,                      // User's password (should be hashed in production)
    val createdAt: Instant = Instant.now()     // Timestamp when the user account was created
)