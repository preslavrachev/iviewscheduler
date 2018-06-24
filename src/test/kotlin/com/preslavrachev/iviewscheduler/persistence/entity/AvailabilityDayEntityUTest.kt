package com.preslavrachev.iviewscheduler.persistence.entity

import net.onedaybeard.bitvector.bitsOf
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.Test
import java.time.LocalDate

class AvailabilityDayEntityUTest {

    /**
     * Tests that when matching candidate appointment against those of an interviewer,
     * the result is a new bit vector, containing only the ones where both parties have an open spot
     */
    @Test
    fun testBitManipulation () {

        val day = LocalDate.now()
        val candidateDay = AvailabilityDay(day, TimeRange(8, 13))
        val interviewerDay = AvailabilityDay(day, TimeRange(10, 11))

        val bits = candidateDay.availabilityBits.copy()
        bits.andNot(interviewerDay.availabilityBits)

        // NOTE: `is` is a reserved word in Kotlin
        Assert.assertThat(bits, Matchers.`is`(bitsOf(8, 9, 11, 12)))
    }
}
