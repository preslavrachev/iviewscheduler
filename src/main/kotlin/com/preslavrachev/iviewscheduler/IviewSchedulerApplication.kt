package com.preslavrachev.iviewscheduler

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Configuration

@SpringBootApplication
@Configuration
class IviewSchedulerApplication

fun main(args: Array<String>) {
    runApplication<IviewSchedulerApplication>(*args)
}
