package com.loopers.domain.user

import com.loopers.domain.user.User.Gender
import com.loopers.generator.LongValueGenerator
import org.assertj.core.api.Assertions.assertThat
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
class UserServiceUnitTest {

    @InjectMocks
    private lateinit var userService: UserService

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var generator: LongValueGenerator

    @DisplayName("유저 생성 시, ")
    @Nested
    inner class Create {

        @Test
        fun `사용자를 생성 후 저장한다`() {
            // given
            val command = UserCommand.Create(
                userId = "discphy",
                name = "10",
                email = "dinophy@nate.com",
                birth = "2000-01-01",
                gender = UserCommand.Gender.M,
            )

            val user = User.create(
                id = generator.nextId(),
                userId = "discphy",
                name = "10",
                email = "dinophy@nate.com",
                birth = "2000-01-01",
                gender = Gender.M,
            )

            given { userRepository.save(any<User>()) }
                .willReturn { user }

            // when
            val result = userService.create(command)

            // then
            assertThat(result.userId).isEqualTo(command.userId)
            assertThat(result.name).isEqualTo(command.name)
            assertThat(result.email).isEqualTo(command.email)
            assertThat(result.birth).isEqualTo(command.birth)
            assertThat(result.gender).isEqualTo(Gender.M)
        }
    }

    @DisplayName("유저 조회 시, ")
    @Nested
    inner class Get {

        @Test
        fun `사용자가 존재하지 않으면 null을 반환한다`() {
            // given
            val userId = "discphy"

            given { userRepository.find(userId) }
                .willReturn { null }

            // when
            val result = userService.get(userId)

            // then
            assertThat(result).isNull()
        }

        @Test
        fun `사용자가 존재 시 데이터를 반환한다`() {
            // given
            val user = User.create(
                id = generator.nextId(),
                userId = "discphy",
                name = "10",
                email = "dinophy@nate.com",
                birth = "2000-01-01",
                gender = Gender.M,
            )

            given { userRepository.find(user.userId) }
                .willReturn { user }

            // when
            val result = userService.get(user.userId)

            // then
            assertThat(result?.userId).isEqualTo(user.userId)
            assertThat(result?.name).isEqualTo(user.name)
            assertThat(result?.email).isEqualTo(user.email)
            assertThat(result?.birth).isEqualTo(user.birth)
            assertThat(result?.gender).isEqualTo(Gender.M)
        }
    }

}