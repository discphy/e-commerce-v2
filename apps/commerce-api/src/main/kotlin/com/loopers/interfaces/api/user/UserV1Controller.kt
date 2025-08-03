package com.loopers.interfaces.api.user

import com.loopers.application.user.UserFacade
import com.loopers.interfaces.api.ApiResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

@RestController
class UserV1Controller(
    private val userFacade: UserFacade,
) : UserV1ApiSpec {

    @PostMapping("/api/v1/users")
    override fun signUp(
        @Valid @RequestBody request: UserV1Dto.SignUpRequest,
    ): ApiResponse<UserV1Dto.UserResponse> {
        return userFacade.signup(request.toCommand())
            .let { UserV1Dto.UserResponse.from(it) }
            .let { ApiResponse.success(it) }
    }

    @GetMapping("/api/v1/users/me")
    override fun me(
        @RequestHeader("X-USER-ID") userId: String,
    ): ApiResponse<UserV1Dto.UserResponse> {
        return userFacade.me(userId)
            .let { UserV1Dto.UserResponse.from(it) }
            .let { ApiResponse.success(it) }
    }
}
