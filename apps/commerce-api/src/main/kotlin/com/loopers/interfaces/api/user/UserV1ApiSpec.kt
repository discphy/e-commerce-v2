package com.loopers.interfaces.api.user

import com.loopers.interfaces.api.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.RequestBody

@Tag(name = "User V1 API", description = "Loopers 사용자 API 입니다.")
interface UserV1ApiSpec {

    @Operation(
        summary = "회원 가입",
        description = "사용자를 회원 가입합니다.",
    )
    fun signUp(
        @Schema(name = "사용자 정보", description = "회원 가입할 사용자의 정보")
        @RequestBody request: UserV1Dto.SignUpRequest,
    ): ApiResponse<UserV1Dto.UserResponse>

    @Operation(
        summary = "회원 정보 조회",
        description = "사용자의 정보를 조회합니다.",
    )
    fun me(
        @Schema(name = "사용자 ID", description = "조회할 사용자의 ID")
        userId: String,
    ): ApiResponse<UserV1Dto.UserResponse>
}
