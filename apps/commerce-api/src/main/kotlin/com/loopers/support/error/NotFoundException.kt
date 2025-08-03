package com.loopers.support.error

class NotFoundException(
    val customMessage: String,
) : RuntimeException(customMessage)
