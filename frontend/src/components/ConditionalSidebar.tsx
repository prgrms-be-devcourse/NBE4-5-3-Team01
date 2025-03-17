"use client";

import { usePathname } from "next/navigation";
import Sidebar from "@/components/Sidebar";

export default function ConditionalSidebar() {
  const pathname = usePathname();
  // 예를 들어, "/login" 페이지에서는 사이드바를 숨김
  if (pathname === "/login") {
    return null;
  }
  return <Sidebar />;
}
