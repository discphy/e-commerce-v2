package com.loopers.infrastructure.user

import com.loopers.domain.BaseEntity
import com.loopers.domain.user.User
import jakarta.persistence.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Entity
@Table(name = "member")
class UserEntity(
    id: Long,
    userId: String,
    name: String,
    email: String,
    birth: LocalDate,
    gender: Gender,
) : BaseEntity() {

    enum class Gender {
        M,
        F,
    }

    @Id
    val id: Long = id

    val userId: String = userId

    var name: String = name
        private set

    var email: String = email
        private set

    val birth: LocalDate = birth

    @Enumerated(EnumType.STRING)
    val gender: Gender = gender

    companion object {
        fun from(user: User): UserEntity {
            return UserEntity(
                id = user.id,
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

    fun toDomain(): User {
        return User.create(
            id = id,
            userId = userId,
            name = name,
            email = email,
            birth = birth.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
            gender = when (gender) {
                Gender.M -> User.Gender.M
                Gender.F -> User.Gender.F
            },
        )
    }
}