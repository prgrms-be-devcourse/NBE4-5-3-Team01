package com.team01.project.domain.user.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class HandleSpotifyController {

    @RequestMapping("/login/oauth2/code/spotify")
    fun handleSpotifyCallback(@RequestParam("code") code: String): String {
        println("Received Authorization Code: $code")
        return "redirect:/"
    }
}