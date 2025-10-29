package com.example.practice.lecture.api

import com.example.practice.lecture.model.Lecture
import com.example.practice.lecture.service.LectureService

import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Controller
class LectureController(private val lectureService: LectureService) {

    @QueryMapping(name = "lectures")
    fun lectures(): Flux<Lecture> = lectureService.findAll()

    @QueryMapping(name = "lecture")
    fun findById(@Argument id: String): Mono<Lecture> = lectureService.findById(id)

    @MutationMapping
    fun createLecture(
        @Argument title: String?,
        @Argument description: String?,
        @Argument instructor: String
    ): Mono<Lecture> = lectureService.createLecture(title, description, instructor)

    @MutationMapping
    fun updateLecture(
        @Argument id: String,
        @Argument title: String?,
        @Argument description: String?,
        @Argument instructor: String?
    ): Mono<Lecture> = lectureService.updateLecture(id, title, description, instructor)

    @MutationMapping
    fun deleteLecture(@Argument id: String): Mono<Boolean> = lectureService.deleteLecture(id)
}