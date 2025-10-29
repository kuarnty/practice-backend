package com.example.practice.test

import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class HelloController {
    @QueryMapping
    fun hello(): String = "Hello, GraphQL!"
}