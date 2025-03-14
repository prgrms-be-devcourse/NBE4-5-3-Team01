"use client";

import { useEffect } from "react";

export default function Home() {
  useEffect(() => {
    // 브라우저 환경인지 확인
    if ("serviceWorker" in navigator && "PushManager" in window) {
      // 서비스 워커 등록
      navigator.serviceWorker
        .register("/sw.js")
        .then((registration) => {
          console.log("Service Worker 등록 성공:", registration);

          // 알림 권한 요청
          Notification.requestPermission().then((permission) => {
            console.log("알림 권한 상태:", permission);
            if (permission === "granted") {
              // 푸시 구독 등록
              registration.pushManager
                .subscribe({
                  userVisibleOnly: true, // 항상 사용자에게 표시되어야 함
                  applicationServerKey: urlBase64ToUint8Array(
                    "BJSowR6aDh89tu-QupRzNmwjVrr63q2Wbstv536bFya4emx8YigwBkd4Ogbusg6X6uzeNzOkY678PlY9U66xSIU"
                  ),
                })
                .then((subscription) => {
                  console.log("푸시 구독 성공:", subscription);
                  const token = localStorage.getItem("accessToken");
                  if (!token) {
                    alert("로그인이 필요합니다.");
                    window.location.href = "/login";
                    return;
                  }
                  fetch("http://localhost:8080/api/v1/push/subscribe", {
                    method: "POST",
                    headers: {
                      Authorization: `Bearer ${token}`,
                      "Content-Type": "application/json",
                    },
                    body: JSON.stringify(subscription),
                  })
                    .then((response) => response.text())
                    .then((data) =>
                      console.log("백엔드에 구독 정보 전송 성공:", data)
                    );
                })
                .catch((error) => {
                  console.error("푸시 구독 등록 실패:", error);
                });
            }
          });
        })
        .catch((error) => {
          console.error("Service Worker 등록 실패:", error);
        });
    }
  }, []);

  // VAPID 공개 키(Base64)를 Uint8Array로 변환하는 헬퍼 함수
  function urlBase64ToUint8Array(base64String: string) {
    const padding = "=".repeat((4 - (base64String.length % 4)) % 4);
    const base64 = (base64String + padding)
      .replace(/-/g, "+")
      .replace(/_/g, "/");
    const rawData = window.atob(base64);
    const outputArray = new Uint8Array(rawData.length);
    for (let i = 0; i < rawData.length; ++i) {
      outputArray[i] = rawData.charCodeAt(i);
    }
    return outputArray;
  }

  // 알림 전송 API를 호출하는 함수 (예시)
  const sendNotification = () => {
    const payload = "테스트 알림 메시지입니다.";
    const token = localStorage.getItem("accessToken");
    if (!token) {
      alert("로그인이 필요합니다.");
      window.location.href = "/login";
      return;
    }
    fetch("http://localhost:8080/api/v1/push/notify", {
      method: "POST",
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ payload }),
    })
      .then((response) => response.text())
      .then((data) => console.log("알림 전송 응답:", data))
      .catch((error) => console.error("알림 전송 오류:", error));
  };

  return (
    <main>
      <h1>푸시 알림 테스트 페이지</h1>
      <p>서비스 워커가 등록되고 구독 정보가 백엔드에 전송됩니다.</p>
      <button onClick={sendNotification}>알림 전송 (테스트)</button>
    </main>
  );
}
