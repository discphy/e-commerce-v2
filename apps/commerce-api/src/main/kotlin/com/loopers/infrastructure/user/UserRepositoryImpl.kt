package com.loopers.infrastructure.user

import com.loopers.domain.user.User
import com.loopers.domain.user.UserRepository
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(
    private val userJpaRepository: UserJpaRepository,
): UserRepository {

    override fun save(user: User): User {
        return userJpaRepository.save(UserEntity.from(user)).toDomain()
    }

    override fun find(userId: String): User? {
        return userJpaRepository.findByUserId(userId)?.toDomain()
    }
}