"use client";

import "@/components/style/global.css";
import { useState } from "react";
import axios from "axios";
import styles from "@/components/style/notificationSettingConfigure.module.css";
import Checkbox from "./checkbox";
import Link from "next/link";

const NotificationCreate = () => {
  return (
    <div className="encore-dark-theme">
      <div className={styles.page}>
        <div className={styles.container}>
          <div className={styles.btn}>
            <Link href="/notifications/settings">
              <button className={styles.btn1}>
                <span className={styles.baseline}>
                  <span aria-hidden="true" className={styles.btn_icon}>
                    <svg
                      data-encore-id="icon"
                      role="img"
                      aria-label="Back"
                      aria-hidden="false"
                      className={styles.icon}
                      viewBox="0 0 24 24"
                    >
                      <path d="M15.957 2.793a1 1 0 0 1 0 1.414L8.164 12l7.793 7.793a1 1 0 1 1-1.414 1.414L5.336 12l9.207-9.207a1 1 0 0 1 1.414 0z"></path>
                    </svg>
                  </span>
                </span>
              </button>
            </Link>
          </div>
          <div className={styles.wrapper}>
            <h1
              className={styles.header}
              style={{
                fontSize: "3rem",
                fontWeight: "700",
              }}
            >
              알림 설정
            </h1>
            <span className={styles.s1}>
              푸시 또는 이메일로 받고 싶은 알림을 선택하세요. 이러한 기본 설정은
              푸시 및 이메일에만 적용됩니다.
            </span>
            <div className={styles.menu}>
              <div className={styles.d3}>
                <div className={styles.d31}>
                  <svg
                    data-encore-id="icon"
                    role="img"
                    aria-hidden="true"
                    className={styles.icon}
                    viewBox="0 0 24 24"
                  >
                    <path d="M1 3h22v18H1V3zm2 2v1.711l9 5.197 9-5.197V5H3zm18 4.02-9 5.197L3 9.02V19h18V9.02z"></path>
                  </svg>
                  <p>이메일</p>
                </div>
                <div className={styles.d32}>
                  <svg
                    data-encore-id="icon"
                    role="img"
                    aria-hidden="true"
                    className={styles.icon}
                    viewBox="0 0 24 24"
                  >
                    <path d="M5 5a3 3 0 0 1 3-3h8a3 3 0 0 1 3 3v14a3 3 0 0 1-3 3H8a3 3 0 0 1-3-3V5zm3-1a1 1 0 0 0-1 1v14a1 1 0 0 0 1 1h8a1 1 0 0 0 1-1V5a1 1 0 0 0-1-1H8z"></path>
                    <path d="M13.25 16.75a1.25 1.25 0 1 1-2.5 0 1.25 1.25 0 0 1 2.5 0z"></path>
                  </svg>
                  <p>푸시</p>
                </div>
              </div>
              <div className={styles.d4}>
                <div className={styles.d5}>
                  <div className={styles.d51}>
                    <span style={{ fontWeight: "700", fontSize: "20px" }}>
                      음악 기록
                    </span>
                  </div>
                  <div className={styles.d52}>
                    <span style={{ fontWeight: "700", color: "#b3b3b3" }}>
                      매일 정해진 시간에 음악을 기록하라고 알려주는 알림
                    </span>
                  </div>
                  <div className={styles.d53}>
                    <div className={styles.d6}>
                      <Checkbox />
                    </div>
                  </div>
                  <div className={styles.d54}>
                    <div className={styles.d6}>
                      <Checkbox />
                    </div>
                  </div>
                </div>
              </div>
              <div className={styles.d4}>
                <div className={styles.d5}>
                  <div className={styles.d51}>
                    <span style={{ fontWeight: "700", fontSize: "20px" }}>
                      팔로워
                    </span>
                  </div>
                  <div className={styles.d52}>
                    <span style={{ fontWeight: "700", color: "#b3b3b3" }}>
                      다른 이가 나를 팔로우 했을 때 관련 알림
                    </span>
                  </div>
                  <div className={styles.d53}>
                    <div className={styles.d6}>
                      <Checkbox />
                    </div>
                  </div>
                  <div className={styles.d54}>
                    <div className={styles.d6}>
                      <Checkbox />
                    </div>
                  </div>
                </div>
              </div>
              <div className={styles.d4}>
                <div className={styles.d5}>
                  <div className={styles.d51}>
                    <span style={{ fontWeight: "700", fontSize: "20px" }}>
                      음악 추천
                    </span>
                  </div>
                  <div className={styles.d52}>
                    <span style={{ fontWeight: "700", color: "#b3b3b3" }}>
                      오늘 내가 들은 많이 들은 음악 추천
                    </span>
                  </div>
                  <div className={styles.d53}>
                    <div className={styles.d6}>
                      <Checkbox />
                    </div>
                  </div>
                  <div className={styles.d54}>
                    <div className={styles.d6}>
                      <Checkbox />
                    </div>
                  </div>
                </div>
              </div>
              <div className={styles.d4}>
                <div className={styles.d5}>
                  <div className={styles.d51}>
                    <span style={{ fontWeight: "700", fontSize: "20px" }}>
                      설문 조사
                    </span>
                  </div>
                  <div className={styles.d52}>
                    <span style={{ fontWeight: "700", color: "#b3b3b3" }}>
                      플랫폼 개선에 도움이 되는 피드백 설문조사
                    </span>
                  </div>
                  <div className={styles.d53}>
                    <div className={styles.d6}>
                      <Checkbox />
                    </div>
                  </div>
                  <div className={styles.d54}>
                    <div className={styles.d6}>
                      <Checkbox />
                    </div>
                  </div>
                </div>
              </div>
              <div className={styles.d7}>
                <Link href="/notifications/settings" className={styles.cancel}>
                  취소하기
                </Link>
                <button className={styles.saved}>
                  <span className={styles.s2}>저장하기</span>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default NotificationCreate;
