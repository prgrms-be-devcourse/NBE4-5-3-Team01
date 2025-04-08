package com.team01.project

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class ProjectApplication

fun main(args: Array<String>) {
    SpringApplication.run(ProjectApplication::class.java, *args)
}
