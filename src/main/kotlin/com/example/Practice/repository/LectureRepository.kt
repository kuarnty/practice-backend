package com.example.practice.repository

import com.example.practice.model.Lecture
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface LectureRepository : ReactiveMongoRepository<Lecture, String>