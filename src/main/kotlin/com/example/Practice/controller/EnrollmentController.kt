package com.example.practice.controller

import com.example.practice.model.Enrollment
import com.example.practice.model.User
import com.example.practice.model.Lecture
import com.example.practice.repository.EnrollmentRepository
import com.example.practice.repository.UserRepository
import com.example.practice.repository.LectureRepository
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Mono
import reactor.core.publisher.Flux
import java.time.Instant

//GraphQL controller for Enrollment queries.
@Controller
class EnrollmentController(
    private val enrollmentRepository: EnrollmentRepository
) {
    /**
     * Returns a list of all enrollments. Never returns null.
     */
    @QueryMapping
    fun enrollments(): Flux<Enrollment> = enrollmentRepository.findAll()
}

// Mutation controller for Enrollment entity
@Controller
class EnrollmentMutationController(
    private val enrollmentRepository: EnrollmentRepository
) {
    // Create a new Enrollment
    @MutationMapping
    fun createEnrollment(
        @Argument userId: String,
        @Argument lectureId: String
    ): Mono<Enrollment> {
        val enrollment = Enrollment(
            userId = userId,
            lectureId = lectureId,
            enrolledAt = Instant.now()
        )
        return enrollmentRepository.save(enrollment)
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