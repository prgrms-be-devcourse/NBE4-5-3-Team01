"use client"; // 클라이언트 컴포넌트로 설정 (필수)

import React from "react";

export default function LoginPage() {
  const handleLogin = () => {
    window.location.href = "http://localhost:8080/api/v1/oauth2/authorization/spotify"; // Spring Boot의 OAuth2 로그인 엔드포인트
  };

  return (
    <div>
      <h1>Next.js 로그인 페이지 (App Router)</h1>
      <button onClick={handleLogin}>Spotify로 로그인</button>
    </div>
  );
}