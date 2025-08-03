package com.loopers.application.user

import com.loopers.domain.user.UserCommand
import com.loopers.domain.user.UserInfo
import com.loopers.domain.user.UserService
import com.loopers.support.error.NotFoundException
import jakarta.transaction.Transactional
import org.springframework.stereotype.Component

@Component
class UserFacade(
    private val userService: UserService,
) {

    @Transactional
    fun signup(command: UserCommand.Create): UserInfo.Create {
        userService.get(command.userId)?.let { throw IllegalArgumentException("이미 가입된 ID 입니다.") }

        return userService.create(command)
            .let { UserInfo.Create.from(it) }
    }

    fun me(userId: String): UserInfo.Get {
        return userService.get(userId)?.let {
            UserInfo.Get.from(it)
        } ?: throw NotFoundException("사용자를 찾을 수 없습니다.")
    }
}