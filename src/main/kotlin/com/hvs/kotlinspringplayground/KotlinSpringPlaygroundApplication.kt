package com.hvs.kotlinspringplayground

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication(exclude = [SecurityAutoConfiguration::class])
class KotlinSpringPlaygroundApplication

fun main(args: Array<String>) {
    runApplication<KotlinSpringPlaygroundApplication>(*args)
}
