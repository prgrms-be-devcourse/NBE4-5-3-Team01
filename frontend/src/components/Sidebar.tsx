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

  const fetchUsers = async () => {
    try {
      const response = await axios.get(
        "http://localhost:8080/api/v1/user/getUsers",
        {
          withCredentials: true,
        }
      );

      const userData = response.data.data;
      setUserData(userData);
    } catch (error) {
      console.error("사용자 정보 조회 중 오류 발생:", error);
    }
  };

  // 초기 데이터 로드
  useEffect(() => {
    fetchUsers();
  }, []);

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
            className={`fa-regular fa-bell fa-lg ${isHovered ? "fa-shake" : ""
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
          <Link href="/notifications">
            <div className="navLink">
              <i className="fas fa-solid fa-bell"></i>
              <span>Notification</span>
            </div>
          </Link>
        </div>
        <hr />
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
        <div className="navbutton">
          <Link href="/user/recap">
            <div className="navLink">
              <i className="fas fa-solid fa-chart-simple"></i>
              <span>Recap</span>
            </div>
          </Link>
        </div>
        <hr />
        <div className="navbutton">
          <Link href="/follow">
            <div className="navLink">
              <i className="fas fa-heart"></i>
              <span>Follow</span>
            </div>
          </Link>
        </div>
        <div className="navbutton">
          <Link href="/user/search">
            <div className="navLink">
              <i className="fas fa-solid fa-magnifying-glass"></i>
              <span>Search</span>
            </div>
          </Link>
        </div>
        <hr />
        <div className="navbutton">
          <Link href="/membership">
            <div className="navLink">
              <i className="fas fa-solid fa-star"></i>
              <span>Membership</span>
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
            <Link id="nav-footer-title" href="/user/profile">
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
          <p>{userData?.userIntro || "자기소개를 작성해보세요!!"}</p>
        </div>
      </div>
    </div>
  );
}
