package com.preslavrachev.iviewscheduler.persistence.repository

import com.preslavrachev.iviewscheduler.persistence.entity.AvailabilityDayEntity
import com.preslavrachev.iviewscheduler.persistence.entity.UserEntity
import org.springframework.data.mongodb.repository.MongoRepository
import java.time.LocalDate

interface AppointmentDayEntityRepository: MongoRepository<AvailabilityDayEntity, String> {

    fun findOneByDayAndUser(day: LocalDate, user: UserEntity): AvailabilityDayEntity?
    fun findAllByUserIn(users: List<UserEntity>): List<AvailabilityDayEntity>
    fun findAllByDayInAndUserIn(days: List<LocalDate>, users: List<UserEntity>): List<AvailabilityDayEntity>
}
