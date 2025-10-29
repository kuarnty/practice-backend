package com.example.practice.enrollment.repository

import com.example.practice.enrollment.model.Enrollment
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono

interface EnrollmentRepository : ReactiveMongoRepository<Enrollment, String> {
    fun existsByUserIdAndLectureId(userId: String, lectureId: String): Mono<Boolean>
    fun existsByUserIdAndLectureIdAndIdNot(userId: String, lectureId: String, id: String): Mono<Boolean>
}