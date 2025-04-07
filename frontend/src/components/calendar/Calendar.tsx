"use client";

import FullCalendar from "@fullcalendar/react";
import dayGridPlugin from "@fullcalendar/daygrid";
import interactionPlugin from "@fullcalendar/interaction";
import { useEffect, useMemo, useState } from "react";
import { DatesSetArg } from "@fullcalendar/core";
import { useRouter, useSearchParams } from "next/navigation";
import { CalendarDate, Monthly } from "@/types/calendar";
import { User } from "@/types/user";
import { FollowCount } from "@/types/follow";
import { handleDayCellDidMount, handleEventDidMount } from "@/components/calendar/eventHandlers";
import { AxiosError } from "axios";
import { useGlobalAlert } from "../GlobalAlert";
import { fetchUser } from "@/lib/api/user";
import { fetchFollowCount } from "@/lib/api/follow";
import { fetchMonthlyData } from "@/lib/api/calendar";
import "@/components/style/calendar.css";
import LoadingSpinner from '@/components/LoadingSpinner';

const Calendar: React.FC = () => {
  const [monthly, setMonthly] = useState<CalendarDate[]>([]);
  const [selectedYear, setSelectedYear] = useState<number>(new Date().getFullYear());
  const [selectedMonth, setSelectedMonth] = useState<number>(new Date().getMonth() + 1);
  const [followingCount, setFollowingCount] = useState(0);
  const [followerCount, setFollowerCount] = useState(0);
  const [isCalendarOwner, setIsCalendarOwner] = useState<boolean | null>(null);
  const [calendarOwner, setCalendarOwner] = useState<User | null>(null);
  const [today, setToday] = useState(new Date());
  const { setAlert } = useGlobalAlert();

  const router = useRouter();
  const params = useSearchParams();
  const queryString = params.toString();

  const events = useMemo(
    () =>
      monthly.map((arg) => ({
        start: arg.date,
        display: "background",
        extendedProps: {
          albumImage: arg.albumImage,
        },
      })),
    [monthly]
  );

  useEffect(() => {
    const interval = setInterval(() => {
      setToday(new Date());
    }, 60 * 1000 * 10); // 10분마다 갱신

    return () => clearInterval(interval);
  }, []);

  // 캘린더 소유자 데이터 조회
  useEffect(() => {
    const calendarOwnerId = params.get("userId");

    async function initCalendarOwner() {
      if (calendarOwnerId === null) {
        const response = await fetchUser(`/user/byCookie`);

        const calendarOwner: User = response.data.data;

        setCalendarOwner(calendarOwner);
        setIsCalendarOwner(true);
      } else {
        const responseByCookie = await fetchUser(`/user/byCookie`);
        const responseById = await fetchUser(`/user/${calendarOwnerId}`)

        const currentUser: User = responseByCookie.data.data;
        const calendarOwner: User = responseById.data.data;

        setCalendarOwner(calendarOwner);
        setIsCalendarOwner(currentUser.id === calendarOwner.id);
      }
    }

    initCalendarOwner();
  }, [queryString])

  // 캘린더 소유자의 팔로잉, 팔로워 수 조회
  useEffect(() => {
    async function initFollowCount() {
      if (calendarOwner) {
        const response = await fetchFollowCount(calendarOwner.id);

        const followCount: FollowCount = response.data.data;

        setFollowingCount(followCount.followingCount);
        setFollowerCount(followCount.followerCount);
      }
    }

    initFollowCount();
  }, [calendarOwner])

  // 캘린더 소유자의 먼슬리 캘린더 데이터 조회
  useEffect(() => {
    async function initMonthly(year: number, month: number) {
      if (isCalendarOwner === null || !calendarOwner) return;

      try {
        const response = await fetchMonthlyData(
            year,
            month,
            isCalendarOwner ? undefined : calendarOwner.id
        );

        const monthly: Monthly = response.data.data;

        setMonthly(monthly.monthly);
      } catch (error) { // 예외 처리
        if (error instanceof AxiosError)
          setAlert({
            code: error.response!.status.toString(),
            message:  error.response!.data.msg,
          });

        setTimeout(() => {
          router.push("/calendar");
        }, 2000); // 2초 대기 후 이동

        return;
      }
    }

    initMonthly(selectedYear, selectedMonth);
  }, [isCalendarOwner, selectedYear, selectedMonth])

  // 페이지를 떠날 때 스타일 속성 삭제
  useEffect(() => {
    return () => {

      // 기록이 있는 날짜 셀 선택
      const cells = document.querySelectorAll(".fc-daygrid-day.has-record");

      cells.forEach((cell) => {
        if (cell instanceof HTMLElement) {
          const dateNumber = cell.querySelector(".fc-daygrid-day-number") as HTMLElement;

          if (dateNumber) { // 속성 삭제
            dateNumber.style.removeProperty("color");
            dateNumber.style.removeProperty("font-weight");
            dateNumber.style.removeProperty("text-shadow");
          }

          // 클래스 삭제
          cell.classList.remove("has-record");
        }
      });
    };
  }, [queryString]);

  // 먼슬리 캘린더에서 연도와 월이 변경된 경우 상태 저장
  const handleDateChange = (arg: DatesSetArg) => {
    setSelectedYear(arg.view.currentStart.getFullYear());
    setSelectedMonth(arg.view.currentStart.getMonth() + 1);
  };

  // 날짜 셀의 내용에서 숫자 뒤에 오는 "일" 삭제
  const handleDayCellContent = (arg: { dayNumberText: string }) => {
    return (<span className="ml-auto">{arg.dayNumberText.replace("일", "")}</span>
    );
  };

  // 팔로워 또는 팔로잉을 클릭할 경우 목록 페이지로 라우팅
  const handleFollowCountClick = () => {
    if (calendarOwner) {
      router.push(`/follow?userId=${calendarOwner.id}`);
    }
  };

  // 날짜 셀을 클릭한 경우 분기 처리
  const handleDateClick = (arg: { dateStr: string }) => {
    if (monthly) {
      const calendarDate: CalendarDate | undefined = monthly.find(
          (calendarDate) => calendarDate.date === arg.dateStr
      );

      if (!calendarDate && isCalendarOwner) { // 해당 날짜에 기록이 없는 경우 기록 페이지로 이동
        const [year, month, day] = arg.dateStr.split("-");
        router.push(`/calendar/record?year=${year}&month=${month}&day=${day}`);
      } else if (calendarDate) { // 해당 날짜에 기록이 있는 경우 상세 페이지로 이동
        router.push(`/calendar/${calendarDate.id}`);
      } else {
        setAlert({
          code: "404",
          message: "해당 날짜에 기록된 음악이 없습니다.",
        });
      }
    }
  };

  return (
    <>
      {isCalendarOwner === null ? (
        <LoadingSpinner message={"캘린더를 불러오는 중..."}/>
        ) : (
        <div className="flex flex-col w-full px-10 justify-center items-center">
          <div
            className="flex justify-end mt-4 mb-4"
            style={{ width: "min(90vh, calc(100vw - 18rem))" }}
          >
            <h2
              className={`text-xl text-[#393D3F]`}
              onClick={
                isCalendarOwner
                  ? () => router.push("/user/calendar-visibility")
                  : undefined
              }
            >
              {calendarOwner?.name ?? "나"}의 캘린더📆
            </h2>
            <div className="flex space-x-4 ml-4">
              <button
                className="text-lg text-[#393D3F] bg-[#E8E0FF] rounded-lg px-2"
                onClick={handleFollowCountClick}
              >
                {followerCount} 팔로워
              </button>
              <button
                className="text-lg text-[#393D3F] bg-[#E8E0FF] rounded-lg px-2"
                onClick={handleFollowCountClick}
              >
                {followingCount} 팔로잉
              </button>
            </div>
          </div>

          <div
            style={{
              width: "min(90vh, calc(100vw - 18rem))",
              height: "min(90vh, calc(100vw - 18rem))",
            }}
          >
            <FullCalendar
              locale="ko"
              height="100%"
              contentHeight="100%"
              plugins={[dayGridPlugin, interactionPlugin]}
              headerToolbar={{
                left: "prevYear,prev",
                center: "title",
                right: "next,nextYear",
              }}
              initialView="dayGridMonth"
              editable={false}
              selectable={false}
              selectMirror={true}
              dayCellContent={handleDayCellContent}
              datesSet={handleDateChange}
              dateClick={handleDateClick}
              dayMaxEvents={true}
              events={events}
              eventDidMount={handleEventDidMount}
              dayCellDidMount={(arg) =>
                  handleDayCellDidMount(arg, isCalendarOwner)
              }
              stickyHeaderDates={true}
              validRange={{
                end: today,
              }}
              showNonCurrentDates={false}
            />
          </div>
        </div>
      )}
    </>
  );
};

export default Calendar;