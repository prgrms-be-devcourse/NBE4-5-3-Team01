"use client";

import { useState } from "react";
import Link from "next/link";
import Image from "next/image";

export default function Sidebar() {
  const [isHovered, setIsHovered] = useState(false);
  return (
    <div id="nav-bar">
      <input id="nav-toggle" type="checkbox" />
      <div id="nav-header">
        <Link id="nav-title" href="/">
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
          <Link href="/chart">
            <div className="navLink">
              <i className="fas fa-solid fa-chart-simple"></i>
              <span>Chart</span>
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
              Nickname
            </Link>
            <span id="nav-footer-subtitle">name</span>
          </div>
          <label htmlFor="nav-footer-toggle">
            <i className="fas fa-caret-up"></i>
          </label>
        </div>
        <div id="nav-footer-content">
          <p>
            자기소개를 작성하면 나오는 곳 입니다. 아무거나 적으면 됩니다. 자신을
            소개해보세요. 자신을 한 문장으로 알려주세요.
          </p>
        </div>
      </div>
    </div>
  );
}
