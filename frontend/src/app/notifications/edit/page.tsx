"use client";

import { useState, useEffect } from "react";
import axios from "axios";
import styles from "@/components/style/notificationSettingEdit.module.css";
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

const NotificationEdit = () => {
  const [notifications, setNotifications] = useState<NotificationDto[]>([]);
  const [editingNotificationId, setEditingNotificationId] = useState<
    number | null
  >(null);
  const [newNotificationTime, setNewNotificationTime] = useState<string>("");
  const [isSaving, setIsSaving] = useState(false);

  useEffect(() => {
    const fetchNotifications = async () => {
      try {
        const response = await axios.get(
          "http://localhost:8080/api/v1/notifications/modify",
          {
            withCredentials: true, // 쿠키 포함
          }
        );
        setNotifications(response.data);
      } catch (error) {
        console.error("Failed to fetch notifications", error);
      }
    };
    fetchNotifications();
  }, []);

  const handleEditClick = (id: number, currentTime: string) => {
    setEditingNotificationId(id);
    setNewNotificationTime(currentTime);
  };

  const handleSaveClick = async (notificationId: number) => {
    setIsSaving(true);
    try {
      await axios.put(
        `http://localhost:8080/api/v1/notifications/${notificationId}/modify`,
        {
          notificationTime: newNotificationTime,
        },
        {
          withCredentials: true, // 쿠키 포함
        }
      );
      setIsSaving(false);
      setEditingNotificationId(null); // 수정 후 폼 닫기
      setNewNotificationTime(""); // 시간 초기화
      alert("알림 시간이 수정되었습니다.");

      // 알림 시간 반영 (클라이언트 상태 업데이트)
      setNotifications((prevNotifications) =>
        prevNotifications.map((notification) =>
          notification.id === notificationId
            ? { ...notification, notificationTime: newNotificationTime }
            : notification
        )
      );
    } catch (error) {
      setIsSaving(false);
      console.error("Error saving notification time", error);
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
    <div className="w-[800px] min-h-[800px] mx-auto p-5 rounded-lg">
      <div
        style={{
          padding: "10px",
          borderRadius: "10px",
          background: "#F8F7FF",
          boxShadow: "0 4px 6px rgba(0, 0, 0, 0.1)", // 부드러운 그림자 효과
          minHeight: "760px",
        }}
      >
        <div className={styles.page}>
          <div className={styles.container}>
            <div className={styles.btn1}>
              <Link href="/setting">
                <button className={styles.btn2}>
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
                알림 수정
              </h1>
              <div className={styles.menu}>
                {notifications.map((notification) => (
                  <div
                    className={`${styles.linkb} ${styles.kaxeyf}`}
                    key={notification.id}
                  >
                    <div className={styles.jJychV}>
                      <div className={styles.lbPUPJ}>
                        <div>
                          <div className={styles.d5}>
                            <div className={styles.d51}>
                              <span
                                style={{ fontWeight: "600", fontSize: "20px" }}
                              >
                                {titleMap[notification.title] || "알림"}{" "}
                              </span>
                            </div>
                            <div className={styles.d52}>
                              <span
                                style={{ fontWeight: "700", color: "#60676a" }}
                              >
                                {messageMap[notification.title] ||
                                  notification.message}{" "}
                              </span>
                            </div>
                          </div>
                        </div>
                      </div>
                      <div className={styles.d53}>
                        {editingNotificationId === notification.id ? (
                          <div
                            className={styles.d61}
                            style={{ fontWeight: "600", fontSize: "20px" }}
                          >
                            <input
                              type="time"
                              value={newNotificationTime}
                              onChange={(e) =>
                                setNewNotificationTime(e.target.value)
                              }
                              className={styles.timeInput}
                              step={600} // 5분 단위로 설정
                            />
                            <button
                              onClick={() => handleSaveClick(notification.id)}
                              disabled={isSaving}
                              className={styles.saveButton}
                            >
                              {isSaving ? "저장 중..." : "저장"}
                            </button>
                          </div>
                        ) : (
                          <div
                            className={styles.d62}
                            style={{ fontWeight: "600", fontSize: "20px" }}
                          >
                            <button
                              onClick={() =>
                                handleEditClick(
                                  notification.id,
                                  notification.notificationTime
                                )
                              }
                            >
                              수정
                            </button>
                          </div>
                        )}
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default NotificationEdit;
