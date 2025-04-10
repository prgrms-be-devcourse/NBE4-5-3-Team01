"use client";

import React, { useState } from "react";
import { useRouter } from "next/navigation";
import axios from "axios";

export default function LoginPage() {
  const [loginId, setLoginId] = useState("");
  const [password, setPassword] = useState("");
  const router = useRouter();

  // 일반 로그인 처리 (예시: API 호출 후 성공 시 콜백 페이지로 이동)
  const handleNormalLogin = async () => {
    try {
      const response = await axios.post(
        "http://localhost:8080/api/v1/user/login",
        { loginId, password },
        { withCredentials: true }
      );
      console.log("로그인", response);

      const accessToken = response.data.data.access_token;
      const refreshToken = response.data.data.refresh_token;
      const spotifyAccessToken = response.data.data.spotify_access_token;

      if (response.status === 200) {
        router.push(
          `/login/callback?access_token=${accessToken}&refresh_token=${refreshToken}&spotify_access_token=${spotifyAccessToken}`
        );
      }
    } catch (error) {
      console.error("일반 로그인 오류:", error);
      alert("로그인에 실패했습니다. 다시 시도해주세요.");
    }
  };

  // 스포티파이 로그인 처리
  const handleSpotifyLogin = () => {
    window.location.href =
      "http://localhost:8080/api/v1/oauth2/authorization/spotify";
  };

  // 회원가입 페이지 이동
  const handleSignup = () => {
    router.push("/signup");
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-white">
      <div className="bg-white p-8 rounded-lg shadow-lg text-center w-80">
        <h1 className="text-2xl font-bold mb-6">뮤직 캘린더</h1>
        {/* 로그인/패스워드 입력 필드 */}
        <div className="space-y-4 mb-6">
          <input
            type="text"
            placeholder="로그인"
            value={loginId}
            onChange={(e) => setLoginId(e.target.value)}
            className="w-full p-3 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-purple-500"
          />
          <input
            type="password"
            placeholder="패스워드"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="w-full p-3 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-purple-500"
          />
        </div>
        {/* 버튼 그룹 */}
        <div className="space-y-4">
          {/* 일반 로그인 버튼 */}
          <button
            onClick={handleNormalLogin}
            className="w-full bg-blue-500 text-white px-6 py-3 rounded-full font-semibold hover:bg-blue-600 transition-colors"
          >
            로그인
          </button>
          {/* 스포티파이 로그인 버튼 */}
          <button
            onClick={handleSpotifyLogin}
            className="w-full bg-[#1DB954] text-white px-6 py-3 rounded-full font-semibold hover:bg-[#1ed760] transition-colors flex items-center gap-2"
          >
            <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
              <path d="M12 0C5.4 0 0 5.4 0 12s5.4 12 12 12 12-5.4 12-12S18.66 0 12 0zm5.521 17.34c-.24.359-.66.48-1.021.24-2.82-1.74-6.36-2.101-10.561-1.141-.418.122-.779-.179-.899-.539-.12-.421.18-.78.54-.9 4.56-1.021 8.52-.6 11.64 1.32.42.18.479.659.301 1.02zm1.44-3.3c-.301.42-.841.6-1.262.3-3.239-1.98-8.159-2.58-11.939-1.38-.479.12-1.02-.12-1.14-.6-.12-.48.12-1.021.6-1.141C9.6 9.9 15 10.561 18.72 12.84c.361.181.54.78.241 1.2zm.12-3.36C15.24 8.4 8.82 8.16 5.16 9.301c-.6.179-1.2-.181-1.38-.721-.18-.601.18-1.2.72-1.381 4.26-1.26 11.28-1.02 15.721 1.621.539.3.719 1.02.419 1.56-.299.421-1.02.599-1.559.3z" />
            </svg>
            스포티파이로 로그인 하기
          </button>
          {/* 회원가입 버튼 - 회색으로 변경 */}
          <button
            onClick={handleSignup}
            className="w-full bg-gray-500 text-white px-6 py-3 rounded-full font-semibold hover:bg-gray-600 transition-colors"
          >
            회원가입
          </button>
        </div>
      </div>
    </div>
  );
}
