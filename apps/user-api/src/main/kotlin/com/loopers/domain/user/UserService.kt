package com.loopers.domain.user

import com.loopers.generator.LongValueGenerator
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val generator: LongValueGenerator,
) {

    fun create(command: UserCommand.Create): User {
        val user = User.create(
            id = generator.nextId(),
            userId = command.userId,
            name = command.name,
            email = command.email,
            birth = command.birth,
            gender = when (command.gender) {
                UserCommand.Gender.M -> User.Gender.M
                UserCommand.Gender.F -> User.Gender.F
            }
        )
        return userRepository.save(user)
    }

    fun get(userId: String): User? {
        return userRepository.find(userId)
    }
}