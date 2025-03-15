"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import axios from "axios";
import MemoInput from "./MemoInput";
import MusicList from "./MusicList";
import MusicSearch from "./MusicSearch";
import "./style.css";

export default function CalendarRecordPage() {
  const router = useRouter();
  const [memo, setMemo] = useState("");
  const [selectedTracks, setSelectedTracks] = useState([]);

  const handleSelectTrack = (track) => {
    setSelectedTracks((prev) => [...prev, track]);
  };

  const handleRemoveTrack = (trackId) => {
    setSelectedTracks((prev) => prev.filter((track) => track.id !== trackId));
  };

  const handleCreateRecord = async () => {
    try {
      const jwt = localStorage.getItem("accessToken");
      await axios.post(`/api/v1/calendar`, { memo, musicIds: selectedTracks.map(t => t.id) }, {
        headers: { Authorization: `Bearer ${jwt}`, "Content-Type": "application/json" }
      });

      alert("기록 추가 완료!");
      router.push("/calendar");
    } catch (error) {
      alert("기록 추가 중 오류 발생!");
    }
  };

  return (
    <div className="p-6">
      <div className="flex justify-between items-center mb-3">
        <h2 className="text-2xl font-bold">기록 추가</h2>
        <button onClick={handleCreateRecord} className="btn btn-primary">
          완료
        </button>
      </div>
      <div className="space-y-7">
        <MusicSearch onSelectTrack={handleSelectTrack} />
        <MusicList selectedTracks={selectedTracks} onRemoveTrack={handleRemoveTrack} />
        <MemoInput memo={memo} setMemo={setMemo} />
      </div>
    </div>
  );
}
