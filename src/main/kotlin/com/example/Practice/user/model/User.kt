package com.example.practice.user.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

/**
 * User entity for storing user information in MongoDB.
 */
@Document(collection = "users")
data class User(
    @Id val id: String? = null,
    val name: String,
    val email: String,
    //TODO: Use hashed password
    val password: String,
    val createdAt: Instant = Instant.now()
)

data class CreateUserInput(
    val name: String,
    val email: String,
    val password: String
)

data class UpdateUserInput(
    val id: String,
    val name: String?,
    val email: String?,
    val password: String?
)

data class UserSummary(
    val id: String,
    val name: String,
    val email: String
)