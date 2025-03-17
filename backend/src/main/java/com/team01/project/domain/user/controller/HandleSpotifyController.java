package com.team01.project.domain.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HandleSpotifyController {

	@RequestMapping("/login/oauth2/code/spotify")
	public String handleSpotifyCallback(@RequestParam("code") String code) {
		System.out.println("Received Authorization Code: " + code);

		return "redirect:/";
	}
}
