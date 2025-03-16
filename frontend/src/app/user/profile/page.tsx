"use client";

import Image from "next/image";
import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { getCookie } from "@/app/utils/cookie";
import axios from "axios";

export default function ProfilePage() {
  const [imageError, setImageError] = useState(true);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [isBioModalOpen, setIsBioModalOpen] = useState(false);
  const [isLogoutModalOpen, setIsLogoutModalOpen] = useState(false);
  const router = useRouter();

  // 모달이 열려있을 때 body 스크롤 방지
  useEffect(() => {
    if (isEditModalOpen || isBioModalOpen) {
      document.body.style.overflow = "hidden";
    } else {
      document.body.style.overflow = "unset";
    }
    return () => {
      document.body.style.overflow = "unset";
    };
  }, [isEditModalOpen, isBioModalOpen]);

  // 컴포넌트 마운트 시 쿠키 확인
  useEffect(() => {
    console.log("현재 모든 쿠키:", document.cookie);
    const token = getCookie("accessToken");
    console.log("마운트 시 토큰:", token);
  }, []);

  const handleLogout = async () => {
    try {
      const response = await axios.get(
        "http://localhost:8080/api/v1/user/testApiCookie",
        {
          withCredentials: true,
        }
      );

      if (response.status === 200) {
        // 모든 관련 쿠키 삭제
        document.cookie =
          "accessToken=; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT; secure; samesite=strict";
        document.cookie =
          "refreshToken=; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT; secure; samesite=strict";
        document.cookie =
          "spotifyAccessToken=; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT; secure; samesite=strict";

        router.push("/login");
      } else {
        throw new Error("로그아웃 실패");
      }
    } catch (error) {
      console.error("로그아웃 중 오류 발생:", error);
      alert("로그아웃 중 오류가 발생했습니다. 다시 시도해주세요.");
    }
  };

  return (
    <div className="p-8">
      {/* 프로필 이미지 및 기본 정보 */}
      <div
        className={`${
          isEditModalOpen || isBioModalOpen ? "pointer-events-none" : ""
        }`}
      >
        <div className="flex flex-col items-center mb-8">
          <div className="relative w-32 h-32 mb-4">
            {imageError ? (
              <div className="w-full h-full rounded-full bg-purple-100 flex items-center justify-center border-4 border-purple-200">
                <svg
                  className="w-full h-full text-purple-300"
                  viewBox="0 0 36 36"
                  fill="currentColor"
                >
                  <path d="M18 0C8.06 0 0 8.06 0 18s8.06 18 18 18 18-8.06 18-18S27.94 0 18 0zm0 6c3.31 0 6 2.69 6 6s-2.69 6-6 6-6-2.69-6-6 2.69-6 6-6zm0 25.2c-5 0-9.42-2.56-12-6.44.06-3.98 8-6.16 12-6.16 3.98 0 11.94 2.18 12 6.16-2.58 3.88-7 6.44-12 6.44z" />
                </svg>
              </div>
            ) : (
              <Image
                src="/profile-default.png"
                alt="프로필 이미지"
                fill
                className="rounded-full object-cover border-4 border-purple-200"
                onError={() => setImageError(true)}
              />
            )}
            <button
              className="absolute bottom-0 right-0 bg-purple-500 text-white p-2 rounded-full hover:bg-purple-600 transition-colors"
              onClick={() => {
                // TODO: 이미지 변경 로직 구현
              }}
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                className="h-5 w-5"
                viewBox="0 0 20 20"
                fill="currentColor"
              >
                <path d="M13.586 3.586a2 2 0 112.828 2.828l-.793.793-2.828-2.828.793-.793zM11.379 5.793L3 14.172V17h2.828l8.38-8.379-2.83-2.828z" />
              </svg>
            </button>
          </div>
          <h2 className="text-2xl font-bold mb-2">프로토일 앨범다</h2>
          <p className="text-gray-600 mb-4">proto@example.com</p>
        </div>
      </div>

      <div className="space-y-8">
        {/* 기본 정보 섹션 */}
        <section className="bg-purple-50 rounded-lg p-6">
          <h2 className="text-xl font-semibold mb-4">기본 정보</h2>
          <div className="space-y-2">
            <button
              onClick={() => setIsEditModalOpen(true)}
              className="w-full flex items-center justify-between p-4 bg-white rounded-lg transition-colors border border-purple-100 hover:bg-purple-100"
            >
              <div className="flex items-center gap-3">
                <span className="text-lg">프로필 수정</span>
              </div>
              <span className="text-gray-400">›</span>
            </button>
            <button
              onClick={() => setIsBioModalOpen(true)}
              className="w-full flex items-center justify-between p-4 bg-white rounded-lg transition-colors border border-purple-100 hover:bg-purple-100"
            >
              <div className="flex items-center gap-3">
                <span className="text-lg">자기소개</span>
              </div>
              <span className="text-gray-400">›</span>
            </button>
            <div className="flex items-center justify-between p-4 bg-white rounded-lg border border-purple-100">
              <div className="flex items-center gap-3">
                <span className="text-lg text-gray-600">ID</span>
              </div>
              <span className="text-gray-400">proto123</span>
            </div>
          </div>
        </section>

        {/* 연결된 서비스 섹션 */}
        <section className="bg-purple-50 rounded-lg p-6 relative">
          <h2 className="text-xl font-semibold mb-4">연결된 서비스</h2>
          <button
            onClick={() => setIsLogoutModalOpen(true)}
            className="absolute top-6 right-6 px-4 py-2 text-sm text-red-600 hover:bg-red-50 rounded-md transition-colors"
          >
            로그아웃
          </button>
          <div className="space-y-2">
            <div className="flex items-center justify-between p-4 bg-white rounded-lg transition-colors border border-purple-100 hover:bg-purple-100">
              <div className="flex items-center gap-3">
                <svg
                  viewBox="0 0 24 24"
                  className="w-6 h-6 text-[#1DB954]"
                  fill="currentColor"
                >
                  <path d="M12 0C5.4 0 0 5.4 0 12s5.4 12 12 12 12-5.4 12-12S18.66 0 12 0zm5.521 17.34c-.24.359-.66.48-1.021.24-2.82-1.74-6.36-2.101-10.561-1.141-.418.122-.779-.179-.899-.539-.12-.421.18-.78.54-.9 4.56-1.021 8.52-.6 11.64 1.32.42.18.479.659.301 1.02zm1.44-3.3c-.301.42-.841.6-1.262.3-3.239-1.98-8.159-2.58-11.939-1.38-.479.12-1.02-.12-1.14-.6-.12-.48.12-1.021.6-1.141C9.6 9.9 15 10.561 18.72 12.84c.361.181.54.78.241 1.2zm.12-3.36C15.24 8.4 8.82 8.16 5.16 9.301c-.6.179-1.2-.181-1.38-.721-.18-.601.18-1.2.72-1.381 4.26-1.26 11.28-1.02 15.721 1.621.539.3.719 1.02.419 1.56-.299.421-1.02.599-1.559.3z" />
                </svg>
                <span className="text-lg">Spotify</span>
              </div>
              <span className="text-green-500">연결됨</span>
            </div>
          </div>
        </section>

        {/* 로그아웃 확인 모달 */}
        {isLogoutModalOpen && (
          <div
            className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50"
            onClick={() => setIsLogoutModalOpen(false)}
          >
            <div
              className="bg-white rounded-lg p-6 w-full max-w-sm"
              onClick={(e) => e.stopPropagation()}
            >
              <div className="text-center mb-6">
                <h3 className="text-xl font-semibold mb-2">로그아웃</h3>
                <p className="text-gray-600">정말 로그아웃 하시겠습니까?</p>
              </div>
              <div className="flex gap-4">
                <button
                  onClick={() => setIsLogoutModalOpen(false)}
                  className="flex-1 py-2 border border-gray-300 rounded-md hover:bg-gray-50 transition-colors"
                >
                  취소
                </button>
                <button
                  onClick={handleLogout}
                  className="flex-1 py-2 bg-red-500 text-white rounded-md hover:bg-red-600 transition-colors"
                >
                  로그아웃
                </button>
              </div>
            </div>
          </div>
        )}
      </div>

      {/* 프로필 수정 모달 */}
      {isEditModalOpen && (
        <div
          className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50"
          onClick={() => setIsEditModalOpen(false)}
        >
          <div
            className="bg-white rounded-lg p-6 w-full max-w-md"
            onClick={(e) => e.stopPropagation()}
          >
            <div className="flex justify-between items-center mb-4">
              <h3 className="text-xl font-semibold">프로필 수정</h3>
              <button
                onClick={() => setIsEditModalOpen(false)}
                className="text-gray-500 hover:text-gray-700"
              >
                ✕
              </button>
            </div>
            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  이름
                </label>
                <input
                  type="text"
                  className="w-full p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-purple-500"
                  placeholder="이름을 입력하세요"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  이메일
                </label>
                <input
                  type="email"
                  className="w-full p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-purple-500"
                  placeholder="이메일을 입력하세요"
                />
              </div>
              <button className="w-full bg-purple-500 text-white py-2 rounded-md hover:bg-purple-600 transition-colors">
                저장하기
              </button>
            </div>
          </div>
        </div>
      )}

      {/* 자기소개 모달 */}
      {isBioModalOpen && (
        <div
          className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50"
          onClick={() => setIsBioModalOpen(false)}
        >
          <div
            className="bg-white rounded-lg p-6 w-full max-w-md"
            onClick={(e) => e.stopPropagation()}
          >
            <div className="flex justify-between items-center mb-4">
              <h3 className="text-xl font-semibold">자기소개</h3>
              <button
                onClick={() => setIsBioModalOpen(false)}
                className="text-gray-500 hover:text-gray-700"
              >
                ✕
              </button>
            </div>
            <div className="space-y-4">
              <textarea
                className="w-full p-2 border border-gray-300 rounded-md h-32 resize-none focus:outline-none focus:ring-2 focus:ring-purple-500"
                placeholder="자기소개를 입력하세요"
              />
              <button className="w-full bg-purple-500 text-white py-2 rounded-md hover:bg-purple-600 transition-colors">
                저장하기
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
