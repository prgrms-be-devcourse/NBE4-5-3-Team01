"use client";

import Image from "next/image";
import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { getCookie } from "@/app/utils/cookie";
import axios from "axios";
import "./style.css";

interface User {
  id: string;
  email: string;
  name: string;
  nickName: string | null;
  userIntro: string | null;
  image: string | null;
  birthDay: string | null;
  createdDate: string;
  field: string | null;
}

export default function ProfilePage() {
  const [imageError, setImageError] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [isBioModalOpen, setIsBioModalOpen] = useState(false);
  const [isLogoutModalOpen, setIsLogoutModalOpen] = useState(false);
  const [isImageModalOpen, setIsImageModalOpen] = useState(false);
  const [selectedImage, setSelectedImage] = useState<File | null>(null);
  const [previewUrl, setPreviewUrl] = useState<string | null>(null);
  const [userData, setUserData] = useState<User | null>(null);
  const [bioText, setBioText] = useState("");
  const [nameText, setNameText] = useState("");
  const router = useRouter();

  // bioText, nameText 초기값 설정
  useEffect(() => {
    if (userData?.userIntro) {
      setBioText(userData.userIntro);
    }
    if (userData?.name) {
      setNameText(userData.name);
    }
  }, [userData]);

  // userData 상태가 변경될 때마다 로그 출력
  useEffect(() => {
    console.log("현재 userData 상태:", userData);
  }, [userData]);

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

  // getUsers API 호출
  useEffect(() => {
    const fetchUsers = async () => {
      try {
        const response = await axios.get(
          "http://localhost:8080/api/v1/user/getUsers",
          {
            withCredentials: true,
          }
        );

        console.log("API 응답 전체:", response);
        console.log("response.data:", response.data);

        // response.data.data에서 사용자 정보 추출
        const userData = response.data.data;

        console.log("처리할 userData:", userData);

        const user = {
          id: userData.id,
          email: userData.email,
          name: userData.name,
          nickName: userData.nickName,
          userIntro: userData.userIntro,
          image: userData.image,
          birthDay: userData.birthDay,
          createdDate: userData.createdDate,
          field: userData.field,
        };

        setUserData(user);
        console.log("최종 처리된 사용자 정보:", user);
      } catch (error) {
        console.error("사용자 정보 조회 중 오류 발생:", error);
      }
    };

    fetchUsers();
  }, []);

  const handleLogout = async () => {
    try {
      // 백엔드에서 모든 로그아웃 처리 (로컬 + 스포티파이)
      const response = await axios.get(
        "http://localhost:8080/api/v1/user/logout",
        {
          withCredentials: true,
        }
      );

      console.log("리스폰스테이터스", response.status);
      if (response.status === 200 || response.status === 302) {
        // 로그인 페이지로 리다이렉트
        router.push("/login");
      } else {
        throw new Error("로그아웃 실패");
      }
    } catch (error) {
      console.error("로그아웃 중 오류 발생:", error);
      alert("로그아웃 중 오류가 발생했습니다. 다시 시도해주세요.");
    }
  };

  const handleBioSubmit = async () => {
    try {
      const response = await axios.put(
        "http://localhost:8080/api/v1/user/userIntro",
        { userIntro: bioText },
        {
          withCredentials: true,
        }
      );

      if (response.status === 200) {
        setIsBioModalOpen(false);
        setUserData((prev) => (prev ? { ...prev, userIntro: bioText } : null));
        // 자기소개 업데이트 시간을 localStorage에 저장
        localStorage.setItem("lastBioUpdate", new Date().toISOString());
        // router.refresh(); // 페이지 새로고침
        window.location.reload();
      }
    } catch (error) {
      console.error("자기소개 업데이트 중 오류 발생:", error);
      alert("자기소개 업데이트 중 오류가 발생했습니다. 다시 시도해주세요.");
    }
  };

  const handleNameSubmit = async () => {
    try {
      const response = await axios.put(
        "http://localhost:8080/api/v1/user/profileName",
        { name: nameText },
        {
          withCredentials: true,
        }
      );

      if (response.status === 200) {
        setIsEditModalOpen(false);
        setUserData((prev) => (prev ? { ...prev, name: nameText } : null));
        // 프로필 이름 업데이트 시간을 localStorage에 저장
        localStorage.setItem("lastNameUpdate", new Date().toISOString());
        // router.refresh(); // 페이지 새로고침
        window.location.reload();
      }
    } catch (error) {
      console.error("이름 업데이트 중 오류 발생:", error);
      alert("이름 업데이트 중 오류가 발생했습니다. 다시 시도해주세요.");
    }
  };

  const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      setSelectedImage(file);
      setPreviewUrl(URL.createObjectURL(file));
    }
  };

  const handleImageSubmit = async () => {
    if (!selectedImage) {
      alert("이미지를 선택해주세요.");
      return;
    }

    try {
      const formData = new FormData();
      formData.append("image", selectedImage);

      const response = await axios.post(
        "http://localhost:8080/api/v1/user/image",
        formData,
        {
          withCredentials: true,
          headers: {
            "Content-Type": "multipart/form-data",
          },
        }
      );

      if (response.status === 200) {
        setIsImageModalOpen(false);
        setSelectedImage(null);
        setPreviewUrl(null);
        // 이미지 업데이트 시간을 localStorage에 저장
        localStorage.setItem("lastImageUpdate", new Date().toISOString());
        // router.refresh(); // 페이지 새로고침
        window.location.reload();
      }
    } catch (error) {
      console.error("이미지 업로드 중 오류 발생:", error);
      alert("이미지 업로드 중 오류가 발생했습니다. 다시 시도해주세요.");
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
            {userData?.image && !imageError ? (
              <Image
                src={`data:image/png;base64,${userData.image}`}
                alt="프로필 이미지"
                fill
                unoptimized
                className="rounded-full object-cover border-4 border-purple-200"
                onError={() => setImageError(true)}
              />
            ) : (
              <div className="w-full h-full rounded-full bg-purple-100 flex items-center justify-center border-4 border-purple-200">
                <svg
                  className="w-full h-full text-purple-300"
                  viewBox="0 0 36 36"
                  fill="currentColor"
                >
                  <path d="M18 0C8.06 0 0 8.06 0 18s8.06 18 18 18 18-8.06 18-18S27.94 0 18 0zm0 6c3.31 0 6 2.69 6 6s-2.69 6-6 6-6-2.69-6-6 2.69-6 6-6zm0 25.2c-5 0-9.42-2.56-12-6.44.06-3.98 8-6.16 12-6.16 3.98 0 11.94 2.18 12 6.16-2.58 3.88-7 6.44-12 6.44z" />
                </svg>
              </div>
            )}
            {/* 이미지 변경 버튼 */}
            <button
              className="absolute bottom-0 right-0 bg-purple-500 text-white p-2 rounded-full hover:bg-purple-600 transition-colors"
              onClick={() => setIsImageModalOpen(true)}
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
          <h2 className="text-2xl font-bold mb-2">
            {userData?.name || "로딩 중..."}
          </h2>
          <p className="text-gray-600 mb-4">
            {userData?.email || "로딩 중..."}
          </p>
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
                <span className="text-lg text-gray-600">Email</span>
              </div>
              <span className="text-gray-400">
                {userData?.email || "로딩 중..."}
              </span>
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
            className="modal-overlay"
            onClick={() => setIsLogoutModalOpen(false)}
          >
            <div className="modal" onClick={(e) => e.stopPropagation()}>
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
          className="modal-overlay"
          onClick={() => setIsEditModalOpen(false)}
        >
          <div className="modal" onClick={(e) => e.stopPropagation()}>
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
                  value={nameText}
                  onChange={(e) => setNameText(e.target.value)}
                />
              </div>
              <button
                onClick={handleNameSubmit}
                className="w-full bg-purple-500 text-white py-2 rounded-md hover:bg-purple-600 transition-colors"
              >
                저장하기
              </button>
            </div>
          </div>
        </div>
      )}

      {/* 자기소개 모달 */}
      {isBioModalOpen && (
        <div className="modal-overlay" onClick={() => setIsBioModalOpen(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
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
                value={bioText}
                onChange={(e) => setBioText(e.target.value)}
              />
              <button
                onClick={handleBioSubmit}
                className="w-full bg-purple-500 text-white py-2 rounded-md hover:bg-purple-600 transition-colors"
              >
                저장하기
              </button>
            </div>
          </div>
        </div>
      )}

      {/* 이미지 업로드 모달 */}
      {isImageModalOpen && (
        <div
          className="modal-overlay"
          onClick={() => setIsImageModalOpen(false)}
        >
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <div className="flex justify-between items-center mb-4">
              <h3 className="text-xl font-semibold">프로필 이미지 변경</h3>
              <button
                onClick={() => setIsImageModalOpen(false)}
                className="text-gray-500 hover:text-gray-700"
              >
                ✕
              </button>
            </div>
            <div className="space-y-4">
              <div className="flex flex-col items-center">
                {previewUrl ? (
                  <div className="relative w-32 h-32 mb-4">
                    <Image
                      src={previewUrl}
                      alt="프로필 이미지 미리보기"
                      fill
                      className="rounded-full object-cover"
                    />
                  </div>
                ) : (
                  <div className="w-32 h-32 rounded-full bg-purple-100 flex items-center justify-center mb-4">
                    <svg
                      className="w-16 h-16 text-purple-300"
                      fill="none"
                      stroke="currentColor"
                      viewBox="0 0 24 24"
                    >
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        strokeWidth={2}
                        d="M12 6v6m0 0v6m0-6h6m-6 0H6"
                      />
                    </svg>
                  </div>
                )}
                <input
                  type="file"
                  accept="image/*"
                  className="hidden"
                  id="imageInput"
                  onChange={handleImageChange}
                />
                <label
                  htmlFor="imageInput"
                  className="cursor-pointer bg-purple-50 text-purple-500 px-4 py-2 rounded-md hover:bg-purple-100 transition-colors"
                >
                  이미지 선택
                </label>
              </div>
              <button
                onClick={handleImageSubmit}
                className="w-full bg-purple-500 text-white py-2 rounded-md hover:bg-purple-600 transition-colors"
                disabled={!selectedImage}
              >
                저장하기
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
