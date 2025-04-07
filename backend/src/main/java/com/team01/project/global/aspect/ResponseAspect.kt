package com.team01.project.global.aspect

import com.team01.project.global.dto.RsData
import jakarta.servlet.http.HttpServletResponse
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Aspect
@Component
class ResponseAspect(
    private val response: HttpServletResponse
) {

    @Around(
        """
        (
            within(@org.springframework.web.bind.annotation.RestController *) &&
            (
                @annotation(org.springframework.web.bind.annotation.GetMapping) ||
                @annotation(org.springframework.web.bind.annotation.PostMapping) ||
                @annotation(org.springframework.web.bind.annotation.PutMapping) ||
                @annotation(org.springframework.web.bind.annotation.DeleteMapping) ||
                @annotation(org.springframework.web.bind.annotation.PatchMapping)
            )
        )
        ||
        @annotation(org.springframework.web.bind.annotation.ResponseBody)
        """
    )
    fun responseAspect(joinPoint: ProceedingJoinPoint): Any? {
        val result = joinPoint.proceed()

        if (result is RsData<*>) {
            response.status = result.statusCode
        }

        return result
    }
}
