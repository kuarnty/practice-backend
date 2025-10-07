package com.example.practice.controller

import com.example.practice.model.Lecture
import com.example.practice.repository.LectureRepository
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.stereotype.Controller
import reactor.core.publisher.Mono
import reactor.core.publisher.Flux
import java.time.Instant

 //GraphQL controller for Lecture queries.
@Controller
class LectureController(
    private val lectureRepository: LectureRepository
) {
    /**
     * Returns a list of all lectures. Never returns null.
     */
    @QueryMapping
    fun lectures(): Flux<Lecture> = lectureRepository.findAll()
}

// Mutation controller for Lecture entity
@Controller
class LectureMutationController(
    private val lectureRepository: LectureRepository
) {
    // Create a new Lecture
    @MutationMapping
    fun createLecture(
        @Argument title: String,
        @Argument description: String,
        @Argument instructor: String
    ): Mono<Lecture> {
        val lecture = Lecture(
            title = title,
            description = description,
            instructor = instructor,
            createdAt = Instant.now()
        )
        return lectureRepository.save(lecture)
    }
    @MutationMapping
    fun updateLecture(
        @Argument id: String,
        @Argument title: String?,
        @Argument description: String?,
        @Argument instructor: String?
    ): Mono<Lecture> {
        return lectureRepository.findById(id).flatMap { lecture ->
            val updated = lecture.copy(
                title = title ?: lecture.title,
                description = description ?: lecture.description,
                instructor = instructor ?: lecture.instructor
            )
            lectureRepository.save(updated)
        }
    }

    @MutationMapping
    fun deleteLecture(@Argument id: String): Mono<Boolean> {
        return lectureRepository.deleteById(id).thenReturn(true)
    }
}