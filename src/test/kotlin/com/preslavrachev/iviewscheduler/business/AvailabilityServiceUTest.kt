package com.preslavrachev.iviewscheduler.business

import com.preslavrachev.iviewscheduler.business.model.TimeSlot
import com.preslavrachev.iviewscheduler.business.model.UserType
import com.preslavrachev.iviewscheduler.persistence.entity.AvailabilityDayEntity
import com.preslavrachev.iviewscheduler.persistence.entity.TimeRange
import com.preslavrachev.iviewscheduler.persistence.entity.UserEntity
import com.preslavrachev.iviewscheduler.persistence.repository.AppointmentDayEntityRepository
import com.preslavrachev.iviewscheduler.persistence.repository.UserRepository
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.time.LocalDate
import java.time.LocalTime
import java.time.Month

internal class AvailabilityServiceUTest {

    companion object {
        val MONDAY: LocalDate = LocalDate.of(2018, Month.MAY, 20)
        val TUESDAY: LocalDate = MONDAY.plusDays(1)
        val WEDNESDAY: LocalDate = MONDAY.plusDays(2)
        val THURSDAY: LocalDate = MONDAY.plusDays(3)
        val FRIDAY: LocalDate = MONDAY.plusDays(4)
    }

    private lateinit var availabilityService: AvailabilityService
    private lateinit var userRepository: UserRepository
    private lateinit var appointmentDayEntityRepository: AppointmentDayEntityRepository

    @Before
    fun setUp() {
        userRepository = mock(UserRepository::class.java)
        appointmentDayEntityRepository = mock(AppointmentDayEntityRepository::class.java)

        availabilityService = AvailabilityService(userRepository, appointmentDayEntityRepository)
    }

    @Test
    fun testReturnOpenTimeSlots() {

        val (candidate, candidateAvailability) = createCandidateAndAvailability()
        val (firstInterviewer, firstInterviewerAvailability) = createFirstInterviewerAndAvailability()
        val (secondInterviewer, secondInterviewerAvailability) = createSecondInterviewerAndAvailability()
        val users = listOfNotNull(candidate, firstInterviewer, secondInterviewer)
        val userIds = users.mapNotNull(UserEntity::id)
        `when`(userRepository.findAllById(userIds)).thenReturn(users)
        `when`(appointmentDayEntityRepository.findAllByUserIn(users)).thenReturn(
                candidateAvailability
                        .plus(firstInterviewerAvailability)
                        .plus(secondInterviewerAvailability))

        val resultTimeSlots = availabilityService.returnOpenTimeSlots(userIds)

        val expectedTimeSlot1 = TimeSlot(TUESDAY, LocalTime.of(9, 0), LocalTime.of(10, 0))
        val expectedTimeSlot2 = TimeSlot(THURSDAY, LocalTime.of(9, 0), LocalTime.of(10, 0))

        print(resultTimeSlots)
        Assert.assertThat(resultTimeSlots, Matchers.contains(expectedTimeSlot1, expectedTimeSlot2))
    }

    private fun createCandidateAndAvailability(): Pair<UserEntity, List<AvailabilityDayEntity>> {

        val candidate = UserEntity(id = "cand1", firstName = "Candidate", lastName = "One", type = UserType.CANDIDATE)

        return Pair(candidate, listOf(
                AvailabilityDayEntity(MONDAY, candidate, TimeRange(9, 10)),
                AvailabilityDayEntity(TUESDAY, candidate, TimeRange(9, 10)),
                AvailabilityDayEntity(WEDNESDAY, candidate, TimeRange(9, 10)),
                AvailabilityDayEntity(WEDNESDAY, candidate, TimeRange(10, 12)),
                AvailabilityDayEntity(THURSDAY, candidate, TimeRange(9, 10)),
                AvailabilityDayEntity(FRIDAY, candidate, TimeRange(9, 10))
        ))
    }

    private fun createFirstInterviewerAndAvailability(): Pair<UserEntity, List<AvailabilityDayEntity>> {

        val interviewer1 = UserEntity(id = "iview1", firstName = "Interviewer", lastName = "One", type = UserType.INTERVIEWER)
        return Pair(interviewer1, listOf(
                AvailabilityDayEntity(MONDAY, interviewer1, TimeRange(9, 16)),
                AvailabilityDayEntity(TUESDAY, interviewer1, TimeRange(9, 16)),
                AvailabilityDayEntity(WEDNESDAY, interviewer1, TimeRange(9, 16)),
                AvailabilityDayEntity(THURSDAY, interviewer1, TimeRange(9, 16)),
                AvailabilityDayEntity(FRIDAY, interviewer1, TimeRange(9, 16))
        ))
    }

    private fun createSecondInterviewerAndAvailability(): Pair<UserEntity, List<AvailabilityDayEntity>> {

        val interviewer2 = UserEntity(id = "iview2", firstName = "Interviewer", lastName = "Two", type = UserType.INTERVIEWER)
        return Pair(interviewer2, listOf(
                AvailabilityDayEntity(MONDAY, interviewer2, TimeRange(12, 18)),
                AvailabilityDayEntity(TUESDAY, interviewer2, TimeRange(9, 12)),
                AvailabilityDayEntity(WEDNESDAY, interviewer2, TimeRange(12, 18)),
                AvailabilityDayEntity(THURSDAY, interviewer2, TimeRange(9, 12))
        ))
    }

}
