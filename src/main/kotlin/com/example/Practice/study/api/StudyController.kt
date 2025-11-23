package com.example.practice.study.api

import com.example.practice.study.model.Study
import com.example.practice.study.service.StudyService
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
class StudyController(
    private val studyService: StudyService,
    private val userService: UserService,
    private val lectureService: LectureService
) {

    @QueryMapping(name = "studies")
    fun studies(): Flux<Study> = studyService.findAll()

    @QueryMapping(name = "study")
    fun findById(@Argument id: String): Mono<Study> = studyService.findById(id)

    @MutationMapping
    fun createStudy(
        @Argument userId: String, 
        @Argument lectureId: String
    ): Mono<Study> = studyService.createStudy(userId, lectureId)

    @MutationMapping
    fun updatestudy(
        @Argument id: String, 
        @Argument userId: String?, 
        @Argument lectureId: String?, 
        @Argument progress: Float?, 
        @Argument grade: String?
    ): Mono<Study> = studyService.updateStudy(id, userId, lectureId, progress, grade)

    @MutationMapping
    fun deleteStudy(@Argument id: String): Mono<Boolean> = studyService.deleteStudy(id)

    @SchemaMapping(typeName = "study", field = "user")
    fun user(study: Study): Mono<User> = userService.findById(study.userId)

    @SchemaMapping(typeName = "study", field = "lecture")
    fun lecture(study: Study): Mono<Lecture> = lectureService.findById(study.lectureId)
}