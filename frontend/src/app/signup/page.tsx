"use client";

import React, { useState } from "react";
import { useRouter } from "next/navigation";
import axios from "axios";

export default function SignupPage() {
  const [name, setName] = useState("");
  const [id, setId] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [isLoadingEmailAuth, setIsLoadingEmailAuth] = useState(false); // 로딩 상태

  // 아이디 중복 확인 성공 여부
  const [isIdAvailable, setIsIdAvailable] = useState(false);
  // 이메일 인증 성공 여부
  const [isEmailVerified, setIsEmailVerified] = useState(false);

  // 모달 관련 상태
  const [showModal, setShowModal] = useState(false);
  const [inputVerificationCode, setInputVerificationCode] = useState("");
  const [sentVerificationCode, setSentVerificationCode] = useState("");

  const router = useRouter();

  // 아이디 중복 확인 처리
  const handleCheckDuplicate = async () => {
    if (!id) {
      alert("아이디를 입력하세요.");
      return;
    }
    try {
      const response = await axios.get(
        `http://localhost:8080/api/v1/user/check-duplicate?checkId=${encodeURIComponent(
          id
        )}`
      );
      // 응답값이 true이면 사용 가능한 아이디
      if (response.data.data) {
        alert("사용 가능한 아이디입니다.");
        setIsIdAvailable(true);
      } else {
        alert("이미 사용 중인 아이디입니다.");
        setIsIdAvailable(false);
      }
    } catch (error) {
      console.error("아이디 중복 확인 오류:", error);
      alert("중복 확인에 실패했습니다.");
      setIsIdAvailable(false);
    }
  };

  // 인증번호 받기 처리 (이메일 인증)
  const handleEmailAuth = async () => {
    if (!email) {
      alert("이메일을 입력하세요.");
      return;
    }
    // 간단한 이메일 형식 검증
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
      alert("이메일 형식이 아닙니다.");
      return;
    }

    setIsLoadingEmailAuth(true); // 로딩 시작
    try {
      const response = await axios.get(
        `http://localhost:8080/api/v1/userEmail/emailAuth?email=${encodeURIComponent(
          email
        )}`
      );
      if (response.status === 200) {
        // 인증번호가 전송되면 바로 모달창을 띄움
        alert("인증번호가 발송 되었습니다.");
        setSentVerificationCode(response.data.data);
        setShowModal(true);
      }
    } catch (error) {
      console.error("이메일 인증 오류:", error);
      alert("이메일 인증에 실패했습니다. 다시 시도해주세요.");
      setIsEmailVerified(false);
    } finally {
      setIsLoadingEmailAuth(false); // 로딩 종료
    }
  };

  // 모달창 내 인증번호 검증 처리
  const handleVerifyCode = () => {
    if (inputVerificationCode === sentVerificationCode) {
      alert("이메일 인증이 완료되었습니다.");
      setIsEmailVerified(true);
      setShowModal(false);
    } else {
      alert("인증번호가 일치하지 않습니다. 다시 시도해주세요.");
    }
  };

  // 회원가입 처리
  const handleSignup = async () => {
    if (password !== confirmPassword) {
      alert("비밀번호가 일치하지 않습니다.");
      return;
    }
    if (!(password.length >= 8 && password.length <= 20)) {
      alert("비밀번호는 최소 8자리 이상 20자리 이하이어야 합니다.");
      return;
    }
    try {
      const response = await axios.post(
        "http://localhost:8080/api/v1/user/signup",
        { name, id, email, password },
        { withCredentials: true }
      );
      if (response.status === 200) {
        alert("회원가입 완료");
        router.push("/login");
      }
    } catch (error) {
      console.error("회원가입 오류:", error);
      alert("회원가입에 실패했습니다. 다시 시도해주세요.");
    }
  };

  // 비밀번호 길이 유효성 체크
  const isPasswordValid = password.length >= 8 && password.length <= 20;

  // 모든 조건이 만족되었는지 확인
  const isFormValid =
    isIdAvailable &&
    isEmailVerified &&
    password &&
    confirmPassword &&
    password === confirmPassword &&
    isPasswordValid;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-gray-50">
      <div className="bg-white p-8 rounded-lg shadow-md text-center w-96">
        <h1 className="text-3xl font-bold mb-8">회원가입</h1>
        <div className="space-y-6 mb-8">
          {/* 이름 */}
          <input
            type="text"
            placeholder="이름"
            value={name}
            onChange={(e) => setName(e.target.value)}
            className="w-full px-4 py-3 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-purple-500"
          />
          {/* 아이디 + 중복확인 */}
          <div className="flex items-center">
            <input
              type="text"
              placeholder="아이디"
              value={id}
              onChange={(e) => setId(e.target.value)}
              className="flex-grow px-4 py-3 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-purple-500"
            />
            <button
              onClick={handleCheckDuplicate}
              className="ml-2 w-28 px-4 py-3 bg-indigo-500 text-white text-sm rounded-md hover:bg-indigo-600 transition-colors whitespace-nowrap"
            >
              중복확인
            </button>
          </div>
          {/* 이메일 + 인증번호 받기 */}
          <div className="flex items-center">
            <input
              type="email"
              placeholder="이메일"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="flex-grow px-4 py-3 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-purple-500"
            />
            <button
              onClick={handleEmailAuth}
              disabled={isLoadingEmailAuth}
              className={`ml-2 w-28 px-4 py-3 text-white text-sm rounded-md transition-colors whitespace-nowrap ${
                isLoadingEmailAuth
                  ? "bg-gray-400 cursor-not-allowed"
                  : "bg-orange-500 hover:bg-orange-600"
              }`}
            >
              {isLoadingEmailAuth ? "전송 중..." : "인증번호 받기"}
            </button>
          </div>
          {/* 비밀번호 */}
          <div>
            <input
              type="password"
              placeholder="비밀번호"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="w-full px-4 py-3 mb-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-purple-500"
            />
            {password && !isPasswordValid && (
              <p className="mt-1 text-left text-sm text-red-500">
                비밀번호는 최소 8자리 이상 20자리 이하이어야 합니다.
              </p>
            )}
          </div>
          {/* 비밀번호 확인 및 일치 여부 메시지 */}
          <div>
            <input
              type="password"
              placeholder="비밀번호 확인"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              className="w-full px-4 py-3 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-purple-500"
            />
            {password && confirmPassword && (
              <p
                className="mt-1 text-left text-sm"
                style={{
                  color: password === confirmPassword ? "green" : "red",
                }}
              >
                {password === confirmPassword
                  ? "비밀번호가 일치합니다."
                  : "비밀번호가 일치하지 않습니다."}
              </p>
            )}
          </div>
        </div>
        <button
          onClick={handleSignup}
          disabled={!isFormValid}
          className={`w-full text-white px-6 py-3 rounded-full font-semibold transition-colors ${
            isFormValid
              ? "bg-gray-500 hover:bg-gray-600"
              : "bg-gray-300 cursor-not-allowed"
          }`}
        >
          회원가입
        </button>
      </div>

      {/* 모달창: 인증번호 입력 */}
      {showModal && (
        <div className="fixed inset-0 z-60 flex items-center justify-center backdrop-blur-sm bg-transparent">
          <div className="bg-white p-6 rounded-lg shadow-md w-80">
            <h2 className="text-xl font-bold mb-4">인증번호 입력</h2>
            <input
              type="text"
              placeholder="인증번호"
              value={inputVerificationCode}
              onChange={(e) => setInputVerificationCode(e.target.value)}
              className="w-full px-4 py-2 mb-4 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-purple-500"
            />
            <button
              onClick={handleVerifyCode}
              className="w-full bg-blue-500 text-white px-4 py-2 rounded-md hover:bg-blue-600 transition-colors"
            >
              인증하기
            </button>
            <button
              onClick={() => setShowModal(false)}
              className="w-full mt-2 bg-gray-300 text-black px-4 py-2 rounded-md hover:bg-gray-400 transition-colors"
            >
              취소
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
