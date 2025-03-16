"use client"; // 클라이언트 컴포넌트로 설정 (필수)

import React, { useEffect } from "react";
import { useRouter } from "next/navigation";

export default function LoginPage() {
  const router = useRouter();

  useEffect(() => {
    // 쿠키에서 토큰 확인
    const getCookie = (name: string) => {
      const value = `; ${document.cookie}`;
      const parts = value.split(`; ${name}=`);
      if (parts.length === 2) return parts.pop()?.split(";").shift();
      return null;
    };

    const accessToken = getCookie("accessToken");
    if (accessToken) {
      // 토큰이 있으면 홈으로 리다이렉트
      router.push("/");
    }
  }, [router]);

  const handleLogin = () => {
    window.location.href =
      "http://localhost:8080/api/v1/oauth2/authorization/spotify"; // Spring Boot의 OAuth2 로그인 엔드포인트
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-white">
      <div className="bg-white p-8 rounded-lg shadow-lg text-center">
        <h1 className="text-2xl font-bold mb-6">뮤직 캘린더</h1>
        <button
          onClick={handleLogin}
          className="bg-[#1DB954] text-white px-6 py-3 rounded-full font-semibold hover:bg-[#1ed760] transition-colors flex items-center gap-2"
        >
          <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 0C5.4 0 0 5.4 0 12s5.4 12 12 12 12-5.4 12-12S18.66 0 12 0zm5.521 17.34c-.24.359-.66.48-1.021.24-2.82-1.74-6.36-2.101-10.561-1.141-.418.122-.779-.179-.899-.539-.12-.421.18-.78.54-.9 4.56-1.021 8.52-.6 11.64 1.32.42.18.479.659.301 1.02zm1.44-3.3c-.301.42-.841.6-1.262.3-3.239-1.98-8.159-2.58-11.939-1.38-.479.12-1.02-.12-1.14-.6-.12-.48.12-1.021.6-1.141C9.6 9.9 15 10.561 18.72 12.84c.361.181.54.78.241 1.2zm.12-3.36C15.24 8.4 8.82 8.16 5.16 9.301c-.6.179-1.2-.181-1.38-.721-.18-.601.18-1.2.72-1.381 4.26-1.26 11.28-1.02 15.721 1.621.539.3.719 1.02.419 1.56-.299.421-1.02.599-1.559.3z" />
          </svg>
          스포티파이로 로그인 하기
        </button>
      </div>
    </div>
  );
}
