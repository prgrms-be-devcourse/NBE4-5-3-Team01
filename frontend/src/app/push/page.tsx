"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";

export default function Push() {
  const router = useRouter();

  useEffect(() => {
    if ("serviceWorker" in navigator && "PushManager" in window) {
      navigator.serviceWorker
        .register("/sw.js")
        .then((registration) => {
          console.log("Service Worker 등록 성공:", registration);

          Notification.requestPermission().then((permission) => {
            console.log("알림 권한 상태:", permission);
            if (permission === "granted") {
              registration.pushManager
                .subscribe({
                  userVisibleOnly: true,
                  applicationServerKey: urlBase64ToUint8Array(
                    "BJSowR6aDh89tu-QupRzNmwjVrr63q2Wbstv536bFya4emx8YigwBkd4Ogbusg6X6uzeNzOkY678PlY9U66xSIU"
                  ),
                })
                .then((subscription) => {
                  console.log("푸시 구독 성공:", subscription);

                  return fetch("http://localhost:8080/api/v1/push/subscribe", {
                    method: "POST",
                    credentials: "include",
                    headers: {
                      "Content-Type": "application/json",
                    },
                    body: JSON.stringify(subscription),
                  });
                })
                .then((response) => response.text())
                .then((data) => {
                  console.log("백엔드에 구독 정보 전송 성공:", data);
                  router.push("/user/profile"); // 구독 완료 후 리다이렉트
                })
                .catch((error) => console.error("푸시 구독 등록 실패:", error));
            } else {
              console.log("알림 권한이 거부됨");
              router.push("/user/profile"); // 권한 거부 시에도 리다이렉트
            }
          });
        })
        .catch((error) => {
          console.error("Service Worker 등록 실패:", error);
          router.push("/user/profile"); // SW 등록 실패해도 리다이렉트
        });
    } else {
      console.log("푸시 알림을 지원하지 않는 브라우저");
      router.push("/user/profile"); // 지원되지 않으면 리다이렉트
    }
  }, [router]);

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

  return null; // 화면을 렌더링하지 않음
}
