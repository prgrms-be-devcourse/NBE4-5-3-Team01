import { NextResponse } from "next/server";
import type { NextRequest } from "next/server";

// 리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급받는 함수
const refreshAccessToken = async (refreshToken: string) => {
  try {
    console.log("Attempting to refresh token with:", refreshToken);
    const response = await fetch("http://localhost:8080/api/v1/user/refresh", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${refreshToken}`
      },
      body: JSON.stringify({ refreshToken })
    });

    if (!response.ok) {
      const errorData = await response.text();
      console.error("Refresh token response error:", errorData);
      throw new Error("Failed to refresh access token");
    }

    const data = await response.json();
    console.log("Refresh token response data:", data);
    return data.accessToken; // 새로운 액세스 토큰 반환
  } catch (error) {
    console.error("Error refreshing token:", error);
    return null;
  }
};

// 액세스 토큰 만료 여부를 확인하는 함수
const isAccessTokenExpired = (accessToken: string): boolean => {
  try {
    const payload = JSON.parse(atob(accessToken.split(".")[1])); // JWT payload 디코딩
    const expiration = payload.exp * 1000; // 만료 시간 (초 -> 밀리초 변환)
    return Date.now() >= expiration;
  } catch (error) {
    console.error("Error decoding token:", error);
    return true;
  }
};

// 보호되지 않은(public) 경로인지 확인하는 함수
const isPublicRoute = (pathname: string): boolean => {
  const publicPaths = [
    "/login",
    "/callback",
    "/api",
    "/_next",
    "/static",
    "/favicon.ico"
  ];
  return publicPaths.some((path) => pathname.startsWith(path));
};

export default async function middleware(req: NextRequest) {
  const { pathname } = req.nextUrl;
  console.log("Middleware handling path:", pathname);

  // public 경로는 미들웨어 건너뛰기
  if (isPublicRoute(pathname)) {
    console.log("Public route, passing through");
    return NextResponse.next();
  }

  // 쿠키에서 액세스 토큰과 리프레시 토큰 가져오기
  const accessToken = req.cookies.get("accessToken")?.value;
  const refreshToken = req.cookies.get("refreshToken")?.value;

  console.log("Token check:", {
    access: accessToken ? "exists" : "none",
    refresh: refreshToken ? "exists" : "none",
  });

  // 토큰이 없으면 로그인 페이지로 리디렉션
  if (!accessToken || !refreshToken) {
    console.log("Tokens not found, redirecting to /login");
    return NextResponse.redirect(new URL("/login", req.url));
  }

  // 액세스 토큰이 만료되었으면 리프레시 토큰으로 갱신
  if (isAccessTokenExpired(accessToken)) {
    console.log("Access token expired, attempting refresh");
    try {
      const newAccessToken = await refreshAccessToken(refreshToken);
      if (!newAccessToken) {
        console.log("Token refresh failed, redirecting to /login");
        return NextResponse.redirect(new URL("/login", req.url));
      }
      console.log("Token refreshed successfully:", newAccessToken);

      // 새 액세스 토큰을 쿠키에 저장하고, Authorization 헤더 설정
      const response = NextResponse.next();
      response.cookies.set("accessToken", newAccessToken, {
        path: "/",
        maxAge: 3600, // 1시간
        httpOnly: true,
        secure: true,
        sameSite: "strict"
      });
      response.headers.set("Authorization", `Bearer ${newAccessToken}`);
      return response;
    } catch (error) {
      console.error("Error refreshing token:", error);
      return NextResponse.redirect(new URL("/login", req.url));
    }
  }

  // 액세스 토큰이 유효하면 Authorization 헤더 설정
  const response = NextResponse.next();
  response.headers.set("Authorization", `Bearer ${accessToken}`);
  return response;
}

export const config = {
  matcher: [
    // api, _next, favicon.ico 등은 제외
    "/((?!api|_next/static|_next/image|favicon.ico).*)",
  ],
};