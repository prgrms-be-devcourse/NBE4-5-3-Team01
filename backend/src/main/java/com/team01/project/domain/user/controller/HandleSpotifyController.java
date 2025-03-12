package com.team01.project.domain.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HandleSpotifyController {

	@RequestMapping("/login/oauth2/code/spotify")
	public String handleSpotifyCallback(@RequestParam("code") String code) {
		System.out.println("Received Authorization Code: " + code);

		// 여기서 code를 사용하여 액세스 토큰을 요청하는 로직을 추가합니다.
		// 예를 들어, code를 사용하여 Spotify API에 액세스 토큰을 요청하고,
		// 액세스 토큰을 받아 사용자 정보를 처리하는 등의 작업을 진행합니다.

		// 액세스 토큰을 받아 성공적으로 처리되면, 사용자를 애플리케이션 내부로 리다이렉트합니다.
		return "redirect:/";  // 로그인 후 리다이렉트할 URL
	}

	// @RequestMapping("/error")
	// public String handleError(HttpServletRequest request) {
	// 	Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
	// 	// 추가적인 에러 로직을 처리할 수 있습니다.
	// 	return "error";  // "error" 뷰로 에러 페이지를 반환
	// }

}
