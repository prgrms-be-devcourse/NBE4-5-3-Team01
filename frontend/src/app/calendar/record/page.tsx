"use client";

import { useState, useEffect, useRef } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import axios from "axios";
import "./style.css";

import MemoInput from "./MemoInput";
import MusicList from "./MusicList";
import MusicSearch from "./MusicSearch";
import RecentlyPlayedList from "./RecentlyPlayedList";

import { Card } from "@/components/ui/card";
import { useGlobalAlert } from "@/components/GlobalAlert";
import { getSpotifyAccessToken } from "@/app/utils/getSpotifyAccessToken";

export default function CalendarRecordPage() {
  const API_URL = "http://localhost:8080/api/v1";
  const SPOTIFY_URL = "http://localhost:8080/api/v1/music/spotify";

  const router = useRouter();

  const searchParams = useSearchParams();
  const trackId = searchParams.get("trackId");
  const playlistId = searchParams.get("playlistId");

  const id = searchParams.get("id");
  const year = searchParams.get("year");
  const month = searchParams.get("month");
  const day = searchParams.get("day");

  const [memo, setMemo] = useState("");
  const [selectedTracks, setSelectedTracks] = useState<any[]>([]);
  const [membershipGrade, setMembershipGrade] = useState("basic");

  const MAX_TRACK_COUNT = membershipGrade === "premium" ? 50 : 20;
  const MAX_MEMO_LENGTH = membershipGrade === "premium" ? 500 : 200;

  const { setAlert } = useGlobalAlert();
  const [isEditing, setIsEditing] = useState(false);

  const isFetched = useRef(false);

  const [recentTracks, setRecentTracks] = useState<any[]>([]);
  const [isFetchingRecent, setIsFetchingRecent] = useState(false);

  const [isSaving, setIsSaving] = useState(false);

  useEffect(() => {
    if (isFetched.current) return;
    isFetched.current = true;

    const fetchUser = async () => {
      try {
        const membershipRes = await axios.get(`${API_URL}/membership/my`, {
          withCredentials: true,
        });
        const membership = membershipRes.data.data;
        setMembershipGrade(membership?.grade || "basic");
      } catch (error) {
        setAlert({
          code: "500-2",
          message: "사용자 정보를 가져오지 못했어요.",
        });
      }
    };

    const fetchInitialData = async () => {
      const data = await fetchRecord(id);

      if (data !== undefined) {
        setMemo(data.memo || "");
        await setSelectedTracks(data.musics || []);
      }

      if (trackId) {
        await fetchTrack(trackId, data?.musics || []);
      }

      if (playlistId) {
        if (id === null) {
          await fetchTracksFromPlaylist(playlistId);
        } else {
          setAlert({
            code: "400-5",
            message: "오늘은 이미 음악이 기록되어 있어 전체 추가는 불가능해요.",
          });
        }
      }
    };

    fetchInitialData();
    fetchUser();
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
        const isDuplicate = musics.some(
          (track: { id: any }) => track.id === data.id
        );
        if (isDuplicate) {
          setAlert({
            code: "400-2",
            message: "이미 추가된 음악입니다.",
          });
          return;
        }

        // ✅ 기록 저장 개수 초과 여부도 밖에서 확인
        if (musics.length >= MAX_TRACK_COUNT) {
          setAlert({
            code: "400-3",
            message: `음악은 최대 ${MAX_TRACK_COUNT}개까지만 추가할 수 있습니다.`,
          });
          return;
        }

        // ✅ 문제 없을 경우만 추가
        setSelectedTracks((prev) => [...prev, data]);
      }
    } catch (error) {
      setAlert({
        code: "500-1",
        message: "음악 정보를 가져오는 데 실패했습니다.",
      });
      throw error;
    }
  };

  const fetchTracksFromPlaylist = async (playlistId: string) => {
    try {
      const res = await axios.get(
        `${API_URL}/music/spotify/playlist/${playlistId}`,
        {
          withCredentials: true,
        }
      );

      const { code, data, msg } = res.data;
      if (code.startsWith("200")) {
        setSelectedTracks(data);
      }
    } catch (error) {
      console.error(error);
      setAlert({
        code: "500-4",
        message: "플레이리스트 트랙을 불러오지 못했습니다.",
      });
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
        const { code, msg, data } = res.data;
        if (code.startsWith("2")) {
          return data;
        }
      }
    } catch (error) {
      setAlert({
        code: "500-2",
        message: "음악 기록을 불러오는 데 실패했습니다.",
      });
      throw error;
    }
  };

  // 📌 기록 저장 (신규 or 수정)
  const handleSaveRecord = async () => {
    try {
      // 📌 음악이 하나도 선택되지 않았다면 알림 표시
      if (selectedTracks.length === 0 && recentTracks.length === 0) {
        setAlert({
          code: "400-4",
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

      setIsSaving(true); // 저장 시작

      const finalMemo = memo.trim();

      const allTracks = [...selectedTracks, ...recentTracks];

      // 중복 제거 (id 기준)
      const uniqueTracksMap = new Map();
      allTracks.forEach((track) => {
        if (!uniqueTracksMap.has(track.id)) {
          uniqueTracksMap.set(track.id, track);
        }
      });
      const finalTracks = Array.from(uniqueTracksMap.values());

      console.log("finalTracks", finalTracks);

      const saveRes = await axios.post(
        `${API_URL}/music/save-all`,
        finalTracks,
        {
          withCredentials: true,
        }
      );

      const { code, msg } = saveRes.data;

      const musicIds = finalTracks.map((track) => track.id);

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
        alert("새로운 기록이 추가되었습니다!");
        router.push("/calendar");
      }
    } catch (error) {
      setAlert({
        code: "500-3",
        message: "음악 기록을 저장하는 중 오류가 발생했습니다.",
      });
      throw error;
    } finally {
      setIsSaving(false); // 저장 종료
    }
  };

  const handleSelectTrack = (track: any) => {
    setSelectedTracks((prev) => [...prev, track]);
  };

  const handleRemoveTrack = (trackId: any) => {
    setSelectedTracks((prev) => prev.filter((track) => track.id !== trackId));
  };

  const handleFetchRecentTracks = async () => {
    setIsFetchingRecent(true);
    const token = getSpotifyAccessToken();

    const res = await fetch(
      "https://api.spotify.com/v1/me/player/recently-played?limit=30",
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );

    const data = await res.json();

    const rawTracks = data.items.map((item: any) => ({
      id: item.track.id,
      name: item.track.name,
      singer: item.track.artists.map((a: any) => a.name).join(", "),
      singerId: item.track.artists.map((a: any) => a.id).join(", "),
      releaseDate: item.track.album.release_date,
      albumImage: item.track.album.images[0]?.url,
      genre: null,
      uri: item.track.uri,
      playedAt: item.played_at,
    }));

    // 최신순 정렬 + 중복 제거 후 최대 10곡
    const uniqueMap = new Map<string, any>();
    for (const track of rawTracks) {
      if (!uniqueMap.has(track.id)) uniqueMap.set(track.id, track);
      if (uniqueMap.size >= 10) break;
    }

    setRecentTracks(Array.from(uniqueMap.values()));
    setIsFetchingRecent(false);
  };

  const handleRemoveRecentTrack = (trackId: string) => {
    setRecentTracks((prev) => prev.filter((t) => t.id !== trackId));
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
        {isSaving && (
          <div className="fixed inset-0 z-50 flex items-center justify-center backdrop-blur-sm bg-transparent">
            <div className="flex items-center space-x-3">
              <svg
                className="w-5 h-5 animate-spin text-purple-500"
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 24 24"
              >
                <circle
                  className="opacity-25"
                  cx="12"
                  cy="12"
                  r="10"
                  stroke="currentColor"
                  strokeWidth="4"
                />
                <path
                  className="opacity-75"
                  fill="currentColor"
                  d="M4 12a8 8 0 018-8v8z"
                />
              </svg>
              <span>저장 중입니다...</span>
            </div>
          </div>
        )}
        <div className="space-y-7">
          <MusicSearch
            onSelectTrack={handleSelectTrack}
            selectedTracks={selectedTracks}
          />
          <MusicList
            selectedTracks={selectedTracks}
            onRemoveTrack={handleRemoveTrack}
            maxCount={membershipGrade === "premium" ? 50 : 20}
          />
          <MemoInput
            memo={memo}
            setMemo={setMemo}
            maxLength={membershipGrade === "premium" ? 500 : 200}
          />

          <div className="p-6">
            <div className="flex items-center justify-between mb-4">
              <button
                onClick={handleFetchRecentTracks}
                disabled={isFetchingRecent}
                className="mb-6 px-6 py-3 bg-green-500 text-white font-semibold rounded-lg shadow hover:bg-green-600 transition"
              >
                {isFetchingRecent
                  ? "불러오는 중..."
                  : "Spotify 최근 재생 목록 가져오기 (최대 10곡)"}
              </button>
              <h2 className="text-lg font-bold mb-4 text-right">
                * 최근에 재생한 음악이 Spotify history에 반영되는 데 시간이 좀
                걸릴 수 있습니다.
                <br /> * 동일한 음악은 한 번만 표시됩니다.
                <br /> * 가져온 재생 목록도 캘린더에 기록됩니다.
              </h2>
            </div>

            <RecentlyPlayedList
              selectedTracks={recentTracks}
              onRemoveTrack={handleRemoveRecentTrack}
            />
          </div>
        </div>
      </div>
    </Card>
  );
}
