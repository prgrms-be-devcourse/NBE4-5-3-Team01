"use client";

import { useRouter } from "next/navigation";

export default function CalendarMainPage() {
  const router = useRouter();

  const handleAddRecordClick = () => {
    router.push("/calendar/record");
  };

  const handleEditRecord = () => {
    router.push("/calendar/record?id=1");
  };

  return (
    <div className="p-6">
      <h2 className="text-2xl font-bold">캘린더</h2>
      <button
        onClick={handleAddRecordClick}
        className="btn btn-primary mt-4"
      >
        기록 추가
      </button>
      <button
        onClick={handleEditRecord}
        className="btn btn-primary mt-4"
      >
        기존 기록 수정 (ID: 1)
      </button>
    </div>
  );
}