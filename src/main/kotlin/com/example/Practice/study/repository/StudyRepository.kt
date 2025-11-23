package com.example.practice.study.repository

import com.example.practice.study.model.Study
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono
import reactor.core.publisher.Flux

interface StudyRepository : ReactiveMongoRepository<Study, String> {
    fun existsByUserIdAndLectureId(userId: String, lectureId: String): Mono<Boolean>
    fun existsByUserIdAndLectureIdAndIdNot(userId: String, lectureId: String, id: String): Mono<Boolean>
}