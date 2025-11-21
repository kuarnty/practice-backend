package com.example.practice.study.repository

import com.example.practice.study.model.Study
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono
import reactor.core.publisher.Flux

interface StudyRepository : ReactiveMongoRepository<Study, String> {
    fun existsByAccountIdAndLectureId(accounotId: String, lectureId: String): Mono<Boolean>
    fun existsByAccountIdAndLectureIdAndIdNot(accoundId: String, lectureId: String, id: String): Mono<Boolean>
}