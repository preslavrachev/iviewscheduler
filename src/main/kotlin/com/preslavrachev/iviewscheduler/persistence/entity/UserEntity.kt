package com.preslavrachev.iviewscheduler.persistence.entity

import com.preslavrachev.iviewscheduler.business.model.UserType
import org.springframework.data.annotation.Id

/**
 * An entity that represents a persistable user.
 *
 * NOTE: There is no business model super class for this entity, since it has not been needed for this demo
 * Data class inheritance is not allowed by default, and can be overcome with delegation:
 * https://kotlinlang.org/docs/reference/delegation.html
 */
data class UserEntity(@Id var id: String? = null,
                      val firstName: String,
                      val lastName: String,
                      val type: UserType)
