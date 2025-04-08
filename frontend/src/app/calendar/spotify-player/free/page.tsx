"use client";

import { useEffect, useState } from "react";

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

export default function SpotifyFreePlayerPage() {
  const [musicRecord, setMusicRecord] = useState<MusicRecord | null>(null);

  useEffect(() => {
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
  }, []);

  if (!musicRecord) {
    return <p className="text-center mt-10 text-gray-500">로딩 중...</p>;
  }

  return (
    <div className="flex flex-col items-center justify-center w-full max-w-2xl mx-auto mt-10">
      <h2 className="text-2xl font-bold text-[#393D3F] mb-4">
        {musicRecord.date} 재생 목록
      </h2>

      <p className="text-sm text-gray-500 mb-6">
        각 곡을 클릭하면 Spotify 앱에서 직접 감상할 수 있어요.
      </p>

      <ul className="w-full space-y-4">
        {musicRecord.musics.map((music) => (
          <li
            key={music.id}
            className="flex items-center space-x-4 border p-4 rounded-lg border-gray-300 hover:bg-gray-100 transition"
          >
            <img
              src={music.albumImage}
              alt={music.name}
              className="w-16 h-16 object-cover rounded-md"
            />
            <div className="flex flex-col">
              <h3 className="text-lg font-semibold">{music.name}</h3>
              <p className="text-sm text-gray-500 mb-1">{music.singer}</p>
              <a
                href={`https://open.spotify.com/track/${music.id}`}
                target="_blank"
                rel="noopener noreferrer"
                className="text-sm text-[#c8b6ff] hover:underline"
              >
                Spotify에서 듣기 →
              </a>
            </div>
          </li>
        ))}
      </ul>
    </div>
  );
}
