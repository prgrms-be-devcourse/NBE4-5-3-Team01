"use client";

import { useState, useEffect } from "react";
import axios from "axios";
import styles from "@/components/style/notificationSetting.module.css";

interface NotificationDto {
  id: number;
  message: string;
  notificationTime: string;
}

const NotificationEdit = () => {
  // 편집할 알림의 ID나 목록을 가져오는 로직 필요 (여기서는 단순 예시)
  const [editingId, setEditingId] = useState<number | null>(null);
  const [editMessage, setEditMessage] = useState<string>("");
  const [editTime, setEditTime] = useState<string>("");
  const [notifications, setNotifications] = useState<NotificationDto[]>([]);

  useEffect(() => {
    // 예시: 수정 가능한 알림 목록을 불러옴
    const fetchNotifications = async () => {
      try {
        const response = await axios.get(
          "http://localhost:8080/api/v1/notifications"
        );
        // 여기서 필요한 데이터만 필터링하거나 선택할 수 있음
        setNotifications(response.data);
      } catch (error) {
        console.error("Failed to fetch notifications", error);
      }
    };
    fetchNotifications();
  }, []);

  const startEditing = (notification: NotificationDto) => {
    setEditingId(notification.id);
    setEditMessage(notification.message);
    setEditTime(notification.notificationTime);
  };

  const handleUpdate = async () => {
    if (editingId === null) return;
    try {
      const payload = {
        message: editMessage,
        notificationTime: editTime,
      };
      await axios.put(
        `http://localhost:8080/api/v1/notifications/${editingId}/modify`,
        payload
      );
      alert("알림이 수정되었습니다.");
      setEditingId(null);
      setEditMessage("");
      setEditTime("");
      // 수정 후 다시 목록을 불러오거나 상태 업데이트
    } catch (error) {
      console.error("Failed to update notification", error);
      alert("알림 수정에 실패했습니다.");
    }
  };

  return (
    <div>
      <h3>알림 수정</h3>
      {editingId ? (
        <div className={styles.editForm}>
          <div className={styles.inputGroup}>
            <label>메시지</label>
            <input
              className={styles.input}
              type="text"
              value={editMessage}
              onChange={(e) => setEditMessage(e.target.value)}
            />
          </div>
          <div className={styles.inputGroup}>
            <label>알림 시간 (HH:mm)</label>
            <input
              className={styles.input}
              type="time"
              value={editTime}
              onChange={(e) => setEditTime(e.target.value)}
            />
          </div>
          <button className={styles.button} onClick={handleUpdate}>
            저장
          </button>
          <button className={styles.button} onClick={() => setEditingId(null)}>
            취소
          </button>
        </div>
      ) : (
        <div>
          <p>수정할 알림을 선택해주세요.</p>
          {notifications.map((notification) => (
            <div key={notification.id} className={styles.notificationCard}>
              <h4>{notification.message}</h4>
              <p>{notification.notificationTime}</p>
              <button
                className={styles.button}
                onClick={() => startEditing(notification)}
              >
                수정하기
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default NotificationEdit;
