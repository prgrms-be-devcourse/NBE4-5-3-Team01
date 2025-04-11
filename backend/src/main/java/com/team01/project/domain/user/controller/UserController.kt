package com.team01.project.domain.user.controller

import com.team01.project.domain.follow.controller.dto.FollowResponse
import com.team01.project.domain.notification.service.NotificationService
import com.team01.project.domain.user.dto.CalendarVisibilityUpdateRequest
import com.team01.project.domain.user.dto.SimpleUserResponse
import com.team01.project.domain.user.dto.UserDto
import com.team01.project.domain.user.service.UserService
import com.team01.project.global.dto.RsData
import com.team01.project.global.security.JwtTokenProvider
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.hibernate.query.sqm.tree.SqmNode.log
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.multipart.MultipartFile
import java.time.LocalTime

@Tag(name = "Users", description = "유저 API")
@RequestMapping("/user")
@Controller
class UserController(
    private val jwtTokenProvider: JwtTokenProvider,
    private val userService: UserService,
    private val notificationService: NotificationService
) {

    @Operation(summary = "로그인 api", description = "db에서 아이디 조회 후 입력한 비밀번호 검증 후 토큰 반환")
    @ResponseBody
    @PostMapping("/login")
    fun login(
        @RequestBody reqMap: Map<String, Any>,
        response: HttpServletResponse
    ): RsData<Map<String, Any>> {
        return RsData("200-1", "로그인 성공", userService.validLogin(reqMap, response))
    }

    @Operation(summary = "로그아웃 api", description = "토큰을 삭제하며 로그인 정보를 삭제")
    @ResponseBody
    @GetMapping("/logout")
    fun forceLogout(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication?
    ): ResponseEntity<*> {
        return userService.logoutService(request, response, authentication)
    }

    @Operation(summary = "jwt 재발급 api", description = "리프레시 토큰을 이용한 jwt를 재발급 한다.")
    @ResponseBody
    @PostMapping("/refresh")
    fun refreshToken(@RequestBody reqMap: Map<String, Any>): RsData<Map<String, Any>> {
        val refreshToken = reqMap["refreshToken"].toString()
        val rsToken = userService.refreshToken(refreshToken)
        return RsData("200-1", "토큰을 재발급 합니다.", rsToken)
    }

    @ResponseBody
    @GetMapping("/testApi")
    fun testApi(@AuthenticationPrincipal user: org.springframework.security.oauth2.core.user.OAuth2User): Map<String, String> {
        val spotifyToken = user.getAttribute<String>("spotifyToken")
        println("스포티파이 토큰체크: $spotifyToken")
        val userId = user.name
        println("유저아이디 체크: $userId")
        return mapOf("res" to "테스트 api 입니다.", "userId" to userId)
    }

    @Operation(summary = "유저 검색 api", description = "이름과 닉네임으로 유저를 검색한다.")
    @ResponseBody
    @GetMapping("/search")
    fun search(
        @RequestParam("q") name: String,
        @AuthenticationPrincipal user: org.springframework.security.oauth2.core.user.OAuth2User
    ): RsData<List<FollowResponse>> {
        val result = userService.search(user.name, name)
        return RsData("200-1", "유저 검색이 완료되었습니다.", result)
    }

    @Operation(summary = "유저 조회 api", description = "현재 로그인한 유저의 정보를 조회한다.")
    @ResponseBody
    @GetMapping("/byToken")
    fun getUserByToken(@RequestHeader("Authorization") accessToken: String): SimpleUserResponse {
        val token = if (accessToken.startsWith("Bearer ")) accessToken.substring(7) else accessToken
        val userId = jwtTokenProvider.getUserIdFromToken(token)
        return SimpleUserResponse.from(userService.getUserById(userId))
    }

    @Operation(summary = "유저 정보 api", description = "현재 로그인한 유저의 정보를 가져온다.")
    @ResponseBody
    @GetMapping("/getUsers")
    fun getUser(@AuthenticationPrincipal oAuth2User: org.springframework.security.oauth2.core.user.OAuth2User): RsData<UserDto> {
        val userId = oAuth2User.name
        val user = userService.getUserById(userId)
        return RsData("200-1", "유저 정보 조회 완료.", UserDto.from(user))
    }

    @Operation(summary = "자기소개 변경 api", description = "현재 로그인한 유저의 자기소개를 수정한다.")
    @ResponseBody
    @PutMapping("/userIntro")
    fun userIntro(
        @AuthenticationPrincipal oAuth2User: org.springframework.security.oauth2.core.user.OAuth2User,
        @RequestBody reqMap: Map<String, Any>
    ): RsData<Void> {
        val userIntro = reqMap["userIntro"].toString()
        val userId = oAuth2User.name
        userService.updateUserIntro(userId, userIntro)
        return RsData("200-1", "user intro modified")
    }

    @Operation(summary = "이름 변경 api", description = "현재 로그인한 유저의 이름을 수정한다.")
    @ResponseBody
    @PutMapping("/profileName")
    fun changeProfileName(
        @AuthenticationPrincipal oAuth2User: org.springframework.security.oauth2.core.user.OAuth2User,
        @RequestBody reqMap: Map<String, Any>
    ): RsData<Void> {
        val profileName = reqMap["name"].toString()
        val userId = oAuth2User.name
        userService.updateProfileName(userId, profileName)
        return RsData("200-1", "user name modified")
    }

    @Operation(summary = "이미지 변경 api", description = "현재 로그인한 유저의 프로필 사진을 변경한다.")
    @ResponseBody
    @PostMapping("/image")
    fun uploadImage(
        @AuthenticationPrincipal oAuth2User: org.springframework.security.oauth2.core.user.OAuth2User,
        @RequestParam("image") file: MultipartFile
    ): RsData<String> {
        val userId = oAuth2User.name
        return try {
            val savedFileInfo = userService.uploadImage(userId, file)
            RsData("200-1", "File uploaded successfully.", savedFileInfo)
        } catch (e: Exception) {
            RsData("500", "error")
        }
    }

    @Operation(summary = "유저 조회 api", description = "아이디가 user-id인 유저의 정보를 조회한다.")
    @ResponseBody
    @GetMapping("/{user-id}")
    fun getUserByUserId(@PathVariable("user-id") userId: String): RsData<SimpleUserResponse> {
        return RsData(
            "200-15",
            "유저 정보 조회에 성공했습니다.",
            SimpleUserResponse.from(userService.getUserById(userId))
        )
    }

    @Operation(summary = "유저 조회 api", description = "현재 로그인한 유저의 정보를 조회한다.")
    @ResponseBody
    @GetMapping("/byCookie")
    fun getUserByCookie(@CookieValue("accessToken") accessToken: String): RsData<SimpleUserResponse> {
        val userId = jwtTokenProvider.getUserIdFromToken(accessToken)
        return RsData(
            "200-15",
            "유저 정보 조회에 성공했습니다.",
            SimpleUserResponse.from(userService.getUserById(userId))
        )
    }

    @Operation(summary = "Spotify Token 반환", description = "쿠키에 존재하는 Spotify Token 반환한다.")
    @ResponseBody
    @GetMapping("/spotify-token")
    fun getSpotifyToken(@AuthenticationPrincipal user: org.springframework.security.oauth2.core.user.OAuth2User): ResponseEntity<String> {
        val spotifyToken = user.getAttribute<String>("spotifyToken")
        return ResponseEntity.ok(spotifyToken)
    }

    @Operation(summary = "회원가입 api", description = "회원가입 기능")
    @ResponseBody
    @PostMapping("/signup")
    fun userSignUp(@RequestBody body: UserDto): RsData<UserDto> {
        val savedUser = userService.addUser(body)
        val userDto = UserDto.from(savedUser)

        notificationService.createDefaultNotifications(savedUser)
        log.info("${savedUser.name}님의 알림이 생성되었습니다.")

        notificationService.initLoginNotifications(LocalTime.now(), savedUser)

        return RsData("200-1", "등록된 유저", userDto)
    }

    @Operation(summary = "아이디 중복 확인 api", description = "회원가입 아이디 중복 확인")
    @ResponseBody
    @GetMapping("/check-duplicate")
    fun checkDuplicate(@RequestParam("checkId") checkId: String): RsData<Boolean> {
        var exists = userService.existsByLoginId(checkId)
        exists = if (exists) false else true
        return RsData("200-1", "아이디 중복 체크", exists)
    }

    @Operation(summary = "캘린더 공개 여부 수정 api", description = "현재 로그인한 유저의 캘린더 공개 여부를 수정한다.")
    @ResponseBody
    @PatchMapping("/calendar-visibility")
    fun updateCalendarVisibility(
        @RequestBody(required = false) requestDto: CalendarVisibilityUpdateRequest?,
        @AuthenticationPrincipal user: org.springframework.security.oauth2.core.user.OAuth2User
    ): RsData<Void> {
        val userId = user.name
        if (requestDto?.calendarVisibility != null) {
            userService.updateCalendarVisibility(userId, requestDto.calendarVisibility)
        }
        return RsData("200-14", "캘린더 공개 여부 수정이 완료되었습니다.")
    }
}
