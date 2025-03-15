"use client";

import { useRouter } from "next/navigation";

export default function CalendarMainPage() {
  const router = useRouter();

  const handleAddRecordClick = () => {
    router.push("/calendar/record");
  };

  return (
    <div className="p-6">
      <h2 className="text-2xl font-bold">캘린더</h2>
      <button
        onClick={handleAddRecordClick}
        className="bg-blue-500 text-white px-4 py-2 rounded-md mt-4 hover:bg-blue-600"
      >
        기록 추가
      </button>
    </div>
  );
}