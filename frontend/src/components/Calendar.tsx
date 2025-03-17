"use client";

import FullCalendar from "@fullcalendar/react";
import dayGridPlugin from "@fullcalendar/daygrid";
import interactionPlugin from "@fullcalendar/interaction";
import { useEffect, useState } from "react";
import {
  DatesSetArg,
} from "@fullcalendar/core";
import { useRouter, useSearchParams } from "next/navigation";

interface CalendarDate {
  id: number; // 캘린더 아이디
  date: string; // 'yyyy-MM-dd' 형식
  hasMemo: boolean; // 메모 작성 여부
  albumImage: string; // 앨범 이미지 링크
}

interface Monthly {
  monthly: CalendarDate[];
}

interface User {
  id: string;
  name: string;
  nickName: string;
}

interface FollowCount {
  followingCount: number;
  followerCount: number;
}

const BASE_URL = "http://localhost:8080/api/v1";

const Calendar: React.FC = () => {
  const [monthly, setMonthly] = useState<CalendarDate[]>([]);
  const [currentYear, setCurrentYear] = useState<number>(
    new Date().getFullYear()
  );
  const [currentMonth, setCurrentMonth] = useState<number>(
    new Date().getMonth() + 1
  );
  const [user, setUser] = useState<User | null>(null);
  const [followingCount, setFollowingCount] = useState(0);
  const [followerCount, setFollowerCount] = useState(0);
  const [ownerId, setOwnerId] = useState<string | null>(null);
  const [isCalendarOwner, setIsCalendarOwner] = useState<boolean>(false);
  const router = useRouter();
  const params = useSearchParams();

  useEffect(() => {
    const fetchOwnerId = async () => {
      const userId = params.get("userId");
      let currentOwnerId: string | null = null;

      // 현재 로그인한 사용자의 정보 조회
      const response = await fetch(BASE_URL + "/user/byCookie", {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include",
      });

      if (!response.ok) {
        throw new Error("사용자 정보를 불러오는 데 실패했습니다.");
      }

      const currentUser: User = await response.json();

      // 파라미터가 없거나 현재 로그인한 유저와 동일하다면 현재 로그인한 유저가 캘린더 오너
      if (!userId || userId === currentUser.id) {
        setUser(currentUser);
        currentOwnerId = currentUser.id;
        setIsCalendarOwner(true);
      } else { // 아니라면 userId를 가진 유저와 맞팔인지 확인
        const response = await fetch(BASE_URL + `/follows/check/${userId}`, {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
          },
          credentials: "include",
        });

        if (!response.ok) {
          throw new Error("맞팔로우 여부 정보를 불러오는 데 실패했습니다.");
        }

        const isMutualFollowing: boolean = await response.json();

        if (isMutualFollowing) { // 맞팔이라면 userId를 가진 유저가 캘린더 오너
          const response = await fetch(BASE_URL + `/user/${userId}`, {
            method: "GET",
            headers: {
              "Content-Type": "application/json",
            },
            credentials: "include",
          });

          if (!response.ok) {
            throw new Error("사용자 정보를 불러오는 데 실패했습니다.");
          }

          const fetchedUser: User = await response.json();

          setUser(fetchedUser);
          currentOwnerId = fetchedUser.id;
          setIsCalendarOwner(false);
        } else { // 아니라면 캘린더 조회 권한 없음
          alert("캘린더를 조회할 권한이 없습니다.");
          router.push("/calendar");
          return;
        }
      }

      if (currentOwnerId) {
        setOwnerId(currentOwnerId);
        fetchFollowCount(currentOwnerId);
      }
    };

    fetchOwnerId();
  }, [params]);

  const fetchFollowCount = async (userId: string | undefined) => {
    const response = await fetch(BASE_URL + `/follows/count/${userId}`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
      },
      credentials: "include",
    });

    if (!response.ok) {
      throw new Error("사용자 팔로우 정보를 불러오는 데 실패했습니다.");
    }

    const data: FollowCount = await response.json();
    setFollowerCount(data.followerCount);
    setFollowingCount(data.followingCount);
  };

  const fetchCalendarData = async (year: number, month: number) => {
    const headers: Record<string, string> = {
      "Content-Type": "application/json",
      ...(isCalendarOwner ? {} : { "Calendar-Owner-Id": ownerId! }),
    };

    const res = await fetch(
      BASE_URL + `/calendar?year=${year}&month=${month}`,
      {
        method: "GET",
        headers: headers,
        credentials: "include",
      }
    );

    const data: Monthly = await res.json();
    setMonthly(data.monthly);
  };

  useEffect(() => {
    if (ownerId) {
      fetchCalendarData(currentYear, currentMonth);
    }
  }, [ownerId, currentYear, currentMonth]);

  const handleDateChange = (arg: DatesSetArg) => {
    setCurrentYear(arg.view.currentStart.getFullYear());
    setCurrentMonth(arg.view.currentStart.getMonth() + 1);
  };

  useEffect(() => {
    const style = document.createElement("style");
    style.innerHTML = `
        .fc { background-color: #F8F7FF; color: #393D3F; } 
        .fc-daygrid-day { background-color: white; } 
        .fc-toolbar { background-color: #F8F7FF; color: #393D3F; } 
        .fc-daygrid-day-number { color: #393D3F !important; } 
        `;
    document.head.appendChild(style);
  }, []);

  const handleDayCellContent = (arg: { dayNumberText: string }) => {
    return (<span className="ml-auto">{arg.dayNumberText.replace("일", "")}</span>
    );
  };

  const handleFollowButtonClick = (ownerId: string) => {
    router.push(`/follow?userId=${ownerId}`);
  };

  const handleDateClick = (arg: { dateStr: string }) => {
    const clickedDate: CalendarDate | undefined = monthly?.find(
      (calendarDate) => calendarDate.date === arg.dateStr
    );

    if (!clickedDate && isCalendarOwner) {
      const [yearStr, monthStr, dayStr] = arg.dateStr.split("-");

      const year = parseInt(yearStr, 10);
      const month = parseInt(monthStr, 10);
      const day = parseInt(dayStr, 10);

      router.push(`/calendar/record?year=${year}&month=${month}&day=${day}`);
    } else if (clickedDate && isCalendarOwner) {
      router.push(`/calendar/${clickedDate.id}`);
    } else if (clickedDate && !isCalendarOwner) {
      router.push(`/calendar/${clickedDate.id}?readOnly=true`);
    }
  };

  const createButton = () => {
    const button = document.createElement("button");
    button.textContent = "+";

    // 버튼 스타일링
    button.style.position = "absolute"; // 위치를 절대값으로 설정
    button.style.top = "50%"; // 세로 중앙 정렬
    button.style.left = "50%"; // 가로 중앙 정렬
    button.style.transform = "translate(-50%, -50%)"; // 중앙 정렬을 위한 이동
    button.style.border = "none"; // 버튼 테두리 없애기
    button.style.padding = "5px 10px"; // 버튼 안쪽 여백
    button.style.cursor = "pointer"; // 커서 포인터로 변경
    button.style.fontSize = "1.5em"; // 텍스트 크기
    button.style.color = "text-[#393D3F]"; // 텍스트 색상

    return button;
  };

  return (
      <div className="flex flex-col w-full px-10 justify-center items-center">
        <div className="flex justify-end mt-4 mb-4" style={{width: "min(90vh, calc(100vw - 18rem))"}}>
          <h2 className="text-xl text-[#393D3F]">
            {user?.name ?? "나"}의 캘린더📆
          </h2>
          <div className="flex space-x-4 ml-4">
            <button
                className="text-lg text-[#393D3F] bg-[#C8B6FF] rounded-lg px-2"
                onClick={() => handleFollowButtonClick(ownerId!)}
            >
              {followerCount} 팔로워
            </button>
            <button
                className="text-lg text-[#393D3F] bg-[#C8B6FF] rounded-lg px-2"
                onClick={() => handleFollowButtonClick(ownerId!)}
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
              events={monthly?.map((arg) => ({
                start: arg.date,
                display: "background", // 배경 이벤트로 설정
                extendedProps: {
                  albumImage: arg.albumImage,
                },
              }))}
              eventDidMount={(info) => {
                const albumImage = info.event.extendedProps.albumImage;
                if (albumImage) {
                  info.el.style.backgroundImage = `url(${albumImage})`;
                  info.el.style.backgroundSize = "cover";
                  info.el.style.backgroundPosition = "center";
                  info.el.style.opacity = "1";
                  info.el.style.pointerEvents = "none";
                }
                const cell = info.el.closest(".fc-daygrid-day");
                const dateNumber = cell?.querySelector(".fc-daygrid-day-number") as HTMLElement;

                if (dateNumber) {
                  dateNumber.style.setProperty("color", "#C8B6FF", "important");
                  dateNumber.style.setProperty("font-weight", "700", "important");
                  dateNumber.style.setProperty("text-shadow", "0 0 3px rgba(0,0,0,0.5)", "important");
                }
              }}
              dayCellDidMount={(info) => {
                const cellDate = info.date.toLocaleDateString("en-CA"); // YYYY-MM-DD 형식으로 변환
                const hasEvent = monthly.some((event) => event.date === cellDate);
                const currentDate = new Date().toLocaleDateString("en-CA");
                const isAfterToday = cellDate > currentDate;

                if (isAfterToday) {
                  return;
                }
                info.el.style.position = "relative"; // 날짜 셀에 상대적인 위치 부여

                const button = createButton(); // [+] 버튼 생성
                const cell = info.el as HTMLElement;

                cell.addEventListener("mouseenter", () => {
                  cell.style.backgroundColor = "#D9CFFF"; // 배경색 변경
                  if (!hasEvent) {
                    cell.appendChild(button); // 이벤트가 없으면 [+] 버튼 추가
                  }
                });

                // 마우스가 셀에서 벗어날 때
                cell.addEventListener("mouseleave", () => {
                  cell.style.backgroundColor = ""; // 배경색 원래대로 복구
                  if (!hasEvent && button.parentNode) {
                    button.parentNode.removeChild(button); // [+] 버튼 제거
                  }
                });
              }}
              stickyHeaderDates={true}
              validRange={{
                end: new Date(),
              }}
          />
        </div>
      </div>
  );
};

export default Calendar;
