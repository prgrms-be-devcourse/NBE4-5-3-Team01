self.addEventListener("push", (event) => {
  let payloadText = "새로운 알림이 도착했습니다."; // 기본 메시지
  let title = "📢 새로운 알림!"; // 기본 제목
  let iconUrl = "/music_calendar.png"; // 아이콘 이미지

  try {
    const data = event.data.json(); // 서버에서 전달된 JSON 데이터 파싱
    title = data.title || title; // 서버에서 전달된 title 사용
    payloadText = data.message || payloadText; // 서버에서 전달된 message 사용
  } catch (error) {
    console.error("JSON 파싱 실패, 텍스트로 처리", error);
    payloadText = event.data ? event.data.text() : payloadText;
  }

  const options = {
    body: payloadText,
    icon: iconUrl, // 작은 아이콘 (앱 로고 추천)
    badge: "/bell.png", // 상태 표시용 작은 배지
    data: {
      url: "http://localhost:3000/notifications",
    },
  };

  event.waitUntil(self.registration.showNotification(title, options));
});

self.addEventListener("notificationclick", (event) => {
  event.notification.close();

  const url = event.notification.data.url;
  event.waitUntil(
    clients.matchAll({ type: "window" }).then((clientList) => {
      for (let client of clientList) {
        if (client.url === url && "focus" in client) {
          return client.focus();
        }
      }
      if (clients.openWindow) {
        return clients.openWindow(url);
      }
    })
  );
});
