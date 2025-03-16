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
    
    // 명시적으로 토큰 존재 여부 확인
    if (!data.accessToken) {
      console.error("Server response missing access token");
      return null;
    }

    return {
      accessToken: data.accessToken,
      spotifyAccessToken: data.spotifyAccessToken
    };
  } catch (error) {
    console.error("Error refreshing token:", error);
    return null;
  }
};

// 액세스 토큰 만료 여부를 확인하는 함수
const isAccessTokenExpired = (accessToken: string | undefined | null): boolean => {
  if (!accessToken) {
    console.log("Access token is null or undefined");
    return true;
  }
  
  try {
    const payload = JSON.parse(atob(accessToken.split(".")[1]));
    const expiration = payload.exp * 1000;
    return Date.now() >= expiration;
  } catch (error) {
    console.error("Error decoding token:", error);
    return true;
  }
};

// 스포티파이 토큰 만료 여부를 확인하는 함수
const isSpotifyTokenExpired = (spotifyToken: string | undefined | null): boolean => {
  if (!spotifyToken) {
    console.log("Spotify token is null or undefined");
    return true;
  }
  
  try {
    const payload = JSON.parse(atob(spotifyToken.split(".")[1]));
    const expiration = payload.exp * 1000;
    return Date.now() >= expiration;
  } catch (error) {
    console.error("Error decoding Spotify token:", error);
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
  const spotifyToken = req.cookies.get("spotifyAccessToken")?.value;

  console.log("Token check:", {
    access: accessToken ? "exists" : "none",
    refresh: refreshToken ? "exists" : "none",
    spotify: spotifyToken ? "exists" : "none",
  });

  // 토큰이 없으면 로그인 페이지로 리디렉션
  if (!accessToken || !refreshToken) {
    console.log("Tokens not found, redirecting to /login");
    return NextResponse.redirect(new URL("/login", req.url));
  }

  // 액세스 토큰이나 스포티파이 토큰이 만료되었으면 리프레시
  if (isAccessTokenExpired(accessToken) || isSpotifyTokenExpired(spotifyToken)) {
    console.log("Access token or Spotify token expired, attempting refresh");
    try {
      const tokens = await refreshAccessToken(refreshToken);
      if (!tokens) {
        console.log("Token refresh failed, redirecting to /login");
        return NextResponse.redirect(new URL("/login", req.url));
      }
      console.log("Tokens refreshed successfully:", tokens);

      const response = NextResponse.next();
      response.cookies.set("accessToken", tokens.accessToken, {
        path: "/",
        maxAge: 3600,
        httpOnly: true,
        secure: true,
        sameSite: "strict"
      });
      
      if (tokens.spotifyAccessToken) {
        response.cookies.set("spotifyAccessToken", tokens.spotifyAccessToken, {
          path: "/",
          maxAge: 3600,
          httpOnly: true,
          secure: true,
          sameSite: "strict"
        });
      } else {
        console.log("No Spotify token received in refresh response");
      }
      
      response.headers.set("Authorization", `Bearer ${tokens.accessToken}`);
      return response;
    } catch (error) {
      console.error("Error refreshing token:", error);
      return NextResponse.redirect(new URL("/login", req.url));
    }
  }

  // 토큰이 유효한 경우
  const response = NextResponse.next();
  response.headers.set("Authorization", `Bearer ${accessToken}`);
  // 스포티파이 토큰이 있으면 헤더에 추가
  if (spotifyToken) {
    response.headers.set("Spotify-Authorization", `Bearer ${spotifyToken}`);
  }
  return response;
}

export const config = {
  matcher: [
    // api, _next, favicon.ico 등은 제외
    "/((?!api|_next/static|_next/image|favicon.ico).*)",
  ],
};