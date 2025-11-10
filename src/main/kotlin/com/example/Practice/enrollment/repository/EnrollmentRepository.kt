package com.example.practice.enrollment.repository

import com.example.practice.enrollment.model.Enrollment
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono
import reactor.core.publisher.Flux

interface EnrollmentRepository : ReactiveMongoRepository<Enrollment, String> {
    fun existsByAccountIdAndLectureId(accounotId: String, lectureId: String): Mono<Boolean>
    fun existsByAccountIdAndLectureIdAndIdNot(accoundId: String, lectureId: String, id: String): Mono<Boolean>
}