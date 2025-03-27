"use client";

import { useState, useEffect } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import axios from "axios";
import MemoInput from "./MemoInput";
import MusicList from "./MusicList";
import MusicSearch from "./MusicSearch";
import "./style.css";
import { AlertComponent } from "@/components/alert";
import { Card } from "@/components/ui/card";

export default function CalendarRecordPage() {
  const API_URL = "http://localhost:8080/api/v1";

  const router = useRouter();
  const searchParams = useSearchParams();

  const id = searchParams.get("id");
  const year = searchParams.get("year");
  const month = searchParams.get("month");
  const day = searchParams.get("day");

  const [memo, setMemo] = useState("");
  const [selectedTracks, setSelectedTracks] = useState<any[]>([]);
  const [isEditing, setIsEditing] = useState(false);

  const [alertData, setAlertData] = useState<{
    title: string;
    description: string;
    variant: "default" | "success" | "warning" | "destructive"
  } | null>(null);

  // 📌 기존 기록 불러오기 (id가 존재할 경우)
  useEffect(() => {
    fetchRecord();
  }, [id]);

  const fetchRecord = async () => {
    try {
      if (id) {
        setIsEditing(true);
        const res = await axios.get(`${API_URL}/calendar/${id}`, {
          headers: {
            "Content-Type": "application/json",
          },
          withCredentials: true,
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
      // 📌 음악이 하나도 선택되지 않았다면 알림 표시
      if (selectedTracks.length === 0) {
        setAlertData({
          title: "기록 저장",
          description: "음악 기록을 추가해주세요",
          variant: "warning",
        });
        return;
      }

      // 📌 메모가 비어있다면 확인 요청
      if (!memo.trim()) {
        const confirmSave = window.confirm(
          "메모를 작성하지 않으셨습니다. 그대로 저장하시겠습니까?"
        );
        if (!confirmSave) return;
      }

      let finalMemo = memo.trim() ? memo : null;

      await axios.post(`${API_URL}/music/save-all`, selectedTracks, {
        headers: {
          "Content-Type": "application/json",
        },
        withCredentials: true,
      });

      const musicIds = selectedTracks.map((track) => track.id);

      if (isEditing) {
        // 기존 기록 수정
        await axios.put(
          `${API_URL}/calendar/${id}/music`,
          { musicIds: musicIds },
          {
            headers: {
              "Content-Type": "application/json",
            },
            withCredentials: true,
          }
        );

        await axios.patch(
          `${API_URL}/calendar/${id}/memo`,
          { memo: finalMemo },
          {
            headers: {
              "Content-Type": "application/json",
            },
            withCredentials: true,
          }
        );

        alert("기록이 성공적으로 수정되었습니다!");
        router.push("/calendar");
      } else {
        // 새 기록 추가
        await axios.post(
          `${API_URL}/calendar`,
          { memo: finalMemo, musicIds },
          {
            params: { year, month, day },
            headers: {
              "Content-Type": "application/json",
            },
            withCredentials: true,
          }
        );

        alert("새로운 기록이 추가되었습니다!");
        router.push("/calendar");
      }
    } catch (error) {
      console.error("기록 저장 실패:", error);
      setAlertData({
        title: "기록 저장",
        description: "기록 저장 중 오류 발생",
        variant: "destructive",
      });
    }
  };

  const handleSelectTrack = (track: any) => {
    setSelectedTracks((prev) => [...prev, track]);
  };

  const handleRemoveTrack = (trackId: any) => {
    setSelectedTracks((prev) => prev.filter((track) => track.id !== trackId));
  };

  return (
    <Card className="m-10 bg-white border-0 p-0">
      <div className="p-6">
        {alertData && (
          <AlertComponent
            title={alertData.title}
            description={alertData.description}
            variant={alertData.variant}
          />
        )}
        <div className="flex justify-between items-center mb-3">
          <h2 className="text-2xl font-bold">
            {isEditing ? "기록 수정" : "기록 추가"}
          </h2>
          <button onClick={handleSaveRecord} className="btn btn-primary">
            완료
          </button>
        </div>
        <div className="space-y-7">
          <MusicSearch onSelectTrack={handleSelectTrack} />
          <MusicList
            selectedTracks={selectedTracks}
            onRemoveTrack={handleRemoveTrack}
          />
          <MemoInput memo={memo} setMemo={setMemo} />
        </div>
      </div>
    </Card>
  );
}
