package com.example.practice.exception

import graphql.GraphQLError
import graphql.GraphqlErrorBuilder
import org.springframework.graphql.execution.ErrorType
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter
import org.springframework.stereotype.Component

@Component
class GraphQLExceptionHandler : DataFetcherExceptionResolverAdapter() {
    override fun resolveToSingleError(ex: Throwable, env: graphql.schema.DataFetchingEnvironment): GraphQLError? {
        return when (ex) {
            is IllegalArgumentException -> GraphqlErrorBuilder.newError(env)
                .errorType(ErrorType.BAD_REQUEST)
                .message(ex.message ?: "Validation error")
                .build()
            else -> null
        }
    }
}