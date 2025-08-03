package com.loopers.domain.user

import com.loopers.domain.user.User.Gender
import com.loopers.generator.SnowflakeLongValueGenerator
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.time.LocalDate

class UserTest {

    private val generator = SnowflakeLongValueGenerator()

    @DisplayName("사용자 생성 시, ")
    @Nested
    inner class Create {

        @ParameterizedTest
        @ValueSource(
            strings = [
                "user12345678", // 11자
                "user!@#",      // 특수문자 포함
                "user 123",     // 공백 포함
                "한글",          // 한글 포함
                "",             // 공백
            ]
        )
        fun `ID가 올바르지 않으면, 사용자 생성에 실패한다`(userId: String) {
            // when & then
            assertThatThrownBy {
                User.create(
                    id = generator.nextId(),
                    userId = userId,
                    name = "10",
                    email = "dinophy@nate.com",
                    birth = "2000-01-01",
                    gender = Gender.M,
                )
            }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("ID는 영문 및 숫자 10자 이내이어야 합니다.")
        }

        @ParameterizedTest
        @ValueSource(
            strings = [
                "user@domain",          // 도메인 없는 이메일
                "user@.com",           // 도메인 앞에 점
                "user@domain.",        // 도메인 뒤에 점
                "user@domain,com",     // 잘못된 구분자 사용
                "userdomain.com",      // @ 기호 없음
                "",                    // 공백
            ]
        )
        fun `이메일이 올바르지 않으면, 사용자 생성에 실패한다`(email: String) {
            // when & then
            assertThatThrownBy {
                User.create(
                    id = generator.nextId(),
                    userId = "user123",
                    name = "10",
                    email = email,
                    birth = "2000-01-01",
                    gender = Gender.M,
                )
            }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("이메일은 xx@yy.zz 형식이어야 합니다.")
        }

        @ParameterizedTest
        @ValueSource(
            strings = [
                "2000-01-01T00:00:00", // 시간 포함
                "2000/01/01", // 슬래시 사용
                "20000101",   // 하이픈 없음
                "",           // 공백
            ]
        )
        fun `생년월일이 올바르지 않으면, 사용자 생성에 실패한다`(birth: String) {
            // when & then
            assertThatThrownBy {
                User.create(
                    id = generator.nextId(),
                    userId = "user123",
                    name = "10",
                    email = "dinophy@nate.com",
                    birth = birth,
                    gender = Gender.M,
                )
            }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("생년월일은 yyyy-MM-dd 형식이어야 합니다.")
        }

        @Test
        fun `사용자를 생성한다`() {
            // when
            val user = User.create(
                id = generator.nextId(),
                userId = "user123",
                name = "10",
                email = "dinophy@nate.com",
                birth = "2025-08-02",
                gender = Gender.M,
            )

            // then
            assertThat(user.id).isNotNull
            assertThat(user.userId).isEqualTo("user123")
            assertThat(user.name).isEqualTo("10")
            assertThat(user.email).isEqualTo("dinophy@nate.com")
            assertThat(user.birth).isEqualTo(LocalDate.of(2025, 8, 2))
            assertThat(user.gender).isEqualTo(Gender.M)
        }
    }
}