"use client";

import "@/components/style/global.css";
import { useEffect, useState } from "react";
import axios from "axios";
import styles from "@/components/style/notificationSettingConfigure.module.css";
import Checkbox from "./checkbox";
import Link from "next/link";

interface NotificationDto {
  id: number;
  userId: string;
  title: string;
  message: string;
  notificationTime: string;
  isEmailEnabled: boolean;
  isPushEnabled: boolean;
}

const NotificationCreate = () => {
  const [notifications, setNotifications] = useState<NotificationDto[]>([]);

  useEffect(() => {
    const fetchNotifications = async () => {
      try {
        const token = localStorage.getItem("accessToken");
        if (!token) {
          alert("로그인이 필요합니다.");
          window.location.href = "/login";
          return;
        }

        const response = await axios.get(
          "http://localhost:8080/api/v1/notifications/lists",
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );

        setNotifications(response.data);
      } catch (error) {
        console.error("Failed to fetch notifications", error);
      }
    };

    fetchNotifications();
  }, []);

  const handleCheckboxChange = (id: number, type: "email" | "push") => {
    setNotifications((prev) =>
      prev.map((n) =>
        n.id === id
          ? {
              ...n,
              isEmailEnabled:
                type === "email" ? !n.isEmailEnabled : n.isEmailEnabled,
              isPushEnabled:
                type === "push" ? !n.isPushEnabled : n.isPushEnabled,
            }
          : n
      )
    );
  };

  const handleSave = async () => {
    const token = localStorage.getItem("accessToken");
    if (!token) {
      alert("로그인이 필요합니다.");
      window.location.href = "/login";
      return;
    }

    // 각 알림의 이메일/푸시 알림 활성화 상태
    const updatedNotifications = notifications.map((notification) => ({
      notificationId: notification.id,
      isEmailNotificationEnabled: notification.isEmailEnabled, // 이메일 활성화 여부
      isPushNotificationEnabled: notification.isPushEnabled, // 푸시 알림 활성화 여부
    }));

    try {
      await axios.patch(
        "http://localhost:8080/api/v1/notifications/update",
        updatedNotifications, // 서버에 보낼 데이터
        {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        }
      );

      alert("알림 설정이 저장되었습니다!");
    } catch (error) {
      console.error("알림 설정 저장 실패", error);
    }
  };

  const titleMap: Record<string, string> = {
    "DAILY CHALLENGE": "음악 기록",
    "SHARE MUSIC": "음악 공유",
    "BUILD PLAYLIST": "나만의 플레이리스트",
    "YEAR HISTORY": "1년 전 음악",
    FOLLOWING: "팔로워",
  };

  const messageMap: Record<string, string> = {
    "DAILY CHALLENGE": "매일 정해진 시간에 음악을 기록하라고 알려주는 알림",
    "SHARE MUSIC": "음악을 캘린더에 기록했을 때 알림",
    "BUILD PLAYLIST": "플레이리스트를 만들어보길 추천하는 알림",
    "YEAR HISTORY": "작년 오늘 기록한 음악 알림",
    FOLLOWING: "다른 사용자가 나를 팔로우하기 시작할 때 알림",
  };

  return (
    <div className="encore-dark-theme">
      <div className={styles.page}>
        <div className={styles.container}>
          <div className={styles.btn}>
            <Link href="/notifications/settings">
              <button className={styles.btn1}>
                <span className={styles.baseline}>
                  <span aria-hidden="true" className={styles.btn_icon}>
                    <svg
                      data-encore-id="icon"
                      role="img"
                      aria-label="Back"
                      aria-hidden="false"
                      className={styles.icon}
                      viewBox="0 0 24 24"
                    >
                      <path d="M15.957 2.793a1 1 0 0 1 0 1.414L8.164 12l7.793 7.793a1 1 0 1 1-1.414 1.414L5.336 12l9.207-9.207a1 1 0 0 1 1.414 0z"></path>
                    </svg>
                  </span>
                </span>
              </button>
            </Link>
          </div>
          <div className={styles.wrapper}>
            <h1
              className={styles.header}
              style={{
                fontSize: "3rem",
                fontWeight: "700",
              }}
            >
              알림 설정
            </h1>
            <span className={styles.s1}>
              푸시 또는 이메일로 받고 싶은 알림을 선택하세요. 이러한 기본 설정은
              푸시 및 이메일에만 적용됩니다.
            </span>
            <div className={styles.menu}>
              <div className={styles.d3}>
                <div className={styles.d31}>
                  <svg
                    data-encore-id="icon"
                    role="img"
                    aria-hidden="true"
                    className={styles.icon}
                    viewBox="0 0 24 24"
                  >
                    <path d="M1 3h22v18H1V3zm2 2v1.711l9 5.197 9-5.197V5H3zm18 4.02-9 5.197L3 9.02V19h18V9.02z"></path>
                  </svg>
                  <p>이메일</p>
                </div>
                <div className={styles.d32}>
                  <svg
                    data-encore-id="icon"
                    role="img"
                    aria-hidden="true"
                    className={styles.icon}
                    viewBox="0 0 24 24"
                  >
                    <path d="M5 5a3 3 0 0 1 3-3h8a3 3 0 0 1 3 3v14a3 3 0 0 1-3 3H8a3 3 0 0 1-3-3V5zm3-1a1 1 0 0 0-1 1v14a1 1 0 0 0 1 1h8a1 1 0 0 0 1-1V5a1 1 0 0 0-1-1H8z"></path>
                    <path d="M13.25 16.75a1.25 1.25 0 1 1-2.5 0 1.25 1.25 0 0 1 2.5 0z"></path>
                  </svg>
                  <p>푸시</p>
                </div>
              </div>
              {notifications.map((notification) => (
                <div className={styles.d4} key={notification.id}>
                  <div className={styles.d5}>
                    <div className={styles.d51}>
                      <span style={{ fontWeight: "700", fontSize: "20px" }}>
                        {titleMap[notification.title] || "알림"}{" "}
                      </span>
                    </div>
                    <div className={styles.d52}>
                      <span style={{ fontWeight: "700", color: "#b3b3b3" }}>
                        {messageMap[notification.title] || notification.message}{" "}
                      </span>
                    </div>
                    <div className={styles.d53}>
                      <div className={styles.d6}>
                        <Checkbox
                          checked={notification.isEmailEnabled}
                          onChange={() =>
                            handleCheckboxChange(notification.id, "email")
                          }
                        />
                      </div>
                    </div>
                    <div className={styles.d54}>
                      <div className={styles.d6}>
                        <Checkbox
                          checked={notification.isPushEnabled}
                          onChange={() =>
                            handleCheckboxChange(notification.id, "push")
                          }
                        />
                      </div>
                    </div>
                  </div>
                </div>
              ))}

              <div className={styles.d7}>
                <Link href="/notifications/settings" className={styles.cancel}>
                  취소하기
                </Link>
                <button className={styles.saved} onClick={handleSave}>
                  <span className={styles.s2}>저장하기</span>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default NotificationCreate;
