"use client";

import { useState, useEffect, useRef } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import axios from "axios";
import "./style.css";

import MemoInput from "./MemoInput";
import MusicList from "./MusicList";
import MusicSearch from "./MusicSearch";

import { Card } from "@/components/ui/card";
import { useGlobalAlert } from "@/components/GlobalAlert";

export default function CalendarRecordPage() {
  const API_URL = "http://localhost:8080/api/v1";
  const SPOTIFY_URL = "http://localhost:8080/api/v1/music/spotify";

  const router = useRouter();

  const searchParams = useSearchParams();
  const trackId = searchParams.get("trackId");

  const id = searchParams.get("id");
  const year = searchParams.get("year");
  const month = searchParams.get("month");
  const day = searchParams.get("day");

  const [memo, setMemo] = useState("");
  const [selectedTracks, setSelectedTracks] = useState<any[]>([]);

  const { setAlert } = useGlobalAlert();
  const [isEditing, setIsEditing] = useState(false);

  const isFetched = useRef(false);

  useEffect(() => {
    if (isFetched.current) return;
    isFetched.current = true;

    const fetchInitialData = async () => {
      const data = await fetchRecord(id);

      if (data.length !== 0) {
        setMemo(data.memo || "");
        await setSelectedTracks(data.musics || []);
      }

      if (trackId) {
        await fetchTrack(trackId, data.musics);
      }
    };

    fetchInitialData();
  }, [trackId, id]);


  const fetchTrack = async (trackId: string, musics: any) => {
    try {
      const res = await axios.get(`${SPOTIFY_URL}/${trackId}`, {
        headers: { "Content-Type": "application/json" },
        withCredentials: true,
      });

      const { code, msg, data } = res.data;
      setAlert({ code: code, message: msg });

      if (code.startsWith("2")) {
        // ✅ 중복 체크 먼저 실행
        const isDuplicate = musics.some(track => track.id === data.id);
        if (isDuplicate) {
          setAlert({
            code: "400-2",
            message: "이미 추가된 음악입니다.",
          });
          return;
        }

        // ✅ 20개 초과 여부도 밖에서 확인
        if (musics.length >= 20) {
          setAlert({
            code: "400-1",
            message: "음악은 최대 20개까지만 추가할 수 있습니다.",
          });
          return;
        }

        // ✅ 문제 없을 경우만 추가
        setSelectedTracks(prev => [...prev, data]);
      }
    } catch (error) {
      setAlert({
        code: "500-1",
        message: "음악 정보를 가져오는 데 실패했습니다."
      });
      throw error;
    }
  };

  const fetchRecord = async (id: any) => {
    try {
      if (id) {
        setIsEditing(true);
        const res = await axios.get(`${API_URL}/calendar/${id}`, {
          headers: {
            "Content-Type": "application/json",
          },
          withCredentials: true,
        });

        // const { code, msg, data } = res.data;
        // console.log(res.data);
        // setAlert({ code: code, message: msg });
        const code = "200";
        const data = res.data;

        if (code.startsWith("2")) {
          return data;
        }
      }
    } catch (error) {
      setAlert({
        code: "500-2",
        message: "음악 기록을 불러오는 데 실패했습니다."
      });
      throw error;
    }
  };

  // 📌 기록 저장 (신규 or 수정)
  const handleSaveRecord = async () => {
    try {
      // 📌 음악이 하나도 선택되지 않았다면 알림 표시
      if (selectedTracks.length === 0) {
        setAlert({
          code: "400-3",
          message: "음악 기록을 추가해주세요.",
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

      const finalMemo = memo.trim();

      const saveRes = await axios.post(`${API_URL}/music/save-all`, selectedTracks, {
        headers: {
          "Content-Type": "application/json",
        },
        withCredentials: true,
      });

      const { code, msg } = saveRes.data;
      setAlert({ code: code, message: msg });

      const musicIds = selectedTracks.map((track) => track.id);

      if (isEditing) {
        // 기존 기록 수정
        const musicRes = await axios.put(
          `${API_URL}/calendar/${id}/music`,
          { musicIds: musicIds },
          {
            headers: {
              "Content-Type": "application/json",
            },
            withCredentials: true,
          }
        );
        setAlert({ code: musicRes.data.code, message: musicRes.data.msg });

        const memoRes = await axios.patch(
          `${API_URL}/calendar/${id}/memo`,
          { memo: finalMemo },
          {
            headers: {
              "Content-Type": "application/json",
            },
            withCredentials: true,
          }
        );
        setAlert({ code: memoRes.data.code, message: memoRes.data.msg });

        alert("기록이 성공적으로 수정되었습니다!");
        router.push("/calendar");
      } else {
        // 새 기록 추가
        const res = await axios.post(
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

        const { code, msg } = res.data;
        setAlert({ code: code, message: msg });

        alert("새로운 기록이 추가되었습니다!");
        router.push("/calendar");
      }
    } catch (error) {
      setAlert({
        code: "500-3",
        message: "음악 기록을 저장하는 중 오류가 발생생했습니다."
      });
      throw error;
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
        <div className="flex justify-between items-center mb-3">
          <h2 className="text-2xl font-bold">
            {isEditing ? "기록 수정" : "기록 추가"}
          </h2>
          <button onClick={handleSaveRecord} className="btn btn-primary">
            완료
          </button>
        </div>
        <div className="space-y-7">
          <MusicSearch
            onSelectTrack={handleSelectTrack}
            selectedTracks={selectedTracks}
          />
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
