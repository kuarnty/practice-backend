package com.example.practice.account.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

/**
 * Account entity for storing account information in MongoDB.
 */
@Document(collection = "accounts")
data class Account(
    @Id val id: String? = null,                 // Unique identifier for the account
    val name: String,                           // Name for display
    val email: String,                          // Account's email address, used for login
    val password: String,                       // Account's password (should be hashed in production)
    val createdAt: Instant = Instant.now()      // Timestamp when the account was created
)