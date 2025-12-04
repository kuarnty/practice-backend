package com.example.practice.user.api

import com.example.practice.user.model.User
import com.example.practice.user.service.UserService
import com.example.practice.user.model.UserSummary
import com.example.practice.user.model.CreateUserInput
import com.example.practice.user.model.UpdateUserInput

import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Controller
class UserController(private val userService: UserService) {

    @QueryMapping(name = "users")
    fun findAllUsers(): Flux<User> = userService.findAllUsers()

    @QueryMapping(name = "user")
    fun findUserById(@Argument id: String): Mono<User> = userService.findUserById(id)

    @QueryMapping(name = "userSummaries")
    fun findUserSummaries(): Flux<UserSummary> = userService.findAllUserSummaries()

    @QueryMapping(name = "userSummary")
    fun findUserSummaryById(@Argument id: String): Mono<UserSummary> = userService.findUserSummaryById(id)

    @MutationMapping
    fun createUser(@Argument createUserInput: CreateUserInput): Mono<Boolean> = userService.createUser(createUserInput)

    @MutationMapping
    fun updateUser(
        @Argument updateUserInput: UpdateUserInput,
        principal: org.springframework.security.core.Authentication?
    ): Mono<User> {
        if (principal == null || !principal.isAuthenticated)
            return Mono.error(IllegalAccessException("Authentication required"))
        // 인증된 사용자만 자신의 정보 수정 가능 (id 비교)
        // principal.name은 email이므로, id와 매칭하려면 email로 userId를 조회해야 함
        // 여기서는 단순 인증만 체크

        return userService.updateUser(updateUserInput)
    }

    @MutationMapping
    fun deleteUser(
        @Argument id: String,
        principal: org.springframework.security.core.Authentication?
    ): Mono<Boolean> {
        if (principal == null || !principal.isAuthenticated)
            return Mono.error(IllegalAccessException("Authentication required"))

        return userService.deleteUser(id)
    }
}