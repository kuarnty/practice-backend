package com.example.practice.enrollment.api

import com.example.practice.enrollment.model.Enrollment
import com.example.practice.enrollment.service.EnrollmentService
import com.example.practice.user.model.User
import com.example.practice.user.service.UserService
import com.example.practice.lecture.model.Lecture
import com.example.practice.lecture.service.LectureService

import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Controller
class EnrollmentController(
    private val enrollmentService: EnrollmentService,
    private val userService: UserService,
    private val lectureService: LectureService
) {

    @QueryMapping(name = "enrollments")
    fun enrollments(): Flux<Enrollment> = enrollmentService.findAll()

    @QueryMapping(name = "enrollment")
    fun findById(@Argument id: String): Mono<Enrollment> = enrollmentService.findById(id)

    @MutationMapping
    fun createEnrollment(@Argument userId: String?, @Argument lectureId: String?): Mono<Enrollment> =
        enrollmentService.createEnrollment(userId, lectureId)

    @MutationMapping
    fun updateEnrollment(@Argument id: String, @Argument userId: String?, @Argument lectureId: String?, @Argument progress: Float?, @Argument grade: String?): Mono<Enrollment> =
        enrollmentService.updateEnrollment(id, userId, lectureId, progress, grade)

    @MutationMapping
    fun deleteEnrollment(@Argument id: String): Mono<Boolean> =
        enrollmentService.deleteEnrollment(id)

    @SchemaMapping(typeName = "Enrollment", field = "user")
    fun user(enrollment: Enrollment): Mono<User> = userService.findById(enrollment.userId)

    @SchemaMapping(typeName = "Enrollment", field = "lecture")
    fun lecture(enrollment: Enrollment): Mono<Lecture> = lectureService.findById(enrollment.lectureId)
}