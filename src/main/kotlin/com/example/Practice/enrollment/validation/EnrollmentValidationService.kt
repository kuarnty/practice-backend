package com.example.practice.enrollment.validation

import com.example.practice.enrollment.repository.EnrollmentRepository
import com.example.practice.lecture.repository.LectureRepository
import com.example.practice.account.repository.AccountRepository

import com.example.practice.common.validation.CommonValidation
import com.example.practice.common.validation.ValidationResult

import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class EnrollmentValidationService(
    private val accountRepository: AccountRepository,
    private val lectureRepository: LectureRepository,
    private val enrollmentRepository: EnrollmentRepository
) {
    fun validateEnrollmentForCreate(accountId: String, lectureId: String): Mono<ValidationResult> {
        CommonValidation.requireNonBlank(accountId, "Account ID")?.let {
            return Mono.just(ValidationResult.fail(it, mapOf("accountId" to it), "Account_ID_REQUIRED"))
        }

        // check account existence
        return accountRepository.existsById(accountId).flatMap { accountExists ->
            if (!accountExists) Mono.just(ValidationResult.fail("Account not found.", mapOf("accountId" to "not_found"), "ACCOUNT_NOT_FOUND"))
            else {
                CommonValidation.requireNonBlank(lectureId, "Lecture ID")?.let {
                    return@flatMap Mono.just(ValidationResult.fail(it, mapOf("lectureId" to it), "LECTURE_ID_REQUIRED"))
                }
                lectureRepository.existsById(lectureId).flatMap { lectureExists ->
                    if (!lectureExists) Mono.just(ValidationResult.fail("Lecture not found.", mapOf("lectureId" to "not_found"), "LECTURE_NOT_FOUND"))
                    else {
                        enrollmentRepository.existsByAccountIdAndLectureId(accountId, lectureId).map { enrolled ->
                            if (enrolled) ValidationResult.fail("Account is already enrolled in this lecture.", mapOf("accountId" to "already_enrolled"), "ALREADY_ENROLLED")
                            else ValidationResult.OK
                        }
                    }
                }
            }
        }
    }

    fun validateEnrollmentForUpdate(currentId: String, accountId: String?, lectureId: String?): Mono<ValidationResult> {
        CommonValidation.requireNonBlank(accountId, "Account ID")?.let {
            return Mono.just(ValidationResult.fail(it, mapOf("accountId" to it), "ACCOUNT_ID_REQUIRED"))
        }

        return accountRepository.existsById(accountId!!).flatMap { accountExists ->
            if (!accountExists) Mono.just(ValidationResult.fail("Account not found.", mapOf("accountId" to "not_found"), "ACCOUNT_NOT_FOUND"))
            else {
                CommonValidation.requireNonBlank(lectureId, "Lecture ID")?.let {
                    return@flatMap Mono.just(ValidationResult.fail(it, mapOf("lectureId" to it), "LECTURE_ID_REQUIRED"))
                }
                lectureRepository.existsById(lectureId!!).flatMap { lectureExists ->
                    if (!lectureExists) Mono.just(ValidationResult.fail("Lecture not found.", mapOf("lectureId" to "not_found"), "LECTURE_NOT_FOUND"))
                    else {
                        enrollmentRepository.existsByAccountIdAndLectureIdAndIdNot(accountId, lectureId, currentId).map { conflict ->
                        if (conflict) ValidationResult.fail("Another enrollment with same account and lecture already exists.", mapOf("accountId" to "conflict"), "ENROLLMENT_CONFLICT")
                        else ValidationResult.OK
                        }
                    }
                }
            }
        }
    }
}