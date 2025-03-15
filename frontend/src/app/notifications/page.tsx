"use client";

import { useEffect, useState } from "react";
import axios from "axios";
import Link from "next/link";
import styles from "@/components/style/notification.module.css";
import { format, differenceInDays, isToday, isYesterday } from "date-fns";
import { ko } from "date-fns/locale";

interface NotificationDto {
  id: number;
  userId: number;
  message: string;
  notificationTime: string;
  isRead: boolean;
}

const Notifications = () => {
  const [notifications, setNotifications] = useState<NotificationDto[]>([]);
  const [filterType, setFilterType] = useState("All");
  const unreadCount = notifications.filter(
    (notification) => !notification.isRead
  ).length;

  const fetchNotifications = async () => {
    try {
      const token = localStorage.getItem("accessToken"); // 저장된 JWT 토큰 가져오기
      if (!token) {
        console.error("JWT 토큰이 없습니다!");
        return;
      }

      const response = await axios.get(
        "http://localhost:8080/api/v1/notification-lists",
        {
          headers: {
            Authorization: `Bearer ${token}`, // JWT 토큰을 Authorization 헤더에 추가
          },
        }
      );
      setNotifications(
        Array.isArray(response.data)
          ? response.data
          : response.data.notifications || []
      );
    } catch (error) {
      console.error("Failed to fetch notifications", error);
    }
  };

  useEffect(() => {
    fetchNotifications();
  }, []);

  const markAsRead = async (id: number) => {
    try {
      const token = localStorage.getItem("accessToken"); // 저장된 JWT 토큰 가져오기
      if (!token) {
        console.error("JWT 토큰이 없습니다!");
        return;
      }

      await axios.patch(
        `http://localhost:8080/api/v1/notification-lists/${id}`,
        {},
        {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        }
      );

      setNotifications((prev) =>
        prev.map((notification) =>
          notification.id === id
            ? { ...notification, isRead: true }
            : notification
        )
      );
    } catch (error) {
      console.error("Failed to mark notification as read", error);
    }
  };

  const markAllAsRead = async () => {
    try {
      const token = localStorage.getItem("accessToken"); // JWT 토큰 가져오기
      if (!token) {
        console.error("JWT 토큰이 없습니다!");
        return;
      }

      // 서버에 한 번의 요청으로 모든 알림 읽음 처리
      await axios.patch(
        "http://localhost:8080/api/v1/notification-lists/mark-all-read",
        {},
        {
          headers: {
            Authorization: `Bearer ${token}`, // JWT 토큰 추가
            "Content-Type": "application/json",
          },
        }
      );

      // 상태 업데이트: 모든 읽지 않은 알림을 읽음 처리
      setNotifications((prev) =>
        prev.map((notification) => ({ ...notification, isRead: true }))
      );

      console.log("모든 알림을 읽음 처리했습니다!");
    } catch (error) {
      console.error("Failed to mark all notifications as read", error);
    }
  };

  const filteredNotifications = notifications.filter((notification) => {
    if (filterType === "Unread") return !notification.isRead;
    return true;
  });

  // 날짜 변환 함수
  const formatNotificationTime = (dateString: string) => {
    const date = new Date(dateString);
    const daysDiff = differenceInDays(new Date(), date);

    if (isToday(date)) {
      return `오늘 ${format(date, "HH:mm", { locale: ko })}`;
    }
    if (isYesterday(date)) {
      return `어제 ${format(date, "HH:mm", { locale: ko })}`;
    }
    if (daysDiff < 7) {
      return `${daysDiff}일 전`;
    }
    return format(date, "yyyy년 MM월 dd일");
  };

  return (
    <div className="w-[800px] min-h-[800px] mx-auto p-5 rounded-lg">
      <div
        style={{
          padding: "10px",
          borderRadius: "10px",
          background: "white",
          boxShadow: "0 4px 6px rgba(0, 0, 0, 0.1)", // 부드러운 그림자 효과
          minHeight: "760px",
        }}
      >
        <div className="flex justify-between text-center items-center pb-[10px]">
          <h2 className="text-2xl font-semibold text-gray-800 px-4">
            All Notifications
          </h2>
          <Link href="/setting">
            <div className="flex justify-end">
              <button className={styles.setting}>
                <div className="flex flex-row gap-2">
                  <p style={{ fontWeight: "700", color: "#121212" }}>Setting</p>
                  <i
                    className="fa-solid fa-gear fa-lg"
                    style={{
                      display: "flex",
                      justifyContent: "center",
                      alignItems: "center",
                    }}
                  />
                </div>
              </button>
            </div>
          </Link>
        </div>
        <hr
          style={{
            height: "2px",
            border: "0",
            backgroundColor: "rgb(229,229,229)",
            marginLeft: "-10px",
            marginRight: "-10px",
          }}
        />
        <div className="flex justify-between h-14 text-center items-center">
          <div className="flex gap-2">
            <button
              className={`px-4  rounded-md no-underline hover:underline hover:text-blue-600 ${
                filterType === "All" ? "text-black" : "text-gray-300"
              }`}
              onClick={() => setFilterType("All")}
            >
              All
            </button>
            <button
              className={`px-4 rounded-md no-underline hover:underline hover:text-blue-600 ${
                filterType === "Unread" ? "text-black" : "text-gray-500"
              }`}
              onClick={() => setFilterType("Unread")}
            >
              Unread ({unreadCount})
            </button>
          </div>
          <button
            className="px-2 text-blue-400 rounded-md no-underline hover:underline hover:text-blue-600"
            onClick={markAllAsRead}
          >
            Mark all as read
          </button>
        </div>

        <hr
          style={{
            height: "2px",
            border: "0",
            backgroundColor: "rgb(229,229,229)",
            marginLeft: "-10px",
            marginRight: "-10px",
          }}
        />

        {filteredNotifications.length > 0 ? (
          filteredNotifications.map((notification) => (
            <div
              key={notification.id}
              className={`relative px-6 mx-[-10px] py-6 transition-all duration-200 ease-in-out
        ${
          notification.isRead
            ? "bg-[#F8F7FF] hover:bg-[#BBD0FF]"
            : "bg-[#d7e1f7] hover:bg-[#BBD0FF]"
        }`}
            >
              {/* 읽지 않은 알림에만 빨간 점 표시 */}
              {!notification.isRead && (
                <span className="absolute top-2 right-2 w-2 h-2 bg-red-500 rounded-full"></span>
              )}

              <div className="flex justify-between items-center">
                <div>
                  <h4 className="text-lg font-medium text-gray-800">
                    {notification.message}
                  </h4>
                  <p className="text-sm text-gray-600">
                    {formatNotificationTime(notification.notificationTime)}
                  </p>
                </div>
                {/* 읽지 않은 알림에만 체크박스 표시 */}
                {!notification.isRead && (
                  <input
                    type="checkbox"
                    className="w-5 h-5 border-2 border-gray-400 rounded-md appearance-none cursor-pointer hover:bg-green-500 checked:bg-green-400 checked:border-transparent"
                    onChange={() => markAsRead(notification.id)}
                  />
                )}
              </div>
            </div>
          ))
        ) : (
          <p className="m-4 text-center font-bold text-xl">No Notification!!</p>
        )}
      </div>
    </div>
  );
};

export default Notifications;
