"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";

export default function Home() {
  const router = useRouter();

  useEffect(() => {
    router.push("/calendar"); // /calendar 페이지로 이동
  }, [router]);

  return null; // 화면을 렌더링하지 않음
}
