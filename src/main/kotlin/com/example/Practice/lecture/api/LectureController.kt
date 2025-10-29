package com.example.practice.lecture.controller

import com.example.practice.lecture.model.Lecture
import com.example.practice.lecture.repository.LectureRepository
import com.example.practice.lecture.validation.LectureValidationService

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
    private val lectureValidationService: LectureValidationService
) {
    @MutationMapping
    fun createLecture(
        @Argument title: String,
        @Argument description: String?,
        @Argument instructor: String
    ): Mono<Lecture> {
        return lectureValidationService.validateLectureForCreate(title, description, instructor).flatMap { validationResult ->
            if (!validationResult.isValid) {
                Mono.error<Lecture>(IllegalArgumentException(validationResult.errorMessage))
            }
            else {
                val lecture = Lecture(title = title, description = description, instructor = instructor)
                lectureRepository.save(lecture)
            }
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
            lectureValidationService.validateLectureForUpdate(id, newTitle, newDescription, newInstructor).flatMap { validationResult ->
                if (!validationResult.isValid) {
                    Mono.error<Lecture>(IllegalArgumentException(validationResult.errorMessage))
                }
                else {
                    val updatedLecture = existingLecture.copy(
                        title = newTitle,
                        description = newDescription,
                        instructor = newInstructor
                    )
                    lectureRepository.save(updatedLecture)
                }
            }
        }
    }

    @MutationMapping
    fun deleteLecture(
        @Argument id: String
    ): Mono<Boolean> {
        return lectureRepository.existsById(id).flatMap { exists ->
            if (!exists) Mono.just(false)
            else lectureRepository.deleteById(id).thenReturn(true)
        }
    }
}