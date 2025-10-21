package com.loopers.interfaces.api.user

import com.fasterxml.jackson.databind.ObjectMapper
import com.loopers.application.user.UserFacade
import com.loopers.domain.user.UserInfo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@WebMvcTest(controllers = [UserV1Controller::class])
class UserV1ApiUnitTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var userFacade: UserFacade

    @DisplayName("POST /api/v1/users")
    @Nested
    inner class SignUp {

        private val ENDPOINT: String = "/api/v1/users"

        @Test
        fun `사용자 ID는 필수이다`() {
            // given
            val request = UserV1Dto.SignUpRequest(
                userId = "",
                name = "10",
                email = "dinophy@nate.com",
                birth = "1990-01-01",
                gender = "M",
            )

            // when & then
            mockMvc.perform(
                post(ENDPOINT)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.meta.message").value("요청 파라미터 'userId' 값 ''이(가) 잘못되었습니다."))
        }

        @Test
        fun `사용자명은 필수이다`() {
            // given
            val request = UserV1Dto.SignUpRequest(
                userId = "discphy",
                name = "",
                email = "dinophy@nate.com",
                birth = "1990-01-01",
                gender = "M",
            )

            // when & then
            mockMvc.perform(
                post(ENDPOINT)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andDo { print() }
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.meta.message").value("요청 파라미터 'name' 값 ''이(가) 잘못되었습니다."))
        }

        @Test
        fun `이메일은 필수이다`() {
            // given
            val request = UserV1Dto.SignUpRequest(
                userId = "discphy",
                name = "10",
                email = "",
                birth = "1990-01-01",
                gender = "M",
            )

            // when & then
            mockMvc.perform(
                post(ENDPOINT)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.meta.message").value("요청 파라미터 'email' 값 ''이(가) 잘못되었습니다."))
        }

        @Test
        fun `생년월일은 필수이다`() {
            // given
            val request = UserV1Dto.SignUpRequest(
                userId = "discphy",
                name = "10",
                email = "dinophy@nate.com",
                birth = "",
                gender = "M",
            )

            // when & then
            mockMvc.perform(
                post(ENDPOINT)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.meta.message").value("요청 파라미터 'birth' 값 ''이(가) 잘못되었습니다."))
        }

        @Test
        fun `성별은 필수이다`() {
            // given
            val request = UserV1Dto.SignUpRequest(
                userId = "discphy",
                name = "10",
                email = "dinophy@nate.com",
                birth = "1990-01-01",
                gender = "",
            )

            // when & then
            mockMvc.perform(
                post(ENDPOINT)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.meta.message").value("요청 파라미터 'gender' 값 ''이(가) 잘못되었습니다."))
        }

        @Test
        fun `성별이 올바르지 않으면 에러가 발생한다`() {
            // given
            val request = UserV1Dto.SignUpRequest(
                userId = "discphy",
                name = "10",
                email = "dinophy@nate.com",
                birth = "1990-01-01",
                gender = "FS",
            )

            // when & then
            mockMvc.perform(
                post(ENDPOINT)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.meta.message").value("성별을 찾을 수 없습니다."))
        }

        @Test
        fun `사용자 정보를 등록한다`() {
            // given
            val request = UserV1Dto.SignUpRequest(
                userId = "discphy",
                name = "10",
                email = "dinophy@nate.com",
                birth = "1990-01-01",
                gender = "M",
            )

            val info = UserInfo.Create(
                userId = "discphy",
                name = "10",
                email = "dinophy@nate.com",
                birth = LocalDate.of(1990, 1, 1),
                gender = UserInfo.Gender.M,
            )

            given(userFacade.signup(request.toCommand()))
                .willReturn(info)

            // when & then
            mockMvc.perform(
                post(ENDPOINT)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andDo(print())
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.data.userId").value(info.userId))
                .andExpect(jsonPath("$.data.name").value(info.name))
                .andExpect(jsonPath("$.data.email").value(info.email))
                .andExpect(jsonPath("$.data.birth").value(info.birth.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                .andExpect(jsonPath("$.data.gender").value(info.gender.name))
        }
    }

    @DisplayName("GET /api/v1/users/me")
    @Nested
    inner class Get {

        private val ENDPOINT: String = "/api/v1/users/me"

        @Test
        fun `X-USER-ID 헤더는 필수이다`() {
            // when & then
            mockMvc.perform(
                get(ENDPOINT)
            )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.meta.message").value("필수 요청 헤더 'X-USER-ID'가 누락되었습니다."))
        }

        @Test
        fun `X-USER-ID 사용자가 존재하지 않으면 에러가 발생한다`() {
            // given
            val xUserId = "nonexistent-user"

            given(userFacade.me(xUserId))
                .willThrow(IllegalArgumentException("사용자를 찾을 수 없습니다."))

            // when & then
            mockMvc.perform(
                get(ENDPOINT)
                    .header("X-USER-ID", xUserId)
            )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.meta.message").value("사용자를 찾을 수 없습니다."))
        }

        @Test
        fun `사용자 정보를 조회한다`() {
            // given
            val info = UserInfo.Get(
                userId = "discphy",
                name = "10",
                email = "dinophy@nate.com",
                birth = LocalDate.of(1990, 1, 1),
                gender = UserInfo.Gender.M,
            )

            given(userFacade.me(info.userId))
                .willReturn(info)

            // when & then
            mockMvc.perform(
                get(ENDPOINT)
                    .header("X-USER-ID", info.userId)
            )
                .andDo(print())
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.data.userId").value(info.userId))
                .andExpect(jsonPath("$.data.name").value(info.name))
                .andExpect(jsonPath("$.data.email").value(info.email))
                .andExpect(jsonPath("$.data.birth").value(info.birth.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                .andExpect(jsonPath("$.data.gender").value(info.gender.name))
        }
    }
}