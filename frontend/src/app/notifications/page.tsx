"use client";

import { useEffect, useState } from "react";
import axios from "axios";
import Link from "next/link";
import Image from "next/image";
import styles from "@/components/style/notification.module.css";

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
      const response = await axios.get(
        "http://localhost:8080/api/v1/notifications"
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
      await axios.put(`http://localhost:8080/api/v1/notifications/${id}/read`);
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
      await axios.put(
        "http://localhost:8080/api/v1/notifications/mark-all-read"
      );
      setNotifications((prev) =>
        prev.map((notification) => ({ ...notification, isRead: true }))
      );
    } catch (error) {
      console.error("Failed to mark all notifications as read", error);
    }
  };

  const filteredNotifications = notifications.filter((notification) => {
    if (filterType === "Unread") return !notification.isRead;
    return true;
  });

  return (
    <div className="w-[800px] mx-auto p-5 bg-gray-100 rounded-lg">
      <div
        style={{
          padding: "10px",
          borderRadius: "10px",
          background: "white",
          boxShadow: "0 4px 6px rgba(0, 0, 0, 0.1)", // 부드러운 그림자 효과
        }}
      >
        <div className="flex justify-between text-center items-center pb-[10px]">
          <h2 className="text-2xl font-semibold text-gray-800 px-4">
            All Notifications
          </h2>
          <div className="flex justify-end">
            <button className={styles.setting}>
              <Link href="/notifications/settings">
                <div className="flex flex-row gap-2">
                  <p style={{ fontWeight: "700", color: "#121212" }}>Setting</p>
                  <Image
                    src="/setting.svg"
                    alt="설정 아이콘"
                    width={24}
                    height={24}
                  />
                </div>
              </Link>
            </button>
          </div>
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
            ? "bg-white hover:bg-gray-200"
            : "bg-gray-100 hover:bg-gray-200"
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
                    {notification.notificationTime}
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
