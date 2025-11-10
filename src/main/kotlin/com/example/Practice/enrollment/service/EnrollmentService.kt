package com.example.practice.enrollment.service

import com.example.practice.enrollment.model.Enrollment
import com.example.practice.enrollment.repository.EnrollmentRepository
import com.example.practice.enrollment.validation.EnrollmentValidationService

import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant

@Service
class EnrollmentService(
    private val enrollmentRepository: EnrollmentRepository,
    private val enrollmentValidationService: EnrollmentValidationService
) {

    fun findAll(): Flux<Enrollment> = enrollmentRepository.findAll()

    fun findById(id: String): Mono<Enrollment> = enrollmentRepository.findById(id)

    fun createEnrollment(accountId: String, lectureId: String): Mono<Enrollment> {
        return enrollmentValidationService.validateEnrollmentForCreate(accountId, lectureId)
            .flatMap { vr ->
                if (!vr.isValid) return@flatMap Mono.error(IllegalArgumentException(vr.errorMessage ?: "validation failed"))

                val enrollment = Enrollment(
                    id = null,
                    accountId = accountId,
                    lectureId = lectureId,
                    progress = 0.0f,
                    grade = null
                )

                enrollmentRepository.save(enrollment)
                    .onErrorResume { ex ->
                        val isDuplicate = ex is DuplicateKeyException ||
                                (ex.cause is com.mongodb.MongoWriteException &&
                                        (ex.cause as com.mongodb.MongoWriteException).error.category == com.mongodb.ErrorCategory.DUPLICATE_KEY)
                        if (isDuplicate) {
                            Mono.error(IllegalArgumentException("User already enrolled in this lecture."))
                        } else {
                            Mono.error(ex)
                        }
                    }
            }
    }

    fun updateEnrollment(id: String, accountId: String?, lectureId: String?, progress: Float?, grade: String?): Mono<Enrollment> {
        return enrollmentValidationService.validateEnrollmentForUpdate(id, accountId, lectureId)
            .flatMap { vr ->
                if (!vr.isValid) return@flatMap Mono.error(IllegalArgumentException(vr.errorMessage ?: "validation failed"))
                enrollmentRepository.findById(id).flatMap { existing ->
                    val updated = existing.copy(
                        accountId = accountId ?: existing.accountId,
                        lectureId = lectureId ?: existing.lectureId,
                        progress = progress ?: existing.progress,
                        grade = grade ?: existing.grade,
                        updatedAt = Instant.now()
                    )
                    enrollmentRepository.save(updated)
                        .onErrorResume { ex ->
                            val isDuplicate = ex is DuplicateKeyException ||
                                    (ex.cause is com.mongodb.MongoWriteException &&
                                            (ex.cause as com.mongodb.MongoWriteException).error.category == com.mongodb.ErrorCategory.DUPLICATE_KEY)
                            if (isDuplicate) Mono.error(IllegalArgumentException("Another enrollment with same account and lecture exists."))
                            else Mono.error(ex)
                        }
                }
            }
    }

    fun deleteEnrollment(id: String): Mono<Boolean> {
        return enrollmentRepository.findById(id)
            .flatMap { _ -> enrollmentRepository.deleteById(id).then(Mono.just(true)) }
            .defaultIfEmpty(false)
    }
}