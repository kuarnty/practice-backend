package com.example.practice.enrollment.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

/**
 * Enrollment entity for tracking which users are enrolled in which lectures.
 */
@Document(collection = "enrollments")
data class Enrollment(
    @Id val id: String? = null,                 // Unique identifier for the enrollment record
    val userId: String,                         // Reference to the enrolled user's ID
    val lectureId: String,                      // Reference to the lecture's ID
    val progress: Float = 0.0f,                 // Progress percentage of the lecture
    val grade: String? = null,                  // Grade achieved in the lecture, if applicable
    val enrolledAt: Instant = Instant.now(),    // Timestamp when the enrollment occurred
    val updatedAt: Instant = Instant.now()      // Timestamp for the last update to the enrollment record
    )