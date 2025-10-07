package com.example.practice.validation

import com.example.practice.repository.UserRepository
import com.example.practice.repository.LectureRepository
import com.example.practice.repository.EnrollmentRepository

import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.regex.Pattern

@Service
class ValidationService(
    private val userRepository: UserRepository,
    private val lectureRepository: LectureRepository,
    private val enrollmentRepository: EnrollmentRepository
) {
    private val emailRegex = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun validateUser(username: String?, email: String?, password: String?): Mono<ValidationResult> {
        if (username.isNullOrBlank()) return Mono.just(ValidationResult(false, "Username is required and must not be blank."))
        if (username.length !in 2..20) return Mono.just(ValidationResult(false, "Username must be 2 to 20 characters."))
        return userRepository.existsByUsername(username).flatMap { usernameExists ->
            if (usernameExists) return@flatMap Mono.just(ValidationResult(false, "Username already exists."))
            if (email.isNullOrBlank()) return@flatMap Mono.just(ValidationResult(false, "Email is required and must not be blank."))
            if (email.length > 50) return@flatMap Mono.just(ValidationResult(false, "Email must be no longer than 50 characters."))
            if (!emailRegex.matcher(email).matches()) return@flatMap Mono.just(ValidationResult(false, "Email format is invalid."))
            userRepository.existsByEmail(email).flatMap { emailExists ->
                if (emailExists) return@flatMap Mono.just(ValidationResult(false, "Email already exists."))
                if (password.isNullOrBlank()) return@flatMap Mono.just(ValidationResult(false, "Password is required and must not be blank."))
                if (password.length !in 8..50) return@flatMap Mono.just(ValidationResult(false, "Password must be 8 to 50 characters."))
                Mono.just(ValidationResult(true))
            }
        }
    }

    fun validateLecture(title: String?, description: String?, instructor: String?): Mono<ValidationResult> {
        if (title.isNullOrBlank()) return Mono.just(ValidationResult(false, "Lecture title is required and must not be blank."))
        if (title.length !in 2..100) return Mono.just(ValidationResult(false, "Lecture title must be 2 to 100 characters."))
        return lectureRepository.existsByTitle(title).flatMap { titleExists ->
            if (titleExists) return@flatMap Mono.just(ValidationResult(false, "Lecture title already exists."))
            if (description != null && description.length > 500) return@flatMap Mono.just(ValidationResult(false, "Description must be no longer than 500 characters."))
            if (instructor.isNullOrBlank()) return@flatMap Mono.just(ValidationResult(false, "Instructor is required and must not be blank."))
            if (instructor.length !in 2..30) return@flatMap Mono.just(ValidationResult(false, "Instructor must be 2 to 30 characters."))
            Mono.just(ValidationResult(true))
        }
    }

    fun validateEnrollment(userId: String?, lectureId: String?): Mono<ValidationResult> {
        if (userId.isNullOrBlank()) return Mono.just(ValidationResult(false, "User ID is required."))
        return userRepository.existsById(userId).flatMap { userExists ->
            if (!userExists) return@flatMap Mono.just(ValidationResult(false, "User not found."))
            if (lectureId.isNullOrBlank()) return@flatMap Mono.just(ValidationResult(false, "Lecture ID is required."))
            lectureRepository.existsById(lectureId).flatMap { lectureExists ->
                if (!lectureExists) return@flatMap Mono.just(ValidationResult(false, "Lecture not found."))
                enrollmentRepository.existsByUserIdAndLectureId(userId, lectureId).flatMap { enrolled ->
                    if (enrolled) return@flatMap Mono.just(ValidationResult(false, "User is already enrolled in this lecture."))
                    Mono.just(ValidationResult(true))
                }
            }
        }
    }
}