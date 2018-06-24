package com.preslavrachev.iviewscheduler.business

import com.preslavrachev.iviewscheduler.business.model.AvailabilityDay
import com.preslavrachev.iviewscheduler.business.model.TimeRange
import com.preslavrachev.iviewscheduler.business.model.TimeSlot
import com.preslavrachev.iviewscheduler.persistence.entity.AvailabilityDayEntity
import com.preslavrachev.iviewscheduler.persistence.repository.AppointmentDayEntityRepository
import com.preslavrachev.iviewscheduler.persistence.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalTime

@Service
class AvailabilityService(val userRepository: UserRepository,
                          val appointmentDayEntityRepository: AppointmentDayEntityRepository) {

    @Transactional
    fun addNewAppointment(userId: String, timeSlots: List<TimeSlot>) {

        val user = userRepository.findById(userId).orElseThrow { IllegalArgumentException("User not found!") }

        timeSlots.forEach { timeSlot ->
            /* Checks, whether there is an existing appointment in the DB */
            val timeRange = TimeRange(timeSlot.start.hour, timeSlot.end.hour)
            val appointmentDayEntity = appointmentDayEntityRepository.findOneByDayAndUser(timeSlot.day, user)
                    .apply { this?.addTimeRange(timeRange) } ?: AvailabilityDayEntity(timeSlot.day, user, timeRange)

            appointmentDayEntityRepository.save(appointmentDayEntity)
        }
    }

    /**
     * By passing a list of user IDs, a list of time slots is returned, when all parties can be present
     * Filters out multiple interviewer availability days, and groups them by day
     * NOTE: Only days, when all interviewers can be present are included. The rest will be filtered out
     */
    fun returnOpenTimeSlots(userIds: List<String>): List<TimeSlot> {

        val users = userRepository.findAllById(userIds).toList()
        val appointmentDays = appointmentDayEntityRepository.findAllByUserIn(users)

        return appointmentDays
                .map { it.asAppointmentDay() }
                .groupBy { it.day }
                .filter { it.value.size == users.size }
                .map { (day, userAvailabilities) -> mergeAvailabilityBitsOnGivenDay(userAvailabilities, day) }
                .flatMap { it.asTimeSlotList() }
                /* A nice multi-field sorting trick */
                .sortedWith(compareBy({ it.day }, { it.start }, { it.end }))
    }

    private fun mergeAvailabilityBitsOnGivenDay(userAvailabilities: List<AvailabilityDay>, day: LocalDate): AvailabilityDay {
        val mergedBits = userAvailabilities.first().availabilityBits.copy()
        userAvailabilities.forEach { ivDay -> mergedBits.and(ivDay.availabilityBits) }
        return AvailabilityDay(day, mergedBits)
    }

    /**
     * A helper extension method
     */
    private fun AvailabilityDayEntity.asAppointmentDay(): AvailabilityDay {
        return AvailabilityDay(day, availabilityBits)
    }

    /**
     * A helper extension method
     */
    private fun AvailabilityDay.asTimeSlotList(): List<TimeSlot> {
        val timeSlotList = mutableListOf<TimeSlot>()
        availabilityBits.forEachBit { timeSlotList.add(TimeSlot(day, LocalTime.of(it, 0), LocalTime.of(it + 1, 0))) }
        return timeSlotList
    }
}
