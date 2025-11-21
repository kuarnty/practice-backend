package com.example.practice.lecture.validation

import com.example.practice.lecture.repository.LectureRepository

import com.example.practice.common.validation.CommonValidation
import com.example.practice.common.validation.ValidationResult

import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class LectureValidationService(
    private val lectureRepository: LectureRepository
) {
    fun validateLectureForCreate(title: String?, description: String?, teacherId: String?): Mono<ValidationResult> {
        CommonValidation.requireNonBlank(title, "Lecture title")?.let {
            return Mono.just(ValidationResult.fail(it, mapOf("title" to it), "TITLE_REQUIRED"))
        }
        CommonValidation.lengthBetween(title, "Lecture title", 2, 100)?.let {
            return Mono.just(ValidationResult.fail(it, mapOf("title" to it), "TITLE_LENGTH"))
        }

        CommonValidation.maxLength(description, "Description", 500)?.let {
            return Mono.just(ValidationResult.fail(it, mapOf("description" to it), "DESCRIPTION_LENGTH"))
        }

        CommonValidation.requireNonBlank(teacherId, "Teacher ID")?.let {
            return Mono.just(ValidationResult.fail(it, mapOf("teacher" to it), "TEACHER_ID_REQUIRED"))
        }
        CommonValidation.lengthBetween(teacherId, "Teacher ID", 2, 30)?.let {
            return Mono.just(ValidationResult.fail(it, mapOf("teacher" to it), "TEACHER_ID_LENGTH"))
        }

        return lectureRepository.existsByTitle(title!!).map { exists ->
            if (exists) ValidationResult.fail("Lecture title already exists.", mapOf("title" to "exists"), "TITLE_EXISTS")
            else ValidationResult.OK
        }
    }

    fun validateLectureForUpdate(currentId: String, title: String?, description: String?, teacherId: String?): Mono<ValidationResult> {
        CommonValidation.requireNonBlank(title, "Lecture title")?.let {
            return Mono.just(ValidationResult.fail(it, mapOf("title" to it), "TITLE_REQUIRED"))
        }
        CommonValidation.lengthBetween(title, "Lecture title", 2, 100)?.let {
            return Mono.just(ValidationResult.fail(it, mapOf("title" to it), "TITLE_LENGTH"))
        }

        CommonValidation.maxLength(description, "Description", 500)?.let {
            return Mono.just(ValidationResult.fail(it, mapOf("description" to it), "DESCRIPTION_LENGTH"))
        }

        CommonValidation.requireNonBlank(teacherId, "Teacher ID")?.let {
            return Mono.just(ValidationResult.fail(it, mapOf("teacher" to it), "TEACHER_ID_REQUIRED"))
        }
        CommonValidation.lengthBetween(teacherId, "Teacher ID", 2, 30)?.let {
            return Mono.just(ValidationResult.fail(it, mapOf("teacher" to it), "TEACHER_ID_LENGTH"))
        }

        return lectureRepository.existsByTitleAndIdNot(title!!, currentId).map { conflict ->
            if (conflict) ValidationResult.fail("Lecture title already exists.", mapOf("title" to "exists"), "TITLE_EXISTS")
            else ValidationResult.OK
        }
    }
}