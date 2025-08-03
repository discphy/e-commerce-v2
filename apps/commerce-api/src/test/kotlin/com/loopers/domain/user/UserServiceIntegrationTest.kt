package com.loopers.domain.user

import com.loopers.domain.user.User.Gender
import com.loopers.generator.LongValueGenerator
import com.loopers.infrastructure.user.UserEntity
import com.loopers.infrastructure.user.UserJpaRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestConstructor

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class UserServiceIntegrationTest(
    private val userService: UserService,
    private val generator: LongValueGenerator,
    private val userJpaRepository: UserJpaRepository,
) {

    @AfterEach
    fun tearDown() {
        userJpaRepository.deleteAllInBatch()
    }

    @DisplayName("유저 생성 시, ")
    @Nested
    inner class Create {

        @Test
        fun `사용자를 생성 후 저장한다`() {
            // given
            val command = UserCommand.Create(
                userId = "dinophy",
                name = "10",
                email = "dinophy@nate.com",
                birth = "2000-01-01",
                gender = UserCommand.Gender.M,
            )

            // when
            val user = userService.create(command)

            // then
            val result = userJpaRepository.findById(user.id).orElseThrow().toDomain()
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
            assertThat(userJpaRepository.findByUserId(userId)).isNull()

            // when
            val user = userService.get(userId)

            // then
            assertThat(user).isNull()
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

            userJpaRepository.save(UserEntity.from(user))

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