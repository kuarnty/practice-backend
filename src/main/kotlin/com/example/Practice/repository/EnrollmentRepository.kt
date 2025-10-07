package com.example.practice.repository

import com.example.practice.model.Enrollment
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono

interface EnrollmentRepository : ReactiveMongoRepository<Enrollment, String> {
    fun existsByUserIdAndLectureId(userId: String, lectureId: String): Mono<Boolean>
}