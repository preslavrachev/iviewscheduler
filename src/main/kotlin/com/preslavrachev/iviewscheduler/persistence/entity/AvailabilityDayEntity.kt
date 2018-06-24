package com.preslavrachev.iviewscheduler.persistence.entity

import com.preslavrachev.iviewscheduler.business.model.AvailabilityDay
import com.preslavrachev.iviewscheduler.business.model.TimeRange
import net.onedaybeard.bitvector.BitVector
import net.onedaybeard.bitvector.bitsOf
import org.springframework.data.annotation.Id
import java.time.LocalDate

/**
 * A representation of the availability for interview during a given day
 */
data class AvailabilityDayEntity(@Id var id: String? = null,
                                 val day: LocalDate,
                                 val user: UserEntity,
                                 val availabilityBits: BitVector) {

    /**
     * Convenience constructor
     */
    constructor(day: LocalDate,
                user: UserEntity,
                timeRange: TimeRange): this(null,  day, user, AvailabilityDay.toBitMask(timeRange))

    fun addTimeRange(timeRange: TimeRange) {
        val newTimeRangeBitMask = AvailabilityDay.toBitMask(timeRange)
        availabilityBits.or(newTimeRangeBitMask)
    }
}
