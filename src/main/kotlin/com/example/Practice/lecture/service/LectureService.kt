package com.example.practice.lecture.service

import com.example.practice.lecture.model.Lecture
import com.example.practice.lecture.repository.LectureRepository
import com.example.practice.lecture.validation.LectureValidationService

import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant

@Service
class LectureService(
    private val lectureRepository: LectureRepository,
    private val lectureValidationService: LectureValidationService
) {
    
    fun findAll(): Flux<Lecture> = lectureRepository.findAll()

    fun findById(id: String): Mono<Lecture> = lectureRepository.findById(id)

    fun createLecture(title: String?, description: String?, teacherId: String): Mono<Lecture> {
        return lectureValidationService.validateLectureForCreate(title, description, teacherId)
            .flatMap { vr ->
                if (!vr.isValid) return@flatMap Mono.error(IllegalArgumentException(vr.errorMessage ?: "validation failed"))

                val now = Instant.now()
                val lecture = Lecture(
                    id = null,
                    title = title!!,
                    description = description,
                    teacherId = teacherId,
                    createdAt = now,
                    updatedAt = now
                )

                lectureRepository.save(lecture)
                    .onErrorResume { ex ->
                        val isDuplicate = ex is DuplicateKeyException ||
                                (ex.cause is com.mongodb.MongoWriteException &&
                                        (ex.cause as com.mongodb.MongoWriteException).error.category == com.mongodb.ErrorCategory.DUPLICATE_KEY)
                        if (isDuplicate) Mono.error(IllegalArgumentException("Lecture with same title already exists."))
                        else Mono.error(ex)
                    }
            }
    }

    fun updateLecture(id: String, title: String?, description: String?, teacherId: String?): Mono<Lecture> {
        return lectureValidationService.validateLectureForUpdate(id, title, description, teacherId)
            .flatMap { vr ->
                if (!vr.isValid) return@flatMap Mono.error(IllegalArgumentException(vr.errorMessage ?: "validation failed"))

                lectureRepository.findById(id).flatMap { existing ->
                    val updated = existing.copy(
                        title = title ?: existing.title,
                        description = description ?: existing.description,
                        teacherId = teacherId ?: existing.teacherId,
                        updatedAt = Instant.now()
                    )
                    lectureRepository.save(updated)
                        .onErrorResume { ex ->
                            val isDuplicate = ex is DuplicateKeyException ||
                                    (ex.cause is com.mongodb.MongoWriteException &&
                                            (ex.cause as com.mongodb.MongoWriteException).error.category == com.mongodb.ErrorCategory.DUPLICATE_KEY)
                            if (isDuplicate) Mono.error(IllegalArgumentException("Another lecture with same title exists."))
                            else Mono.error(ex)
                        }
                }
            }
    }

    fun deleteLecture(id: String): Mono<Boolean> {
        return lectureRepository.findById(id)
            .flatMap { _ -> lectureRepository.deleteById(id).then(Mono.just(true)) }
            .defaultIfEmpty(false)
    }
}