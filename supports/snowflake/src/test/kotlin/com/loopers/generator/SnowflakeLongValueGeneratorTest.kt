package com.loopers.generator

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class SnowflakeLongValueGeneratorTest {

    private lateinit var generator: LongValueGenerator

    @BeforeEach
    fun setUp() {
        generator = SnowflakeLongValueGenerator()
    }

    @DisplayName("Snowflake로 ID를 생성한다.")
    @Test
    fun generateId() {
        // when
        val id = generator.nextId()

        // then
        assertThat(id).isNotNull
        println("id = $id")
    }

    @DisplayName("Snowflake로 생성된 여러개의 ID는 정렬 순서가 보장된다.")
    @Test
    fun generateMultipleIds() {
        // when
        val ids = (1..10).map { generator.nextId() }

        // then
        assertThat(ids).isSorted()
        println("ids = $ids")
    }
}