"use client";

import { useState, useEffect } from "react";
import Link from "next/link";
import Image from "next/image";
import axios from "axios";

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

export default function Sidebar() {
  const [isHovered, setIsHovered] = useState(false);
  const [userData, setUserData] = useState<User | null>(null);
  const [lastBioUpdate, setLastBioUpdate] = useState<string | null>(null);
  const [lastNameUpdate, setLastNameUpdate] = useState<string | null>(null);
  const [lastImageUpdate, setLastImageUpdate] = useState<string | null>(null);

  const fetchUsers = async () => {
    try {
      const response = await axios.get(
        "http://localhost:8080/api/v1/user/getUsers",
        {
          withCredentials: true,
        }
      );

      const userData = response.data;
      setUserData(userData);
    } catch (error) {
      console.error("사용자 정보 조회 중 오류 발생:", error);
    }
  };

  // 초기 데이터 로드
  useEffect(() => {
    fetchUsers();
  }, []);

  // localStorage의 lastBioUpdate, lastNameUpdate, lastImageUpdate 변경 감지
  useEffect(() => {
    const checkForUpdates = () => {
      const newBioUpdate = localStorage.getItem("lastBioUpdate");
      const newNameUpdate = localStorage.getItem("lastNameUpdate");
      const newImageUpdate = localStorage.getItem("lastImageUpdate");

      if (
        newBioUpdate !== lastBioUpdate ||
        newNameUpdate !== lastNameUpdate ||
        newImageUpdate !== lastImageUpdate
      ) {
        setLastBioUpdate(newBioUpdate);
        setLastNameUpdate(newNameUpdate);
        setLastImageUpdate(newImageUpdate);
        fetchUsers(); // 새로운 데이터 가져오기
      }
    };

    // 1초마다 업데이트 확인
    const interval = setInterval(checkForUpdates, 1000);

    return () => clearInterval(interval);
  }, [lastBioUpdate, lastNameUpdate, lastImageUpdate]);

  return (
    <div id="nav-bar">
      <input id="nav-toggle" type="checkbox" />
      <div id="nav-header">
        <Link id="nav-title" href="/calendar">
          <Image
            src="/music_calendar.png"
            alt="설정 아이콘"
            width={40}
            height={50}
          />
          {/* Music Calendar */}
        </Link>
        <Link
          href="/notifications"
          onMouseEnter={() => setIsHovered(true)}
          onMouseLeave={() => setIsHovered(false)}
        >
          <i
            className={`fa-regular fa-bell fa-lg ${
              isHovered ? "fa-shake" : ""
            }`}
          ></i>
        </Link>
        <label htmlFor="nav-toggle">
          <span id="nav-toggle-burger"></span>
        </label>
        <hr />
      </div>
      <div id="nav-content">
        <div className="navbutton">
          <Link href="/user/profile">
            <div className="navLink">
              <i className="fas fa-solid fa-user"></i>
              <span>Profile</span>
            </div>
          </Link>
        </div>
        <div className="navbutton">
          <Link href="/calendar">
            <div className="navLink">
              <i className="fas fa-solid fa-calendar"></i>
              <span>Calendar</span>
            </div>
          </Link>
        </div>
        <div className="navbutton">
          <Link href="/music">
            <div className="navLink">
              <i className="fas fa-music"></i>
              <span>Music</span>
            </div>
          </Link>
        </div>
        <hr />
        <div className="navbutton">
          <Link href="/following">
            <div className="navLink">
              <i className="fas fa-heart"></i>
              <span>Following</span>
            </div>
          </Link>
        </div>
        <div className="navbutton">
          <Link href="/user/recap">
            <div className="navLink">
              <i className="fas fa-solid fa-chart-simple"></i>
              <span>Recap</span>
            </div>
          </Link>
        </div>
        <div className="navbutton">
          <Link href="/notifications">
            <div className="navLink">
              <i className="fas fa-solid fa-bell"></i>
              <span>Notification</span>
            </div>
          </Link>
        </div>
        <hr />
        <div className="navbutton">
          <Link href="/">
            <div className="navLink">
              <i
                className="fas fa-brands fa-spotify"
                style={{ fontWeight: 400 }}
              ></i>
              <span>Spotify</span>
            </div>
          </Link>
        </div>
        <div className="navbutton">
          <Link href="/setting">
            <div className="navLink">
              <i className="fas fa-solid fa-gear"></i>
              <span>Setting</span>
            </div>
          </Link>
        </div>
        <div id="nav-content-highlight"></div>
      </div>
      <input id="nav-footer-toggle" type="checkbox" />
      <div id="nav-footer">
        <div id="nav-footer-heading">
          <div id="nav-footer-avatar">
            <Image src="/user.png" alt="Avatar" width={28} height={28} />
          </div>
          <div id="nav-footer-titlebox">
            <Link id="nav-footer-title" href="/">
              {userData?.name || "로딩 중..."}
            </Link>
            {/* <span id="nav-footer-subtitle">

            </span> */}
          </div>
          <label htmlFor="nav-footer-toggle">
            <i className="fas fa-caret-up"></i>
          </label>
        </div>
        <div id="nav-footer-content">
          <p>
            {userData?.userIntro ||
              "자기소개를 작성하면 나오는 곳 입니다. 아무거나 적으면 됩니다. 자신을 소개해보세요. 자신을 한 문장으로 알려주세요."}
          </p>
        </div>
      </div>
    </div>
  );
}
