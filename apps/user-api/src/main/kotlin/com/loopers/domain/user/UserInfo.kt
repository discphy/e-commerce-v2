package com.loopers.domain.user

import java.time.LocalDate

class UserInfo {

    class Create(
        val userId: String,
        val name: String,
        val email: String,
        val birth: LocalDate,
        val gender: Gender
    ) {
        companion object {
            fun from(user: User): Create {
                return Create(
                    userId = user.userId,
                    name = user.name,
                    email = user.email,
                    birth = user.birth,
                    gender = when (user.gender) {
                        User.Gender.M -> Gender.M
                        User.Gender.F -> Gender.F
                    },
                )
            }
        }
    }

    class Get(
        val userId: String,
        val name: String,
        val email: String,
        val birth: LocalDate,
        val gender: Gender
    ) {
        companion object {
            fun from(user: User): Get {
                return Get(
                    userId = user.userId,
                    name = user.name,
                    email = user.email,
                    birth = user.birth,
                    gender = when (user.gender) {
                        User.Gender.M -> Gender.M
                        User.Gender.F -> Gender.F
                    },
                )
            }
        }
    }

    enum class Gender {
        M, F;
    }
}