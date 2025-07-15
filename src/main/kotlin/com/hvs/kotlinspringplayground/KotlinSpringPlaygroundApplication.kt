package com.hvs.kotlinspringplayground

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan("com.hvs.outbox")
class KotlinSpringPlaygroundApplication

fun main(args: Array<String>) {
    runApplication<KotlinSpringPlaygroundApplication>(*args)
}
