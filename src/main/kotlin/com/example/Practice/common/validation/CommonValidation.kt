package com.example.practice.common.validation

/**
 * Pure, stateless helper functions for common field validations.
 * These return an error message string when validation fails, or null when the value is valid.
 *
 * Keep these functions synchronous and side-effect free so they are cheap to call
 * before performing any async (repository) checks in domain validation services.
 */
object CommonValidation {
        
    fun requireNonNull(value: String?, fieldName: String): String? =
        if (value == null) "$fieldName is required." else null
        
    fun requireNonBlank(value: String?, fieldName: String): String? =
        if (value.isNullOrBlank()) "$fieldName is required and must not be blank." else null

    fun maxLength(value: String?, fieldName: String, max: Int): String? {
        if (value == null) return null
        return if (value.length > max) "$fieldName must be no longer than $max characters." else null
    }

    fun lengthBetween(value: String?, fieldName: String, min: Int, max: Int): String? {
        if (value == null) return null
        return if (value.length !in min..max) "$fieldName must be $min to $max characters." else null
    }

    fun matchesRegex(value: String?, fieldName: String, regex: Regex, message: String): String? {
        if (value == null) return null
        return if (!regex.matches(value)) message else null
    }
}