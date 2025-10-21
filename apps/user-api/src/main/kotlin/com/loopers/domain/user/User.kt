package com.loopers.domain.user

import java.time.LocalDate

class User private constructor(
    val id: Long,
    val userId: String,
    val name: String,
    val email: String,
    val birth: LocalDate,
    val gender: Gender,
) {

    enum class Gender {
        M,
        F,
    }

    companion object {
        fun create(
            id: Long,
            userId: String,
            name: String,
            email: String,
            birth: String,
            gender: Gender
        ): User {
            if (!userId.matches(Regex("^[a-zA-Z0-9]{1,10}$"))) {
                throw IllegalArgumentException("ID는 영문 및 숫자 10자 이내이어야 합니다.")
            }

            if (!email.matches(Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"))) {
                throw IllegalArgumentException("이메일은 xx@yy.zz 형식이어야 합니다.")
            }

            if (!birth.matches(Regex("^\\d{4}-\\d{2}-\\d{2}$"))) {
                throw IllegalArgumentException("생년월일은 yyyy-MM-dd 형식이어야 합니다.")
            }

            return User(
                id = id,
                userId = userId,
                name = name,
                email = email,
                birth = LocalDate.parse(birth),
                gender = gender
            )
        }

    }
}