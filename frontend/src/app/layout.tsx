import type { Metadata } from "next";
import "./globals.css";
import ClientWrapper from "@/components/ClientWrapper";
import ConditionalSidebar from "@/components/ConditionalSidebar";
// import Script from "next/script";

export const metadata: Metadata = {
  title: "Music Calendar",
  description: "Music Calendar using Spotify API",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="ko">
      <head>
        {/* <Script src="https://sdk.scdn.co/spotify-player.js"></Script> */}
        <link
          rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.7.2/css/all.min.css"
          integrity="sha512-dNr/..."
          crossOrigin="anonymous"
          referrerPolicy="no-referrer"
        />
        <link
          href="https://cdn.jsdelivr.net/gh/sun-typeface/SUIT@2/fonts/static/woff2/SUIT.css"
          rel="stylesheet"
        />
      </head>
      <body>
        <ClientWrapper>
          <ConditionalSidebar />
          {/* 사이드바 크기만큼 밀어줌 */}
          <main className="ml-64">{children}</main>
        </ClientWrapper>
      </body>
    </html>
  );
}
