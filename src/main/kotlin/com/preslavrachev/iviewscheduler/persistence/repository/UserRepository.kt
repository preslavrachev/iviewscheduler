package com.preslavrachev.iviewscheduler.persistence.repository

import com.preslavrachev.iviewscheduler.persistence.entity.UserEntity
import org.springframework.data.mongodb.repository.MongoRepository

interface UserRepository: MongoRepository<UserEntity, String>
