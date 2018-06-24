package com.preslavrachev.iviewscheduler.business.model

import java.time.LocalDate
import java.time.LocalTime

data class TimeSlot(val day: LocalDate,
                    val start: LocalTime,
                    val end: LocalTime) {

    companion object {
        const val START_OF_THE_BUSINESS_DAY = 9
        const val END_OF_THE_BUSINESS_DAY = 18
    }

    init {
        assert(start.hour >= START_OF_THE_BUSINESS_DAY &&
                end.hour <= END_OF_THE_BUSINESS_DAY &&
                start.minute == 0 &&
                end.minute == 0)
    }

    fun durationInMinutes(): Int {
        return end.hour.minus(start.hour) * 60
    }
}
