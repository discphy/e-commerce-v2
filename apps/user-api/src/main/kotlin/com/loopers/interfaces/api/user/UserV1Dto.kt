package com.loopers.interfaces.api.user

import com.loopers.domain.user.UserCommand
import com.loopers.domain.user.UserInfo
import com.loopers.interfaces.api.user.UserV1Dto.Gender.F
import com.loopers.interfaces.api.user.UserV1Dto.Gender.M
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import jakarta.validation.constraints.NotBlank
import java.time.format.DateTimeFormatter

class UserV1Dto {

    data class SignUpRequest(
        @field:NotBlank val userId: String,
        @field:NotBlank val name: String,
        @field:NotBlank val email: String,
        @field:NotBlank val birth: String,
        @field:NotBlank val gender: String,
    ) {
        fun toCommand(): UserCommand.Create {
            return UserCommand.Create(
                userId = userId,
                name = name,
                email = email,
                birth = birth,
                gender = when(gender) {
                    "M" -> UserCommand.Gender.M
                    "F" -> UserCommand.Gender.F
                    else -> throw CoreException(ErrorType.BAD_REQUEST, "성별을 찾을 수 없습니다.")
                },
            )
        }
    }

    data class UserResponse(
        val userId: String,
        val name: String,
        val email: String,
        val birth: String,
        val gender: Gender,
    ) {
        companion object {
            fun from(user: UserInfo.Create): UserResponse {
                return UserResponse(
                    userId = user.userId,
                    name = user.name,
                    email = user.email,
                    birth = user.birth.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    gender = when (user.gender) {
                        UserInfo.Gender.M -> M
                        UserInfo.Gender.F -> F
                    },
                )
            }

            fun from(user: UserInfo.Get): UserResponse {
                return UserResponse(
                    userId = user.userId,
                    name = user.name,
                    email = user.email,
                    birth = user.birth.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    gender = when (user.gender) {
                        UserInfo.Gender.M -> M
                        UserInfo.Gender.F -> F
                    },
                )
            }
        }
    }

    enum class Gender {
        M,
        F,
    }
}
