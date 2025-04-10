package com.team01.project.musicrecord.service

import com.team01.project.calendardate.fixture.CalendarDateFixture
import com.team01.project.domain.calendardate.repository.CalendarDateRepository
import com.team01.project.domain.music.repository.MusicRepository
import com.team01.project.domain.musicrecord.entity.MusicRecord
import com.team01.project.domain.musicrecord.repository.MusicRecordRepository
import com.team01.project.domain.musicrecord.service.MusicRecordService
import com.team01.project.domain.user.repository.UserRepository
import com.team01.project.global.exception.PermissionDeniedException
import com.team01.project.global.permission.CalendarPermission
import com.team01.project.global.permission.PermissionService
import com.team01.project.music.fixture.MusicFixture
import com.team01.project.musicrecord.fixture.MusicRecordFixture
import com.team01.project.user.entity.UserFixture
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.optional.shouldBePresent
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import java.util.Optional

class MusicRecordServiceTest : BehaviorSpec({
    val musicRecordRepository: MusicRecordRepository = mockk()
    val calendarDateRepository: CalendarDateRepository = mockk()
    val musicRepository: MusicRepository = mockk()
    val userRepository: UserRepository = mockk()
    val permissionService: PermissionService = mockk()

    val musicRecordService = MusicRecordService(
        musicRecordRepository,
        calendarDateRepository,
        musicRepository,
        userRepository,
        permissionService
    )

    val userId = "owner"
    val user = UserFixture.유저(userId)

    val calendarDateId = 1L
    val calendarDate = CalendarDateFixture.calendarDate(id = calendarDateId, user = user)

    val music1Id = "music 1 id"
    val music2Id = "music 2 id"
    val music3Id = "music 3 id"
    val music1 = MusicFixture.music(music1Id)
    val music2 = MusicFixture.music(music2Id)
    val music3 = MusicFixture.music(music3Id)
    val newMusicIds = listOf(music2Id, music3Id)

    val musicRecords = listOf(
        MusicRecordFixture.musicRecord(calendarDate = calendarDate, music = music1),
        MusicRecordFixture.musicRecord(calendarDate = calendarDate, music = music2)
    )

    // 공통 모킹
    beforeContainer {
        every { musicRecordRepository.findByCalendarDate(calendarDate) } returns musicRecords
        every { userRepository.findById(userId) } returns Optional.of(user)
    }

    Given("캘린더 아이디에 해당하는 캘린더가 존재하는 경우") {
        every { calendarDateRepository.findWithOwnerById(calendarDateId) } returns Optional.of(calendarDate)
        every { calendarDateRepository.findById(calendarDateId) } returns Optional.of(calendarDate)

        When("캘린더 아이디로 음악 리스트를 조회하면") {
            val result = musicRecordService.findMusicsByCalendarDateId(calendarDateId)

            Then("해당 캘린더와 함께 기록된 음악 리스트가 반환된다") {
                result shouldHaveSize musicRecords.size
            }
        }

        When("캘린더 아이디로 음악 기록 하나를 조회하면") {
            beforeTest {
                every { musicRecordRepository.findTopByCalendarDate(calendarDate) } returns Optional.of(musicRecords[0])
            }

            val result by lazy {
                musicRecordService.findOneByCalendarDateId(calendarDateId)
            }

            Then("음악 기록 하나가 반환된다") {
                result.shouldBePresent {
                    it shouldBe musicRecords[0]
                }
            }
        }

        And("음악 아이디에 해당하는 음악이 존재하는 상태에서") {
            beforeTest {
                every { musicRepository.findById(any<String>()) } answers {
                    when (it.invocation.args[0]) {
                        music1Id -> Optional.of(music1)
                        music2Id -> Optional.of(music2)
                        else -> Optional.empty()
                    }
                }
            }

            When("음악 기록을 생성하면") {
                val result by lazy {
                    musicRecordService.createMusicRecords(calendarDateId, listOf(music1Id, music2Id))
                }

                beforeTest {
                    every { musicRecordRepository.saveAll(any<List<MusicRecord>>()) } returnsArgument 0
                }

                Then("saveAll이 한 번 호출된다") {
                    result
                    verify(exactly = 1) { musicRecordRepository.saveAll(any<List<MusicRecord>>()) }
                }
            }

            And("유저가 캘린더 EDIT 권한을 가질 때") {
                val deletedRecordsSlot = slot<List<MusicRecord>>()
                val savedRecordsSlot = slot<List<MusicRecord>>()

                beforeTest {
                    every { permissionService.checkPermission(eq(user), eq(user)) } returns CalendarPermission.EDIT
                    every { musicRepository.findById(any<String>()) } answers {
                        when (it.invocation.args[0]) {
                            music2Id -> Optional.of(music2)
                            music3Id -> Optional.of(music3)
                            else -> Optional.empty()
                        }
                    }
                    every { musicRecordRepository.deleteAll(capture(deletedRecordsSlot)) } returns Unit
                    every { musicRecordRepository.saveAll(capture(savedRecordsSlot)) } answers { savedRecordsSlot.captured }
                }

                When("음악 기록을 수정하면") {
                    val result by lazy {
                        musicRecordService.updateMusicRecords(calendarDateId, userId, newMusicIds)
                    }

                    Then("새 리스트에 없는 기존 음악 기록은 삭제된다") {
                        result
                        deletedRecordsSlot.isCaptured shouldBe true
                        val deletedIds = deletedRecordsSlot.captured.map { it.music.id }
                        deletedIds shouldBe listOf(music1Id)
                    }

                    Then("새 리스트에 추가된 음악만 새롭게 저장된다") {
                        result
                        savedRecordsSlot.isCaptured shouldBe true
                        val savedIds = savedRecordsSlot.captured.map { it.music.id }
                        savedIds shouldBe listOf(music3Id)
                    }
                }
            }
        }
    }

    Given("캘린더 아이디에 해당하는 캘린더가 존재하지 않는 경우") {
        every { calendarDateRepository.findWithOwnerById(calendarDateId) } returns Optional.empty()
        every { calendarDateRepository.findById(calendarDateId) } returns Optional.empty()

        When("캘린더 아이디로 음악 리스트를 조회하면") {
            Then("IllegalArgumentException이 발생한다") {
                shouldThrow<IllegalArgumentException> {
                    musicRecordService.findMusicsByCalendarDateId(calendarDateId)
                }
            }
        }

        When("캘린더 아이디로 음악 기록 하나를 조회하면") {
            Then("IllegalArgumentException이 발생한다") {
                shouldThrow<IllegalArgumentException> {
                    musicRecordService.findOneByCalendarDateId(calendarDateId)
                }
            }
        }

        When("음악 기록을 생성하면") {
            Then("IllegalArgumentException이 발생한다") {
                shouldThrow<IllegalArgumentException> {
                    musicRecordService.createMusicRecords(calendarDateId, listOf(music1Id, music2Id))
                }
            }
        }

        When("음악 기록을 수정하면") {
            Then("IllegalArgumentException이 발생한다") {
                shouldThrow<IllegalArgumentException> {
                    musicRecordService.updateMusicRecords(calendarDateId, userId, newMusicIds)
                }
            }
        }
    }

    Given("음악 아이디에 해당하는 음악이 존재하지 않을 때") {
        beforeTest {
            every { permissionService.checkPermission(eq(user), eq(user)) } returns CalendarPermission.EDIT
            every { musicRepository.findById(any<String>()) } returns Optional.empty()
        }

        When("음악 기록을 생성하면") {
            Then("IllegalArgumentException이 발생한다") {
                shouldThrow<IllegalArgumentException> {
                    musicRecordService.createMusicRecords(calendarDateId, listOf(music1Id, music2Id))
                }
            }
        }

        When("음악 기록을 수정하면") {
            Then("IllegalArgumentException이 발생한다") {
                shouldThrow<IllegalArgumentException> {
                    musicRecordService.updateMusicRecords(calendarDateId, userId, newMusicIds)
                }
            }
        }
    }

    Given("유저가 캘린더 EDIT 권한을 가지지 않은 경우") {
        beforeTest {
            every { calendarDateRepository.findWithOwnerById(calendarDateId) } returns Optional.of(calendarDate)
            every { permissionService.checkPermission(eq(user), eq(user)) } returns CalendarPermission.VIEW
        }

        When("음악 기록을 수정하면") {
            Then("PermissionDeniedException이 발생한다") {
                shouldThrow<PermissionDeniedException> {
                    musicRecordService.updateMusicRecords(calendarDateId, userId, newMusicIds)
                }
            }
        }
    }
})
