package com.loopers.application.user

import com.loopers.domain.user.User
import com.loopers.domain.user.User.Gender
import com.loopers.domain.user.UserCommand
import com.loopers.domain.user.UserInfo
import com.loopers.domain.user.UserService
import com.loopers.support.error.NotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.mockito.kotlin.willReturn

@ExtendWith(MockitoExtension::class)
class UserFacadeUnitTest {

    @InjectMocks
    private lateinit var userFacade: UserFacade

    @Mock
    private lateinit var userService: UserService

    @DisplayName("회원가입 시, ")
    @Nested
    inner class SignUp {

        @Test
        fun `이미 가입된 ID는 실패한다`() {
            // given
            val command = UserCommand.Create(
                userId = "discphy",
                name = "10",
                email = "dinophy@nate.com",
                birth = "2000-01-01",
                gender = UserCommand.Gender.M,
            )

            val existedUser = User.create(
                id = 1L,
                userId = command.userId,
                name = "사용자",
                email = "discphy@naver.com",
                birth = "2001-01-01",
                gender = Gender.M,
            )

            given { userService.get(command.userId) }
                .willReturn { existedUser }

            // when & then
            assertThatThrownBy {
                userFacade.signup(command)
            }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("이미 가입된 ID 입니다.")
        }

        @Test
        fun `사용자를 생성한다`() {
            // given
            val command = UserCommand.Create(
                userId = "discphy",
                name = "10",
                email = "dinophy@nate.com",
                birth = "2000-01-01",
                gender = UserCommand.Gender.M,
            )

            val user = User.create(
                id = 1L,
                userId = "discphy",
                name = "10",
                email = "dinophy@nate.com",
                birth = "2000-01-01",
                gender = Gender.M,
            )

            given { userService.get(command.userId) }
                .willReturn { null }

            given { userService.create(any()) }
                .willReturn { user }

            // when
            val result = userFacade.signup(command)

            // then
            assertThat(result.userId).isEqualTo(command.userId)
            assertThat(result.name).isEqualTo(command.name)
            assertThat(result.email).isEqualTo(command.email)
            assertThat(result.birth).isEqualTo(command.birth)
            assertThat(result.gender).isEqualTo(UserInfo.Gender.M)
        }
    }

    @DisplayName("내 정보 조회 시, ")
    @Nested
    inner class Me {

        @Test
        fun `사용자가 존재하지 않으면 실패한다`() {
            // given
            val userId = "discphy"

            given { userService.get(userId) }
                .willReturn { null }

            // when & then
            assertThatThrownBy {
                userFacade.me(userId)
            }
                .isInstanceOf(NotFoundException::class.java)
                .hasMessageContaining("사용자를 찾을 수 없습니다.")
        }

        @Test
        fun `사용자가 존재 시 데이터를 반환한다`() {
            // given
            val user = User.Companion.create(
                id = 1L,
                userId = "discphy",
                name = "10",
                email = "dinophy@nate.com",
                birth = "2000-01-01",
                gender = Gender.M,
            )

            given { userService.get(user.userId) }
                .willReturn { user }

            // when
            val result = userFacade.me(user.userId)

            // then
            assertThat(result.userId).isEqualTo(user.userId)
            assertThat(result.name).isEqualTo(user.name)
            assertThat(result.email).isEqualTo(user.email)
            assertThat(result.birth).isEqualTo(user.birth)
            assertThat(result.gender).isEqualTo(UserInfo.Gender.M)
        }
    }
}
