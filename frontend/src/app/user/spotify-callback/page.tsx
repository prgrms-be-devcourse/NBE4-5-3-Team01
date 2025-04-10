"use client";

import { useEffect } from "react";
import { useSearchParams } from "next/navigation";

export default function SpotifyCallbackPage() {
  const searchParams = useSearchParams();

  useEffect(() => {
    const code = searchParams.get("code");
    const state = searchParams.get("state");

    if (!code || !state) {
      alert("잘못된 접근입니다.");
      return;
    }

    const connectSpotify = async () => {
      try {
        const res = await fetch(
          `http://localhost:8080/api/v1/spotify/callback?code=${code}&state=${state}`,
          {
            method: "GET",
            credentials: "include",
          }
        );
        const result = await res.json();
        if (result.code === "200") {
          const url = result.data.redirectUrl || "/user/profile";
          window.location.href = url;
        } else {
          alert("Spotify 연동 실패: " + result.msg);
        }
      } catch (err) {
        console.error("연동 오류:", err);
        alert("오류가 발생했습니다.");
      }
    };

    connectSpotify();
  }, [searchParams]);

  return <div>Spotify 연동 중입니다...</div>;
}
