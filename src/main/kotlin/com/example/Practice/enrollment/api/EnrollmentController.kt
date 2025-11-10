package com.example.practice.enrollment.api

import com.example.practice.enrollment.model.Enrollment
import com.example.practice.enrollment.service.EnrollmentService
import com.example.practice.account.model.Account
import com.example.practice.account.service.AccountService
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
    private val accountService: AccountService,
    private val lectureService: LectureService
) {

    @QueryMapping(name = "enrollments")
    fun enrollments(): Flux<Enrollment> = enrollmentService.findAll()

    @QueryMapping(name = "enrollment")
    fun findById(@Argument id: String): Mono<Enrollment> = enrollmentService.findById(id)

    @MutationMapping
    fun createEnrollment(@Argument accountId: String, @Argument lectureId: String): Mono<Enrollment> =
        enrollmentService.createEnrollment(accountId, lectureId)

    @MutationMapping
    fun updateEnrollment(@Argument id: String, @Argument accountId: String?, @Argument lectureId: String?, @Argument progress: Float?, @Argument grade: String?): Mono<Enrollment> =
        enrollmentService.updateEnrollment(id, accountId, lectureId, progress, grade)

    @MutationMapping
    fun deleteEnrollment(@Argument id: String): Mono<Boolean> =
        enrollmentService.deleteEnrollment(id)

    @SchemaMapping(typeName = "Enrollment", field = "account")
    fun account(enrollment: Enrollment): Mono<Account> = accountService.findById(enrollment.accountId)

    @SchemaMapping(typeName = "Enrollment", field = "lecture")
    fun lecture(enrollment: Enrollment): Mono<Lecture> = lectureService.findById(enrollment.lectureId)
}