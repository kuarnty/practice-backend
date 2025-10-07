package com.example.practice.validation

data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null
)