package com.example.practice.common.validation

/**
 * Simple validation result used across domain validation services.
 * - isValid: true when validation passed.
 * - errorMessage: user-facing message for the first (or summary) error.
 * - fieldErrors: optional map of field -> error message for structured reporting.
 * - code: optional error code (eg. "NAME_EXISTS", "INVALID_EMAIL")
 */
data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null,
    val fieldErrors: Map<String, String>? = null,
    val code: String? = null
) {
    companion object {
        val OK = ValidationResult(true)
        fun fail(message: String, fieldErrors: Map<String, String>? = null, code: String? = null) =
            ValidationResult(false, message, fieldErrors, code)
    }
}