package com.loopers.application.user

import com.loopers.domain.user.User
import com.loopers.domain.user.User.Gender
import com.loopers.domain.user.UserCommand
import com.loopers.domain.user.UserInfo
import com.loopers.infrastructure.user.UserEntity
import com.loopers.infrastructure.user.UserJpaRepository
import com.loopers.support.error.NotFoundException
import com.loopers.utils.DatabaseCleanUp
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class UserFacadeIntegrationTest(
    private val userFacade: UserFacade,
    private val databaseCleanUp: DatabaseCleanUp,
    @MockitoSpyBean private val userJpaRepository: UserJpaRepository
) {

    @AfterEach
    fun tearDown() {
        databaseCleanUp.truncateAllTables()
    }

    @DisplayName("회원가입 시, ")
    @Nested
    inner class Signup {

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

            userJpaRepository.save(UserEntity.from(existedUser))

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
            assertThat(userJpaRepository.findByUserId(command.userId)).isNull()

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
            assertThat(userJpaRepository.findByUserId(userId)).isNull()

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

            userJpaRepository.save(UserEntity.from(user))

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