package com.loopers.interfaces.api.user

import com.loopers.domain.user.User
import com.loopers.generator.LongValueGenerator
import com.loopers.infrastructure.user.UserEntity
import com.loopers.infrastructure.user.UserJpaRepository
import com.loopers.interfaces.api.ApiResponse
import com.loopers.utils.DatabaseCleanUp
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.TestConstructor
import org.springframework.util.LinkedMultiValueMap

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class UserV1ApiE2ETest(
    private val testRestTemplate: TestRestTemplate,
    private val databaseCleanUp: DatabaseCleanUp,
    private val userJpaRepository: UserJpaRepository,
    private val generator: LongValueGenerator,
) {
    @AfterEach
    fun tearDown() {
        databaseCleanUp.truncateAllTables()
    }

    @DisplayName("POST /api/v1/users")
    @Nested
    inner class SignUp {

        private val ENDPOINT: String = "/api/v1/users"

        @Test
        fun `회원 가입이 성공할 경우, 생성된 유저 정보를 응답으로 반환한다`() {
            // arrange
            val request = UserV1Dto.SignUpRequest(
                userId = "discphy",
                name = "10",
                email = "dinophy@nate.com",
                birth = "1990-01-01",
                gender = "M",
            )

            // act
            val responseType = object : ParameterizedTypeReference<ApiResponse<UserV1Dto.UserResponse>>() {}
            val response = testRestTemplate.exchange(ENDPOINT, HttpMethod.POST, HttpEntity(request), responseType)

            // assert
            assertAll(
                { assertThat(response.statusCode.is2xxSuccessful).isTrue() },
                { assertThat(response.body?.data?.userId).isEqualTo(request.userId) },
                { assertThat(response.body?.data?.name).isEqualTo(request.name) },
                { assertThat(response.body?.data?.email).isEqualTo(request.email) },
                { assertThat(response.body?.data?.birth).isEqualTo(request.birth) },
            )
        }

        @Test
        fun `회원 가입 시에 성별이 없을 경우, 400 Bad Request 응답을 반환한다`() {
            // arrange
            val request = UserV1Dto.SignUpRequest(
                userId = "discphy",
                name = "10",
                email = "dinophy@nate.com",
                birth = "1990-01-01",
                gender = "",
            )

            // act
            val responseType = object : ParameterizedTypeReference<ApiResponse<UserV1Dto.UserResponse>>() {}
            val response = testRestTemplate.exchange(ENDPOINT, HttpMethod.POST, HttpEntity(request), responseType)

            // assert
            assertAll(
                { assertThat(response.statusCode.is4xxClientError).isTrue() },
                { assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST) },
            )
        }
    }

    @DisplayName("GET /api/v1/users/me")
    @Nested
    inner class Get {

        private val ENDPOINT: String = "/api/v1/users/me"

        @Test
        fun `내 정보 조회에 성공할 경우, 해당하는 유저 정보를 응답으로 반환한다`() {
            // arrange
            val user = User.create(
                id = generator.nextId(),
                userId = "discphy",
                name = "10",
                email = "dinophy@nate.com",
                birth = "2000-01-01",
                gender = User.Gender.M,
            )

            userJpaRepository.save(UserEntity.from(user))

            // act
            val headers = LinkedMultiValueMap<String, String>().apply {
                this["X-USER-ID"] = listOf(user.userId)
            }
            val responseType = object : ParameterizedTypeReference<ApiResponse<UserV1Dto.UserResponse>>() {}
            val response = testRestTemplate.exchange(ENDPOINT, HttpMethod.GET, HttpEntity(null, headers), responseType)

            // assert
            assertAll(
                { assertThat(response.statusCode.is2xxSuccessful).isTrue() },
                { assertThat(response.body?.data?.userId).isEqualTo(user.userId) },
            )
        }

        @Test
        fun `존재하지 않는 ID 로 조회할 경우, 404 Not Found 응답을 반환한다`() {
            // arrange
            val userId = "nonexistent_user"

            // act
            val headers = LinkedMultiValueMap<String, String>().apply {
                this["X-USER-ID"] = listOf(userId)
            }
            val responseType = object : ParameterizedTypeReference<ApiResponse<UserV1Dto.UserResponse>>() {}
            val response = testRestTemplate.exchange(ENDPOINT, HttpMethod.GET, HttpEntity(null, headers), responseType)

            // assert
            assertAll(
                { assertThat(response.statusCode.is4xxClientError).isTrue() },
                { assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND) },
            )
        }
    }
}
