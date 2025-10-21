package com.loopers.domain.user

class UserCommand {

    data class Create(
        val userId: String,
        val name: String,
        val email: String,
        val birth: String,
        val gender: Gender,
    )

    enum class Gender {
        M,
        F,
    }
}