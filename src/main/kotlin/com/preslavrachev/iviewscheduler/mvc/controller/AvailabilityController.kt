package com.preslavrachev.iviewscheduler.mvc.controller

import com.preslavrachev.iviewscheduler.business.AvailabilityService
import com.preslavrachev.iviewscheduler.business.model.TimeSlot
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.LocalTime

@RestController
@RequestMapping("/appointment")
class AvailabilityController(val availabilityService: AvailabilityService) {

    data class AppointmentAddRequest(val userId: String,
                                     val days: List<LocalDate>,
                                     val start: LocalTime,
                                     val end: LocalTime)

    @PostMapping
    fun addAppointment(request: AppointmentAddRequest) {
        val timeSlots = request.days.map { TimeSlot(it, request.start, request.end) }
        availabilityService.addNewAppointment(request.userId, timeSlots)
    }

    @GetMapping("/search")
    fun findAvaliableTimeSlots(@RequestParam("candidateId") candidateId: String,
                               @RequestParam("interviewerIds") interviewerIds: List<String>): List<TimeSlot> {
        return availabilityService.returnOpenTimeSlots(interviewerIds.plus(candidateId))
    }
}
