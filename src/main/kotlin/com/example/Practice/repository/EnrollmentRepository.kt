package com.example.practice.repository

import com.example.practice.model.Enrollment
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface EnrollmentRepository : ReactiveMongoRepository<Enrollment, String>