package com.loopers.generator

import java.util.random.RandomGenerator

class SnowflakeLongValueGenerator : LongValueGenerator {
    companion object {
        private const val UNUSED_BITS = 1
        private const val EPOCH_BITS = 41
        private const val NODE_ID_BITS = 10
        private const val SEQUENCE_BITS = 12

        private val maxNodeId = (1L shl NODE_ID_BITS) - 1
        private val maxSequence = (1L shl SEQUENCE_BITS) - 1
    }

    private val nodeId = RandomGenerator.getDefault().nextLong(maxNodeId + 1)
    // UTC = 2024-01-01T00:00:00Z
    private val startTimeMillis = 1704067200000L

    private var lastTimeMillis = startTimeMillis
    private var sequence = 0L

    @Synchronized
    override fun nextId(): Long {
        var currentTimeMillis = System.currentTimeMillis()

        if (currentTimeMillis < lastTimeMillis) {
            throw IllegalStateException("Invalid Time")
        }

        if (currentTimeMillis == lastTimeMillis) {
            sequence = (sequence + 1) and maxSequence
            if (sequence == 0L) {
                currentTimeMillis = waitNextMillis(currentTimeMillis)
            }
        } else {
            sequence = 0
        }

        lastTimeMillis = currentTimeMillis

        return ((currentTimeMillis - startTimeMillis) shl (NODE_ID_BITS + SEQUENCE_BITS)) or
            (nodeId shl SEQUENCE_BITS) or
            sequence
    }

    private fun waitNextMillis(currentTimestamp: Long): Long {
        var timestamp = currentTimestamp
        while (timestamp <= lastTimeMillis) {
            timestamp = System.currentTimeMillis()
        }
        return timestamp
    }
}