"use client";

import "@/components/style/global.css";
import styles from "@/components/style/notificationSetting.module.css";
import Link from "next/link";
import Image from "next/image";

const NotificationSettings = () => {
  return (
    <div className="encore-dark-theme">
      <div className={styles.page}>
        <div className={styles.container}>
          <div className={styles.content}>
            <div className={styles.wrapper}>
              <div className={styles.btn1}>
                <Link href="/notifications">
                  <button className={styles.btn2}>
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
              <h2
                className={styles.header}
                style={{
                  fontSize: "2rem",
                  fontWeight: "700",
                  color: "rgb(255, 210, 215)",
                }}
              >
                Setting
                <Image
                  src="/setting.svg"
                  alt="설정 아이콘"
                  width={30}
                  height={30}
                  style={{
                    filter: "invert(1)",
                    marginLeft: "10px",
                    paddingTop: "4px",
                  }}
                />
              </h2>
              <div className={styles.menu}>
                <h1
                  style={{
                    paddingBlockEnd: "16px",
                    fontSize: "1.5rem",
                    fontWeight: "700",
                    padding: "16px",
                  }}
                >
                  알림
                </h1>
                <div>
                  <Link
                    href="/notifications/settings/create"
                    className={`${styles.linkb} ${styles.kaxeyf}`}
                  >
                    <div className={styles.kMKjYE}></div>
                    <div className={styles.jnpRBv}>
                      <div className={styles.hWEpdM}>
                        <div className={styles.hThHw}>
                          <div className={styles.eRugSY}>
                            <svg
                              data-encore-id="icon"
                              role="img"
                              aria-hidden="true"
                              className={styles.icon}
                              viewBox="0 0 16 16"
                            >
                              <path d="M 11.838 0.714 a 2.438 2.438 0 0 1 3.448 3.448 l -9.841 9.841 c -0.358 0.358 -0.79 0.633 -1.267 0.806 l -3.173 1.146 a 0.75 0.75 0 0 1 -0.96 -0.96 l 1.146 -3.173 c 0.173 -0.476 0.448 -0.909 0.806 -1.267 l 9.84 -9.84 Z m 2.387 1.06 a 0.938 0.938 0 0 0 -1.327 0 l -9.84 9.842 a 1.953 1.953 0 0 0 -0.456 0.716 L 2 14.002 l 1.669 -0.604 a 1.95 1.95 0 0 0 0.716 -0.455 l 9.841 -9.841 a 0.938 0.938 0 0 0 0 -1.327 Z" />
                            </svg>
                          </div>
                        </div>
                      </div>
                    </div>
                    <div className={styles.jJychV}>
                      <div className={styles.lbPUPJ}>
                        <p
                          style={{
                            fontSize: "1rem",
                            fontWeight: "400",
                          }}
                        >
                          알림 등록
                        </p>
                      </div>
                      <div className={styles.cmcLAf}>
                        <svg
                          data-encore-id="icon"
                          role="img"
                          aria-hidden="true"
                          className={styles.icon}
                          viewBox="0 0 16 16"
                        >
                          <path d="M4.97.47a.75.75 0 0 0 0 1.06L11.44 8l-6.47 6.47a.75.75 0 1 0 1.06 1.06L13.56 8 6.03.47a.75.75 0 0 0-1.06 0z" />
                        </svg>
                      </div>
                    </div>
                  </Link>
                </div>
                <div>
                  <Link
                    href="/notifications/settings/edit"
                    className={`${styles.linkb} ${styles.kaxeyf}`}
                  >
                    <div className={styles.kMKjYE}></div>
                    <div className={styles.jnpRBv}>
                      <div className={styles.hWEpdM}>
                        <div className={styles.hThHw}>
                          <div className={styles.eRugSY}>
                            <svg
                              data-encore-id="icon"
                              role="img"
                              aria-hidden="true"
                              className={styles.icon}
                              viewBox="0 0 16 16"
                            >
                              <path d="M9.937 2.853a5.5 5.5 0 1 0 3.376 6.57.75.75 0 0 1 1.448.389A7 7 0 1 1 13.5 3.67V2A.75.75 0 0 1 15 2v4h-4a.75.75 0 0 1 0-1.5h1.243a5.5 5.5 0 0 0-2.306-1.647Z" />
                            </svg>
                          </div>
                        </div>
                      </div>
                    </div>
                    <div className={styles.jJychV}>
                      <div className={styles.lbPUPJ}>
                        <p
                          style={{
                            fontSize: "1rem",
                            fontWeight: "400",
                          }}
                        >
                          알림 수정
                        </p>
                      </div>
                      <div className={styles.cmcLAf}>
                        <svg
                          data-encore-id="icon"
                          role="img"
                          aria-hidden="true"
                          className={styles.icon}
                          viewBox="0 0 16 16"
                        >
                          <path d="M4.97.47a.75.75 0 0 0 0 1.06L11.44 8l-6.47 6.47a.75.75 0 1 0 1.06 1.06L13.56 8 6.03.47a.75.75 0 0 0-1.06 0z" />
                        </svg>
                      </div>
                    </div>
                  </Link>
                </div>
                <div>
                  <Link
                    href="/notifications/settings/configure"
                    className={`${styles.linkb} ${styles.kaxeyf}`}
                  >
                    <div className={styles.kMKjYE}></div>
                    <div className={styles.jnpRBv}>
                      <div className={styles.hWEpdM}>
                        <div className={styles.hThHw}>
                          <div className={styles.eRugSY}>
                            <svg
                              data-encore-id="icon"
                              role="img"
                              aria-hidden="true"
                              className={styles.icon}
                              viewBox="0 0 16 16"
                            >
                              <path d="M8 1.5a4 4 0 0 0-4 4v3.27a.75.75 0 0 1-.1.373L2.255 12h11.49L12.1 9.142a.75.75 0 0 1-.1-.374V5.5a4 4 0 0 0-4-4zm-5.5 4a5.5 5.5 0 0 1 11 0v3.067l2.193 3.809a.75.75 0 0 1-.65 1.124H10.5a2.5 2.5 0 0 1-5 0H.957a.75.75 0 0 1-.65-1.124L2.5 8.569V5.5zm4.5 8a1 1 0 1 0 2 0H7z" />
                            </svg>
                          </div>
                        </div>
                      </div>
                    </div>
                    <div className={styles.jJychV}>
                      <div className={styles.lbPUPJ}>
                        <p
                          style={{
                            fontSize: "1rem",
                            fontWeight: "400",
                          }}
                        >
                          알림 설정
                        </p>
                      </div>
                      <div className={styles.cmcLAf}>
                        <svg
                          data-encore-id="icon"
                          role="img"
                          aria-hidden="true"
                          className={styles.icon}
                          viewBox="0 0 16 16"
                        >
                          <path d="M4.97.47a.75.75 0 0 0 0 1.06L11.44 8l-6.47 6.47a.75.75 0 1 0 1.06 1.06L13.56 8 6.03.47a.75.75 0 0 0-1.06 0z" />
                        </svg>
                      </div>
                    </div>
                  </Link>
                </div>
              </div>
              <div className={styles.menu}>
                <h1
                  style={{
                    paddingBlockEnd: "16px",
                    fontSize: "1.5rem",
                    fontWeight: "700",
                    padding: "16px",
                  }}
                >
                  보안 및 개인정보 보호
                </h1>
                <div>
                  <Link
                    href="/notifications/settings/create"
                    className={`${styles.linkb} ${styles.kaxeyf}`}
                  >
                    <div className={styles.kMKjYE}></div>
                    <div className={styles.jnpRBv}>
                      <div className={styles.hWEpdM}>
                        <div className={styles.hThHw}>
                          <div className={styles.eRugSY}>
                            <svg
                              data-encore-id="icon"
                              role="img"
                              aria-hidden="true"
                              className={styles.icon}
                              viewBox="0 0 16 16"
                            >
                              <path d="M1 1h6v6H1V1zm1.5 1.5v3h3v-3h-3zM1 9h6v6H1V9zm1.5 1.5v3h3v-3h-3zM9 1h6v6H9V1zm1.5 1.5v3h3v-3h-3zM9 9h6v6H9V9zm1.5 1.5v3h3v-3h-3z" />
                            </svg>
                          </div>
                        </div>
                      </div>
                    </div>
                    <div className={styles.jJychV}>
                      <div className={styles.lbPUPJ}>
                        <p
                          style={{
                            fontSize: "1rem",
                            fontWeight: "400",
                          }}
                        >
                          앱 관리
                        </p>
                      </div>
                      <div className={styles.cmcLAf}>
                        <svg
                          data-encore-id="icon"
                          role="img"
                          aria-hidden="true"
                          className={styles.icon}
                          viewBox="0 0 16 16"
                        >
                          <path d="M4.97.47a.75.75 0 0 0 0 1.06L11.44 8l-6.47 6.47a.75.75 0 1 0 1.06 1.06L13.56 8 6.03.47a.75.75 0 0 0-1.06 0z" />
                        </svg>
                      </div>
                    </div>
                  </Link>
                </div>
                <div>
                  <Link
                    href="/notifications/settings/create"
                    className={`${styles.linkb} ${styles.kaxeyf}`}
                  >
                    <div className={styles.kMKjYE}></div>
                    <div className={styles.jnpRBv}>
                      <div className={styles.hWEpdM}>
                        <div className={styles.hThHw}>
                          <div className={styles.eRugSY}>
                            <svg
                              data-encore-id="icon"
                              role="img"
                              aria-hidden="true"
                              className={styles.icon}
                              viewBox="0 0 16 16"
                            >
                              <path d="M4.284 5.21A4.055 4.055 0 0 0 4 6.706c0 2.222 1.782 4.04 4 4.04s4-1.818 4-4.04c0-.529-.1-1.034-.284-1.498.291.189.57.402.84.639.436.38 1.143 1.143 1.739 2.175-.596 1.031-1.303 1.793-1.738 2.175-1.244 1.09-2.692 1.68-4.557 1.68-1.865 0-3.313-.59-4.557-1.68-.435-.382-1.142-1.144-1.738-2.175.596-1.032 1.303-1.794 1.738-2.175.27-.237.55-.45.841-.639zM8 2.666c-2.233 0-4.03.725-5.546 2.053-.603.53-1.544 1.564-2.27 2.957l-.18.347.18.346c.726 1.393 1.667 2.428 2.27 2.957C3.97 12.653 5.767 13.378 8 13.378s4.03-.725 5.546-2.052c.603-.53 1.544-1.564 2.27-2.957l.18-.346-.18-.347c-.726-1.393-1.667-2.427-2.27-2.956C12.03 3.392 10.233 2.667 8 2.667zm0 1.5c1.372 0 2.5 1.128 2.5 2.54 0 1.411-1.128 2.54-2.5 2.54s-2.5-1.129-2.5-2.54c0-1.412 1.128-2.54 2.5-2.54z" />
                            </svg>
                          </div>
                        </div>
                      </div>
                    </div>
                    <div className={styles.jJychV}>
                      <div className={styles.lbPUPJ}>
                        <p
                          style={{
                            fontSize: "1rem",
                            fontWeight: "400",
                          }}
                        >
                          계정 개인정보
                        </p>
                      </div>
                      <div className={styles.cmcLAf}>
                        <svg
                          data-encore-id="icon"
                          role="img"
                          aria-hidden="true"
                          className={styles.icon}
                          viewBox="0 0 16 16"
                        >
                          <path d="M4.97.47a.75.75 0 0 0 0 1.06L11.44 8l-6.47 6.47a.75.75 0 1 0 1.06 1.06L13.56 8 6.03.47a.75.75 0 0 0-1.06 0z" />
                        </svg>
                      </div>
                    </div>
                  </Link>
                </div>
              </div>
              <div className={styles.menu}>
                <h1
                  style={{
                    paddingBlockEnd: "16px",
                    fontSize: "1.5rem",
                    fontWeight: "700",
                    padding: "16px",
                  }}
                >
                  도움말
                </h1>
                <div>
                  <Link
                    href="/notifications/settings/create"
                    className={`${styles.linkb} ${styles.kaxeyf}`}
                  >
                    <div className={styles.kMKjYE}></div>
                    <div className={styles.jnpRBv}>
                      <div className={styles.hWEpdM}>
                        <div className={styles.hThHw}>
                          <div className={styles.eRugSY}>
                            <svg
                              data-encore-id="icon"
                              role="img"
                              aria-hidden="true"
                              className={styles.icon}
                              viewBox="0 0 16 16"
                            >
                              <path d="M8 1.5a6.5 6.5 0 1 0 0 13 6.5 6.5 0 0 0 0-13zM0 8a8 8 0 1 1 16 0A8 8 0 0 1 0 8z" />
                              <path d="M7.25 12.026v-1.5h1.5v1.5h-1.5zm.884-7.096A1.125 1.125 0 0 0 7.06 6.39l-1.431.448a2.625 2.625 0 1 1 5.13-.784c0 .54-.156 1.015-.503 1.488-.3.408-.7.652-.973.818l-.112.068c-.185.116-.26.203-.302.283-.046.087-.097.245-.097.57h-1.5c0-.47.072-.898.274-1.277.206-.385.507-.645.827-.846l.147-.092c.285-.177.413-.257.526-.41.169-.23.213-.397.213-.602 0-.622-.503-1.125-1.125-1.125z" />
                            </svg>
                          </div>
                        </div>
                      </div>
                    </div>
                    <div className={styles.jJychV}>
                      <div className={styles.lbPUPJ}>
                        <p
                          style={{
                            fontSize: "1rem",
                            fontWeight: "400",
                          }}
                        >
                          Music Calendar 지원
                        </p>
                      </div>
                      <div className={styles.cmcLAf}>
                        <svg
                          data-encore-id="icon"
                          role="img"
                          aria-hidden="true"
                          className={styles.icon}
                          viewBox="0 0 16 16"
                        >
                          <path d="M4.97.47a.75.75 0 0 0 0 1.06L11.44 8l-6.47 6.47a.75.75 0 1 0 1.06 1.06L13.56 8 6.03.47a.75.75 0 0 0-1.06 0z" />
                        </svg>
                      </div>
                    </div>
                  </Link>
                </div>
              </div>
              {/* <div className={styles.tabContent}>
          {activeTab === "create" ? (
            <NotificationCreate />
          ) : (
            <NotificationEdit />
          )}
        </div> */}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default NotificationSettings;
