package com.preslavrachev.iviewscheduler.business.model

import net.onedaybeard.bitvector.BitVector
import net.onedaybeard.bitvector.bitsOf
import java.time.LocalDate

data class TimeRange(val start: Int, val end: Int)

/**
 * A data model for representing user-agnostic daily availability
 */
data class AvailabilityDay(val day: LocalDate,
                           val availabilityBits: BitVector) {
    companion object {
        fun toBitMask(timeRange: TimeRange): BitVector {
            val bitsRange = (timeRange.start until timeRange.end).distinct().toIntArray()
            return bitsOf(*bitsRange)
        }
    }

    /**
     * Convenience constructor
     */
    constructor(day: LocalDate,
                timeRange: TimeRange): this(day, toBitMask(timeRange))
}
