package com.example.practice.controller

import com.example.practice.model.Enrollment
import com.example.practice.model.User
import com.example.practice.model.Lecture
import com.example.practice.repository.EnrollmentRepository
import com.example.practice.repository.UserRepository
import com.example.practice.repository.LectureRepository
import com.example.practice.validation.ValidationService

import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Mono
import reactor.core.publisher.Flux

// GraphQL controller for Enrollment queries.
@Controller
class EnrollmentController(
    private val enrollmentRepository: EnrollmentRepository
) {
    @QueryMapping
    fun enrollments(): Flux<Enrollment> = enrollmentRepository.findAll()
}

// Mutation controller for Enrollment entity
@Controller
class EnrollmentMutationController(
    private val enrollmentRepository: EnrollmentRepository,
    private val validationService: ValidationService
) {
    @MutationMapping
    fun createEnrollment(
        @Argument userId: String,
        @Argument lectureId: String
    ): Mono<Enrollment> {
        return validationService.validateEnrollment(userId, lectureId).flatMap { validationResult ->
            if (!validationResult.isValid) {
                return@flatMap Mono.error<Enrollment>(IllegalArgumentException(validationResult.errorMessage))
            }
            val enrollment = Enrollment(userId = userId, lectureId = lectureId)
            enrollmentRepository.save(enrollment)
        }
    }

    @MutationMapping
    fun updateEnrollment(
        @Argument id: String,
        @Argument userId: String?,
        @Argument lectureId: String?
    ): Mono<Enrollment> {
        return enrollmentRepository.findById(id).flatMap { existingEnrollment ->
            val newUserId = userId ?: existingEnrollment.userId
            val newLectureId = lectureId ?: existingEnrollment.lectureId
            validationService.validateEnrollment(newUserId, newLectureId).flatMap { validationResult ->
                if (!validationResult.isValid) {
                    return@flatMap Mono.error<Enrollment>(IllegalArgumentException(validationResult.errorMessage))
                }
                val updatedEnrollment = existingEnrollment.copy(
                    userId = newUserId,
                    lectureId = newLectureId
                )
                enrollmentRepository.save(updatedEnrollment)
            }
        }
    }

    @MutationMapping
    fun deleteEnrollment(
        @Argument id: String
    ): Mono<Boolean> {
        return enrollmentRepository.existsById(id).flatMap { exists ->
            if (!exists) return@flatMap Mono.just(false)
            enrollmentRepository.deleteById(id).thenReturn(true)
        }
    }
}

@Controller
class EnrollmentFieldResolver(
    private val userRepository: UserRepository,
    private val lectureRepository: LectureRepository
) {
    @SchemaMapping(typeName = "Enrollment", field = "user")
    fun user(enrollment: Enrollment): Mono<User> {
        return userRepository.findById(enrollment.userId)
    }

    @SchemaMapping(typeName = "Enrollment", field = "lecture")
    fun lecture(enrollment: Enrollment): Mono<Lecture> {
        return lectureRepository.findById(enrollment.lectureId)
    }
}