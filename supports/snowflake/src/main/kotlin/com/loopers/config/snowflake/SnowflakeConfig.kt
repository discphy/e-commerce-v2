package com.loopers.config.snowflake

import com.loopers.generator.LongValueGenerator
import com.loopers.generator.SnowflakeLongValueGenerator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SnowflakeConfig {

    @Bean
    fun longValueGenerator(): LongValueGenerator {
        return SnowflakeLongValueGenerator()
    }
}