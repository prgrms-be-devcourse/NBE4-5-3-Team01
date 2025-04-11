package com.team01.project.calendardate.service

import com.team01.project.calendardate.fixture.CalendarDateFixture
import com.team01.project.domain.calendardate.controller.dto.response.CalendarDateFetchResponse
import com.team01.project.domain.calendardate.entity.CalendarDate
import com.team01.project.domain.calendardate.repository.CalendarDateRepository
import com.team01.project.domain.calendardate.service.CalendarDateService
import com.team01.project.domain.music.entity.Music
import com.team01.project.domain.musicrecord.service.MusicRecordService
import com.team01.project.domain.user.repository.UserRepository
import com.team01.project.global.exception.CalendarDateAlreadyExistsException
import com.team01.project.global.exception.PermissionDeniedException
import com.team01.project.global.permission.CalendarPermission
import com.team01.project.global.permission.PermissionService
import com.team01.project.user.entity.UserFixture
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.springframework.context.ApplicationEventPublisher
import java.time.LocalDate
import java.time.YearMonth
import java.util.Optional

class CalendarDateServiceTest : BehaviorSpec({
    val calendarDateRepository: CalendarDateRepository = mockk()
    val userRepository: UserRepository = mockk()
    val permissionService: PermissionService = mockk()
    val eventPublisher: ApplicationEventPublisher = mockk(relaxed = true)
    val musicRecordService: MusicRecordService = mockk()

    val calendarDateService = CalendarDateService(
        calendarDateRepository,
        userRepository,
        permissionService,
        eventPublisher,
        musicRecordService
    )

    val yearMonth = YearMonth.of(2025, 4)
    val start = yearMonth.atDay(1)
    val end = yearMonth.atEndOfMonth()

    val owner = UserFixture.유저("owner")
    val user = UserFixture.유저("user")

    val calendarDate1Id = 1L
    val calendarDate1 = CalendarDateFixture.calendarDate(id = calendarDate1Id, user = owner, date = start)
    val calendarDate2 = CalendarDateFixture.calendarDate(id = 2L, user = owner, date = end)
    val calendarDates = listOf(calendarDate1, calendarDate2)

    // 공통 모킹
    beforeContainer {
        clearAllMocks()
        every { userRepository.findById(owner.id) } returns Optional.of(owner)
        every { userRepository.findById(user.id) } returns Optional.of(user)
    }

    Given("유저가 캘린더 VIEW 권한을 가지는 경우") {
        beforeTest {
            every { permissionService.checkPermission(eq(owner), eq(user)) } returns CalendarPermission.VIEW
        }

        When("연도와 월로 먼슬리 캘린더를 조회하면") {
            beforeTest {
                every { calendarDateRepository.findByUserAndDateBetween(owner, start, end) } returns calendarDates
            }

            val result by lazy {
                calendarDateService.findAllByYearAndMonth(owner.id, user.id, yearMonth)
            }

            Then("해당 연도와 월의 모든 캘린더 데이터가 반환된다") {
                result shouldHaveSize calendarDates.size
                result.all { it.user.id == owner.id } shouldBe true
            }
        }

        And("캘린더 아이디에 해당하는 캘린더가 존재하는 상태에서") {
            val musics = emptyList<Music>()
            val response = CalendarDateFetchResponse.of(calendarDate1, musics, CalendarPermission.VIEW)

            beforeTest {
                every { calendarDateRepository.findWithOwnerById(calendarDate1Id) } returns Optional.of(calendarDate1)
                every { musicRecordService.findMusicsByCalendarDateId(calendarDate1Id) } returns musics
            }

            When("캘린더 아이디로 캘린더를 조회하면") {
                val result by lazy {
                    calendarDateService.findCalendarDateWithMusics(calendarDate1Id, user.id)
                }

                Then("해당 아이디의 캘린더 데이터가 반환된다") {
                    result shouldBe response
                }
            }
        }
    }

    Given("유저가 캘린더 VIEW 권한을 가지지 않은 경우") {
        beforeTest {
            every { permissionService.checkPermission(eq(owner), eq(user)) } returns CalendarPermission.NONE
        }

        When("연도와 월로 먼슬리 캘린더를 조회하면") {
            Then("PermissionDeniedException이 발생한다") {
                shouldThrow<PermissionDeniedException> {
                    calendarDateService.findAllByYearAndMonth(owner.id, user.id, yearMonth)
                }
            }
        }

        When("캘린더 아이디로 캘린더를 조회하면") {
            beforeTest {
                every { calendarDateRepository.findWithOwnerById(calendarDate1Id) } returns Optional.of(calendarDate1)
            }

            Then("PermissionDeniedException이 발생한다") {
                shouldThrow<PermissionDeniedException> {
                    calendarDateService.findCalendarDateWithMusics(calendarDate1Id, user.id)
                }
            }
        }
    }

    Given("유저가 캘린더 EDIT 권한을 가지는 경우") {
        val newMemo = "new memo"

        beforeTest {
            every { permissionService.checkPermission(eq(owner), eq(user)) } returns CalendarPermission.EDIT
        }

        And("캘린더 아이디에 해당하는 캘린더가 존재하는 상태에서") {
            beforeTest {
                every { calendarDateRepository.findWithOwnerById(calendarDate1Id) } returns Optional.of(calendarDate1)
            }

            When("캘린더 메모를 수정하면") {
                val result by lazy {
                    calendarDateService.updateMemo(calendarDate1Id, user.id, newMemo)
                }

                Then("메모 내용이 변경된다") {
                    result
                    calendarDate1.memo shouldBe newMemo
                }
            }
        }
    }

    Given("유저가 캘린더 EDIT 권한을 가지지 않은 경우") {
        val newMemo = "new memo"

        beforeTest {
            every { calendarDateRepository.findWithOwnerById(calendarDate1Id) } returns Optional.of(calendarDate1)
            every { permissionService.checkPermission(eq(owner), eq(user)) } returns CalendarPermission.VIEW
        }

        When("캘린더 메모를 수정하면") {
            Then("PermissionDeniedException이 발생한다") {
                shouldThrow<PermissionDeniedException> {
                    calendarDateService.updateMemo(calendarDate1Id, user.id, newMemo)
                }
            }
        }
    }

    Given("캘린더가 생성되지 않은 날짜에") {
        val date = LocalDate.now()
        val memo = "memo"

        beforeTest {
            every { calendarDateRepository.existsByUserAndDate(user, date) } returns false
            every { calendarDateRepository.save(any<CalendarDate>()) } answers { firstArg() }
        }

        When("유저 아이디로 캘린더를 생성하면") {
            val result = calendarDateService.create(user.id, date, memo)

            Then("생성 후 저장된 캘린더 데이터가 반환된다") {
                result shouldNotBe null
                result.user.id shouldBe user.id
                result.date shouldBe date
                result.memo shouldBe memo
            }
        }
    }

    Given("캘린더가 이미 존재하는 날짜에") {
        val date = LocalDate.now()
        val memo = "memo"

        beforeTest {
            every { calendarDateRepository.existsByUserAndDate(user, date) } returns true
        }

        When("유저 아이디로 캘린더를 생성하면") {
            Then("CalendarDateAlreadyExistsException이 발생한다") {
                shouldThrow<CalendarDateAlreadyExistsException> {
                    calendarDateService.create(user.id, date, memo)
                }
            }
        }
    }

    Given("캘린더가 존재하지 않는 경우") {
        beforeTest {
            every { calendarDateRepository.findWithOwnerById(calendarDate1Id) } returns Optional.empty()
        }

        When("캘린더 아이디로 캘린더를 조회하면") {
            Then("IllegalArgumentException이 발생한다") {
                shouldThrow<IllegalArgumentException> {
                    calendarDateService.findCalendarDateWithMusics(calendarDate1Id, user.id)
                }
            }
        }

        When("캘린더 메모를 수정하면") {
            val newMemo = "new memo"

            Then("IllegalArgumentException이 발생한다") {
                shouldThrow<IllegalArgumentException> {
                    calendarDateService.updateMemo(calendarDate1Id, user.id, newMemo)
                }
            }
        }
    }
})
