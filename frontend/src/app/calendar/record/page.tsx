"use client";

import { useState, useEffect } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import axios from "axios";
import MemoInput from "./MemoInput";
import MusicList from "./MusicList";
import MusicSearch from "./MusicSearch";
import "./style.css";

export default function CalendarRecordPage() {
  const API_URL = "http://localhost:8080/api/v1";

  const router = useRouter();
  const searchParams = useSearchParams();
  const id = searchParams.get("id");

  const [memo, setMemo] = useState("");
  const [selectedTracks, setSelectedTracks] = useState([]);
  const [isEditing, setIsEditing] = useState(false);

  // 📌 기존 기록 불러오기 (id가 존재할 경우)
  useEffect(() => {
    fetchRecord();
  }, [id]);

  const fetchRecord = async () => {
    try {
      if (id) {
        setIsEditing(true);
        const jwt = localStorage.getItem("accessToken");
        const res = await axios.get(`${API_URL}/calendar/${id}`, {
          headers: {
            Authorization: `Bearer ${jwt}`,
            "Content-Type": "application/json"
          }
        });

        setMemo(res.data.memo || "");
        setSelectedTracks(res.data.musics || []);
      }
    } catch (error) {
      console.error("기록 불러오기 실패:", error);
      router.push("/calendar");
    }
  };

  // 📌 기록 저장 (신규 or 수정)
  const handleSaveRecord = async () => {
    try {
      const jwt = localStorage.getItem("accessToken");

      if (isEditing) {
        // 기존 기록 수정
        console.log(selectedTracks);
        const musicIds = selectedTracks.map(track => track.id);

        await axios.post(`${API_URL}/calendar/${id}/music`,
          { musicIds: musicIds },
          {
            headers: {
              Authorization: `Bearer ${jwt}`,
              "Content-Type": "application/json"
            }
          }
        );

        await axios.post(`${API_URL}/calendar/${id}/memo`,
          { memo: memo },
          {
            headers: {
              Authorization: `Bearer ${jwt}`,
              "Content-Type": "application/json"
            }
          }
        );

        alert("기록이 성공적으로 수정되었습니다!");
      } else {
        // 새 기록 추가
        const res = await axios.post(`${API_URL}/calendar/record`, {
          memo,
          musicList: selectedTracks,
        });
        alert("새로운 기록이 추가되었습니다!");
        router.push(`/calendar/record?id=${res.data.id}`); // ✅ 새 id로 URL 변경
      }
    } catch (error) {
      console.error("기록 저장 실패:", error);
      alert("기록 저장 중 오류 발생!");
    }
  };

  const handleSelectTrack = (track) => {
    setSelectedTracks((prev) => [...prev, track]);
  };

  const handleRemoveTrack = (trackId) => {
    setSelectedTracks((prev) => prev.filter((track) => track.id !== trackId));
  };

  return (
    <div className="p-6">
      <div className="flex justify-between items-center mb-3">
        <h2 className="text-2xl font-bold">{isEditing ? "기록 수정" : "기록 추가"}</h2>
        <button onClick={handleSaveRecord} className="btn btn-primary">
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
