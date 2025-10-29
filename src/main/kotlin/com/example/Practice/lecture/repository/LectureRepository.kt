package com.example.practice.lecture.repository

import com.example.practice.lecture.model.Lecture

import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono

interface LectureRepository : ReactiveMongoRepository<Lecture, String> {
    fun existsByTitle(title: String): Mono<Boolean>
    fun existsByTitleAndIdNot(title: String, id: String): Mono<Boolean>
}