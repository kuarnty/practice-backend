package com.example.practice.repository

import com.example.practice.model.Lecture
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono

interface LectureRepository : ReactiveMongoRepository<Lecture, String> {
    fun existsByTitle(title: String): Mono<Boolean>
}