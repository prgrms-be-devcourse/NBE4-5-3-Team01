"use client";

import "@/components/style/global.css";
import { useState } from "react";
import axios from "axios";
import styles from "@/components/style/notificationSetting.module.css";

const NotificationCreate = () => {
  const [newUserId, setNewUserId] = useState<number>(0);
  const [newMessage, setNewMessage] = useState<string>("");
  const [newTime, setNewTime] = useState<string>("");

  const handleCreate = async () => {
    try {
      const payload = {
        userId: newUserId,
        message: newMessage,
        notificationTime: newTime,
      };
      await axios.post(
        "http://localhost:8080/api/v1/notifications/create",
        payload
      );
      alert("알림이 생성되었습니다.");
      // 필요에 따라 폼 초기화 등 추가 처리
      setNewUserId(0);
      setNewMessage("");
      setNewTime("");
    } catch (error) {
      console.error("Failed to create notification", error);
      alert("알림 생성에 실패했습니다.");
    }
  };

  return (
    <div className="encore-dark-theme">
      <div className={styles.page}>
        <div className={styles.container}>
          <div className={styles.content}>
            <div className={styles.wrapper}>
              <h2
                className={styles.header}
                style={{
                  fontSize: "2rem",
                  fontWeight: "700",
                  color: "rgb(255, 210, 215)",
                }}
              >
                알림 등록
              </h2>
              <div className={styles.menu}>
                <label>사용자 ID</label>
                <input
                  className={styles.input}
                  type="number"
                  value={newUserId || ""}
                  onChange={(e) => setNewUserId(Number(e.target.value))}
                />
              </div>
            </div>
            <div className={styles.inputGroup}>
              <label>메시지</label>
              <input
                className={styles.input}
                type="text"
                value={newMessage}
                onChange={(e) => setNewMessage(e.target.value)}
              />
            </div>
            <div className={styles.inputGroup}>
              <label>알림 시간 (HH:mm)</label>
              <input
                className={styles.input}
                type="time"
                value={newTime}
                onChange={(e) => setNewTime(e.target.value)}
              />
            </div>
            <button className={styles.button} onClick={handleCreate}>
              알림 등록
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default NotificationCreate;
