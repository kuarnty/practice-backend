package com.example.practice.controller

import com.example.practice.model.Lecture
import com.example.practice.repository.LectureRepository
import com.example.practice.validation.ValidationService

import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.stereotype.Controller
import reactor.core.publisher.Mono
import reactor.core.publisher.Flux

// GraphQL controller for Lecture queries.
@Controller
class LectureController(
    private val lectureRepository: LectureRepository
) {
    @QueryMapping
    fun lectures(): Flux<Lecture> = lectureRepository.findAll()
}

// Mutation controller for Lecture entity
@Controller
class LectureMutationController(
    private val lectureRepository: LectureRepository,
    private val validationService: ValidationService
) {
    @MutationMapping
    fun createLecture(
        @Argument title: String,
        @Argument description: String?,
        @Argument instructor: String
    ): Mono<Lecture> {
        return validationService.validateLecture(title, description, instructor).flatMap { validationResult ->
            if (!validationResult.isValid) {
                return@flatMap Mono.error<Lecture>(IllegalArgumentException(validationResult.errorMessage))
            }
            val lecture = Lecture(title = title, description = description, instructor = instructor)
            lectureRepository.save(lecture)
        }
    }

    @MutationMapping
    fun updateLecture(
        @Argument id: String,
        @Argument title: String?,
        @Argument description: String?,
        @Argument instructor: String?
    ): Mono<Lecture> {
        return lectureRepository.findById(id).flatMap { existingLecture ->
            val newTitle = title ?: existingLecture.title
            val newDescription = description ?: existingLecture.description
            val newInstructor = instructor ?: existingLecture.instructor
            validationService.validateLecture(newTitle, newDescription, newInstructor).flatMap { validationResult ->
                if (!validationResult.isValid) {
                    return@flatMap Mono.error<Lecture>(IllegalArgumentException(validationResult.errorMessage))
                }
                val updatedLecture = existingLecture.copy(
                    title = newTitle,
                    description = newDescription,
                    instructor = newInstructor
                )
                lectureRepository.save(updatedLecture)
            }
        }
    }

    @MutationMapping
    fun deleteLecture(
        @Argument id: String
    ): Mono<Boolean> {
        return lectureRepository.existsById(id).flatMap { exists ->
            if (!exists) return@flatMap Mono.just(false)
            lectureRepository.deleteById(id).thenReturn(true)
        }
    }
}