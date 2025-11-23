package com.example.practice.study.service

import com.example.practice.study.model.Study
import com.example.practice.study.repository.StudyRepository
import com.example.practice.study.validation.StudyValidationService

import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant

@Service
class StudyService(
    private val studyRepository: StudyRepository,
    private val studyValidationService: StudyValidationService
) {

    fun findAll(): Flux<Study> = studyRepository.findAll()

    fun findById(id: String): Mono<Study> = studyRepository.findById(id)

    fun createStudy(userId: String, lectureId: String): Mono<Study> {
        return studyValidationService.validateStudyForCreate(userId, lectureId)
            .flatMap { vr ->
                if (!vr.isValid) return@flatMap Mono.error(IllegalArgumentException(vr.errorMessage ?: "validation failed"))

                val study = Study(
                    id = null,
                    userId = userId,
                    lectureId = lectureId,
                    progress = 0.0f,
                    grade = null
                )

                studyRepository.save(study)
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

    fun updateStudy(id: String, userId: String?, lectureId: String?, progress: Float?, grade: String?): Mono<Study> {
        return studyValidationService.validateStudyForUpdate(id, userId, lectureId)
            .flatMap { vr ->
                if (!vr.isValid) return@flatMap Mono.error(IllegalArgumentException(vr.errorMessage ?: "validation failed"))
                studyRepository.findById(id).flatMap { existing ->
                    val updated = existing.copy(
                        userId = userId ?: existing.userId,
                        lectureId = lectureId ?: existing.lectureId,
                        progress = progress ?: existing.progress,
                        grade = grade ?: existing.grade,
                        updatedAt = Instant.now()
                    )
                    studyRepository.save(updated)
                        .onErrorResume { ex ->
                            val isDuplicate = ex is DuplicateKeyException ||
                                    (ex.cause is com.mongodb.MongoWriteException &&
                                            (ex.cause as com.mongodb.MongoWriteException).error.category == com.mongodb.ErrorCategory.DUPLICATE_KEY)
                            if (isDuplicate) Mono.error(IllegalArgumentException("Another study with same user and lecture exists."))
                            else Mono.error(ex)
                        }
                }
            }
    }

    fun deleteStudy(id: String): Mono<Boolean> {
        return studyRepository.findById(id)
            .flatMap { _ -> studyRepository.deleteById(id).then(Mono.just(true)) }
            .defaultIfEmpty(false)
    }
}