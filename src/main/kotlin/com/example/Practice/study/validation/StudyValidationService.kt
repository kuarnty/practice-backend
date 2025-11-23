package com.example.practice.study.validation

import com.example.practice.study.repository.StudyRepository
import com.example.practice.lecture.repository.LectureRepository
import com.example.practice.user.repository.UserRepository

import com.example.practice.common.validation.CommonValidation
import com.example.practice.common.validation.ValidationResult

import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class StudyValidationService(
    private val userRepository: UserRepository,
    private val lectureRepository: LectureRepository,
    private val studyRepository: StudyRepository
) {
    fun validateStudyForCreate(userId: String, lectureId: String): Mono<ValidationResult> {
        CommonValidation.requireNonBlank(userId, "User ID")?.let {
            return Mono.just(ValidationResult.fail(it, mapOf("userId" to it), "USER_ID_REQUIRED"))
        }

        // check user existence
        return userRepository.existsById(userId).flatMap { userExists ->
            if (!userExists) Mono.just(ValidationResult.fail("User not found.", mapOf("userId" to "not_found"), "USER_NOT_FOUND"))
            else {
                CommonValidation.requireNonBlank(lectureId, "Lecture ID")?.let {
                    return@flatMap Mono.just(ValidationResult.fail(it, mapOf("lectureId" to it), "LECTURE_ID_REQUIRED"))
                }
                lectureRepository.existsById(lectureId).flatMap { lectureExists ->
                    if (!lectureExists) Mono.just(ValidationResult.fail("Lecture not found.", mapOf("lectureId" to "not_found"), "LECTURE_NOT_FOUND"))
                    else {
                        studyRepository.existsByUserIdAndLectureId(userId, lectureId).map { enrolled ->
                            if (enrolled) ValidationResult.fail("User is already enrolled in this lecture.", mapOf("userId" to "already_enrolled"), "ALREADY_ENROLLED")
                            else ValidationResult.OK
                        }
                    }
                }
            }
        }
    }

    fun validateStudyForUpdate(currentId: String, userId: String?, lectureId: String?): Mono<ValidationResult> {
        CommonValidation.requireNonBlank(userId, "User ID")?.let {
            return Mono.just(ValidationResult.fail(it, mapOf("userId" to it), "USER_ID_REQUIRED"))
        }

        return userRepository.existsById(userId!!).flatMap { userExists ->
            if (!userExists) Mono.just(ValidationResult.fail("User not found.", mapOf("userId" to "not_found"), "USER_NOT_FOUND"))
            else {
                CommonValidation.requireNonBlank(lectureId, "Lecture ID")?.let {
                    return@flatMap Mono.just(ValidationResult.fail(it, mapOf("lectureId" to it), "LECTURE_ID_REQUIRED"))
                }
                lectureRepository.existsById(lectureId!!).flatMap { lectureExists ->
                    if (!lectureExists) Mono.just(ValidationResult.fail("Lecture not found.", mapOf("lectureId" to "not_found"), "LECTURE_NOT_FOUND"))
                    else {
                        studyRepository.existsByUserIdAndLectureIdAndIdNot(userId, lectureId, currentId).map { conflict ->
                            if (conflict) ValidationResult.fail("Another study with same user and lecture already exists.", mapOf("userId" to "conflict"), "STUDY_CONFLICT")
                            else ValidationResult.OK
                        }
                    }
                }
            }
        }
    }
}