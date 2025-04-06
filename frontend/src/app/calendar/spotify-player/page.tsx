"use client";

import { useEffect, useState } from "react";
import { loadSpotifyPlayer } from "./player";

interface Music {
  id: string;
  name: string;
  singer: string;
  singerId: string;
  releaseDate: string;
  albumImage: string;
  genre: string;
  uri: string;
}

interface MusicRecord {
  id: number;
  date: string;
  memo: string;
  musics: Music[];
}

function getSpotifyAccessToken(): string | null {
  if (typeof document === "undefined") return null;
  const match = document.cookie
    .split(";")
    .map((c) => c.trim())
    .find((c) => c.startsWith("spotifyAccessToken="));

  return match ? decodeURIComponent(match.split("=")[1]) : null;
}

export default function MusicPlayer() {
  const [isPaused, setIsPaused] = useState(true);
  const [playerInstance, setPlayerInstance] = useState<Spotify.Player | null>(
    null
  );
  const [musicRecord, setMusicRecord] = useState<MusicRecord>();
  const [deviceId, setDeviceId] = useState<string | null>(null);
  const [repeatMode, setRepeatMode] = useState<"off" | "context" | "track">(
    "off"
  );
  const [hasLoadedAllTracks, setHasLoadedAllTracks] = useState(false);
  const [hasLoadedSingleTrack, setHasLoadedSingleTrack] = useState(false);
  const [currentTrackUri, setCurrentTrackUri] = useState<string | null>(null);
  const [isPlayingAll, setIsPlayingAll] = useState(false);

  const token = getSpotifyAccessToken();

  useEffect(() => {
    if (!token) {
      console.error("No access token found in cookies");
      return;
    }

    const stored = sessionStorage.getItem("spotify-music-record");
    if (stored) {
      try {
        setMusicRecord(JSON.parse(stored));
      } catch (err) {
        console.error("musicRecord 파싱 실패:", err);
      }
    } else {
      alert(
        "음악 데이터를 찾을 수 없습니다. 이전 페이지에서 다시 시도해주세요."
      );
    }

    loadSpotifyPlayer(token, (player, deviceId) => {
      console.log("🚀 플레이어 준비 완료, deviceId:", deviceId);
      setDeviceId(deviceId);
      setPlayerInstance(player);

      // 재생 상태 변화 감지
      player.addListener("player_state_changed", (state) => {
        if (!state) return;

        setIsPaused(state.paused);

        const uri = state?.track_window?.current_track?.uri;
        if (uri) {
          setCurrentTrackUri(uri);
        } else {
          setCurrentTrackUri(null);
        }

        console.log("🎧 상태 변경됨: isPaused =", state.paused);
      });
    });
  }, []);

  const handleTogglePlay = async () => {
    if (!playerInstance || !deviceId || !musicRecord) return;

    try {
      if (!hasLoadedAllTracks) {
        const uris = musicRecord.musics.map((music) => music.uri);
        const res = await fetch(
          "https://api.spotify.com/v1/me/player/play?device_id=" + deviceId,
          {
            method: "PUT",
            headers: {
              Authorization: `Bearer ${token}`,
              "Content-Type": "application/json",
            },
            body: JSON.stringify({ uris }),
          }
        );

        if (!res.ok) {
          console.error("🎵 전체 트랙 로드 실패:", await res.text());
          return;
        }

        console.log("✅ 전체 트랙 로드 완료");
        setHasLoadedAllTracks(true);
        setHasLoadedSingleTrack(false);
        setIsPlayingAll(true);
        return;
      }

      await playerInstance.togglePlay();
    } catch (err) {
      console.error("🎧 토글 실패:", err);
    }
  };

  const handlePlaySingleTrack = async (uri: string) => {
    if (!token || !deviceId) return;

    const res = await fetch(
      `https://api.spotify.com/v1/me/player/play?device_id=${deviceId}`,
      {
        method: "PUT",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ uris: [uri] }),
      }
    );

    if (res.ok) {
      console.log("🎶 단일 곡 재생됨:", uri);
      setHasLoadedSingleTrack(true);
      setHasLoadedAllTracks(false);
      setIsPlayingAll(false);
    } else {
      console.error("❌ 단일 곡 재생 실패:", await res.text());
    }
  };

  // 개별 곡 일시정지
  const toggleSingleTrackPlay = async () => {
    if (!playerInstance) return;

    try {
      await playerInstance.togglePlay(); // 재생 상태 토글
      console.log("⏯ 개별 곡 재생 상태 전환");
    } catch (err) {
      console.error("⏯ 개별 곡 토글 실패:", err);
    }
  };

  const handleVolumeChange = async (
    event: React.ChangeEvent<HTMLInputElement>
  ) => {
    const volume = Number(event.target.value);
    if (!playerInstance) return;

    try {
      await playerInstance.setVolume(volume);
      console.log("🔊 볼륨 조절됨:", volume);
    } catch (err) {
      console.error("볼륨 조절 실패:", err);
    }
  };

  const handleNext = async () => {
    if (!token || !deviceId) return;
    await fetch("https://api.spotify.com/v1/me/player/next", {
      method: "POST",
      headers: { Authorization: `Bearer ${token}` },
    });
  };

  const handlePrevious = async () => {
    if (!token || !deviceId) return;
    await fetch("https://api.spotify.com/v1/me/player/previous", {
      method: "POST",
      headers: { Authorization: `Bearer ${token}` },
    });
  };

  const toggleRepeatMode = async () => {
    if (!token) return;
    const nextMode =
      repeatMode === "off"
        ? "context"
        : repeatMode === "context"
        ? "track"
        : "off";
    await fetch(
      "https://api.spotify.com/v1/me/player/repeat?state=" + nextMode,
      {
        method: "PUT",
        headers: { Authorization: `Bearer ${token}` },
      }
    );
    setRepeatMode(nextMode);
  };

  return (
    <div className="flex flex-col items-center w-full max-w-2xl mx-auto mt-10 p-4 border border-gray-300 rounded-lg shadow">
      <h2 className="text-xl font-bold text-[#393D3F] mb-4">
        {musicRecord?.date} 재생 목록
      </h2>

      <ul className="w-full space-y-4">
        {musicRecord?.musics.map((music) => (
          <li
            key={music.id}
            onClick={() => handlePlaySingleTrack(music.uri)}
            className={`cursor-pointer flex items-center space-x-4 border p-4 rounded-lg border-gray-300 transition ${
              currentTrackUri === music.uri
                ? "bg-[#e7c6ff] text-[#393D3F] font-bold"
                : "hover:bg-[#c8b6ff]"
            }`}
          >
            <img
              src={music.albumImage}
              alt={music.name}
              className="w-16 h-16 object-cover rounded-md"
            />
            <div>
              <h3 className="text-lg font-semibold">{music.name}</h3>
              <p className="text-sm text-gray-500">
                {music.singer}
                {currentTrackUri === music.uri && (
                  <>
                    <span className="ml-2 text-green-600">(재생 중)</span>
                    <button
                      onClick={(e) => {
                        e.stopPropagation(); // 리스트 클릭 방지
                        toggleSingleTrackPlay();
                      }}
                      className="ml-2 text-base text-[#393D3F] bg-[#c8b6ff] rounded px-2 py-1 hover:bg-white"
                      title={isPaused ? "재생" : "일시정지"}
                    >
                      {isPaused ? "▶ 재생" : "⏸ 일시정지"}
                    </button>
                  </>
                )}
              </p>
            </div>
          </li>
        ))}
      </ul>

      <div className="flex space-x-4 mb-4 mt-4">
        <button
          onClick={handlePrevious}
          className="px-3 py-2 bg-[#c8b6ff] text-white rounded hover:bg-[#e7c6ff]"
        >
          이전 곡
        </button>
        <button
          onClick={handleTogglePlay}
          className="px-4 py-2 bg-[#c8b6ff] text-white rounded hover:bg-[#e7c6ff]"
        >
          {!isPlayingAll || isPaused ? "▶ 플리 재생" : "⏸ 일시정지"}
        </button>
        <button
          onClick={handleNext}
          className="px-3 py-2 bg-[#c8b6ff] text-white rounded hover:bg-[#e7c6ff]"
        >
          다음 곡
        </button>
      </div>

      <div className="flex space-x-4 items-center">
        <label className="text-sm text-gray-700">
          volume
          <input
            type="range"
            min="0"
            max="1"
            step="0.01"
            defaultValue="0.5"
            onChange={handleVolumeChange}
            className="ml-2 w-32"
          />
        </label>

        <button
          onClick={toggleRepeatMode}
          className={`px-3 py-2 rounded transition
                    ${
                      repeatMode === "off"
                        ? "bg-[#c8b6ff] text-white hover:bg-[#e7c6ff]"
                        : "bg-white text-[#c8b6ff] border border-[#c8b6ff] hover:bg-[#f3e8ff]"
                    }`}
        >
          ⟳ 반복{" "}
          {repeatMode === "off"
            ? "없음"
            : repeatMode === "context"
            ? "전체"
            : "한곡"}
        </button>
      </div>
    </div>
  );
}
