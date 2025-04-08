package com.team01.project.domain.musicrecord.service

import com.team01.project.domain.calendardate.repository.CalendarDateRepository
import com.team01.project.domain.calendardate.repository.findByIdOrThrow
import com.team01.project.domain.calendardate.repository.findWithOwnerByIdOrThrow
import com.team01.project.domain.music.entity.Music
import com.team01.project.domain.music.repository.MusicRepository
import com.team01.project.domain.musicrecord.entity.MusicRecord
import com.team01.project.domain.musicrecord.entity.MusicRecordId
import com.team01.project.domain.musicrecord.repository.MusicRecordRepository
import com.team01.project.domain.user.repository.UserRepository
import com.team01.project.global.exception.PermissionDeniedException
import com.team01.project.global.permission.CalendarPermission
import com.team01.project.global.permission.PermissionService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.Optional

@Service
@Transactional
class MusicRecordService(
    private val musicRecordRepository: MusicRecordRepository,
    private val calendarDateRepository: CalendarDateRepository,
    private val musicRepository: MusicRepository,
    private val userRepository: UserRepository,
    private val permissionService: PermissionService
) {
    /**
     * 캘린더에 기록된 음악 리스트 조회
     *
     * @param calendarDateId 캘린더 아이디
     * @return 음악 리스트
     */
    fun findMusicsByCalendarDateId(calendarDateId: Long): List<Music> {
        val calendarDate = calendarDateRepository.findByIdOrThrow(calendarDateId)

        return musicRecordRepository.findByCalendarDate(calendarDate)
            .map { it.music }
    }

    /**
     * 캘린더에 기록된 음악 기록 하나 조회
     *
     * @param calendarDateId 캘린더 아이디
     * @return 음악 기록
     */
    fun findOneByCalendarDateId(calendarDateId: Long): Optional<MusicRecord> {
        val calendarDate = calendarDateRepository.findByIdOrThrow(calendarDateId)
        return musicRecordRepository.findTopByCalendarDate(calendarDate)
    }

    /**
     * 캘린더에 음악 기록 저장
     *
     * @param calendarDateId 캘린더 아이디
     * @param newMusicIds    기록할 전체 음악 아이디 리스트
     */
    fun createMusicRecords(calendarDateId: Long, newMusicIds: List<String>) {
        val calendarDate = calendarDateRepository.findByIdOrThrow(calendarDateId)

        // 기록할 MusicId 목록
        val newMusicIdSet = newMusicIds.toSet()

        val musicRecordsToAdd = newMusicIdSet.map { musicId ->
            MusicRecord(
                id = MusicRecordId(calendarDateId, musicId),
                calendarDate = calendarDate,
                music = musicRepository.findByIdOrThrow(musicId)
            )
        }

        musicRecordRepository.saveAll(musicRecordsToAdd)
    }

    /**
     * 캘린더 음악 기록 수정
     *
     * @param calendarDateId 캘린더 아이디
     * @param loggedInUserId 현재 인증된 유저
     * @param newMusicIds    기록할 전체 음악 아이디 리스트
     */
    fun updateMusicRecords(calendarDateId: Long, loggedInUserId: String, newMusicIds: List<String>) {
        val calendarDate = calendarDateRepository.findWithOwnerByIdOrThrow(calendarDateId)
        val calendarOwner = calendarDate.user
        val loggedInUser = userRepository.getById(loggedInUserId)

        val calendarPermission = permissionService.checkPermission(calendarOwner, loggedInUser)

        if (CalendarPermission.EDIT != calendarPermission) {
            throw PermissionDeniedException("403-12", "캘린더를 수정할 권한이 없습니다.")
        }

        // 1. 기존 MusicRecord 조회
        val oldMusicRecords = musicRecordRepository.findByCalendarDate(calendarDate)

        // 2. 기존 MusicId 목록 조회
        val oldMusicIdSet = oldMusicRecords.map { it.music.id }
            .toSet()

        // 3. 새로 추가될 MusicId 목록
        val newMusicIdSet = newMusicIds.toSet()

        // 4. 삭제할 MusicRecord 목록
        val musicRecordsToDelete = oldMusicRecords
            .filterNot { it.music.id in newMusicIdSet }

        // 5. 추가할 MusicRecord 목록
        val musicRecordsToAdd = newMusicIdSet
            .filterNot { it in oldMusicIdSet }
            .map { musicId ->
                MusicRecord(
                    id = MusicRecordId(calendarDateId, musicId),
                    calendarDate = calendarDate,
                    music = musicRepository.findByIdOrThrow(musicId)
                )
            }

        // 6. MusicRecord 업데이트
        musicRecordRepository.deleteAll(musicRecordsToDelete)
        musicRecordRepository.saveAll(musicRecordsToAdd)
    }

    /**
     * 특정 연도와 월에 기록한 MusicRecord 리스트 조회
     */
    fun getMusicRecordsByUserAndDateRange(
        userId: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<MusicRecord> {
        return musicRecordRepository.findMusicRecordsByUserAndDateRange(userId, startDate, endDate)
    }
}
