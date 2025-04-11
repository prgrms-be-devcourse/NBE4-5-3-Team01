"use client";

import { useState, useEffect, useRef } from "react";
import axios from "axios";
import RecentTracks from "./RecentTracks";
import MoodTracks from "./MoodTracks";
import PlaylistSection from "./PlaylistSection";
import "@/app/music/style.css";
import { Card } from "@/components/ui/card";
import { useHandleApiError } from "@/lib/useHandleApiError";

const API_URL = "http://localhost:8080/api/v1";
const SPOTIFY_URL = "http://localhost:8080/api/v1/music/spotify";

export default function MusicPage() {
  const [userName, setUserName] = useState("사용자");
  const [singer, setSinger] = useState("아티스트");
  const [recentTracks, setRecentTracks] = useState([]);
  const [moodTracks, setMoodTracks] = useState([]);
  const [selectedMood, setSelectedMood] = useState("");
  const [membershipGrade, setMembershipGrade] = useState("basic");
  const [spotifyToken, setSpotifyToken] = useState(true);

  const [isLoading, setIsLoading] = useState(false);
  const isFetched = useRef(false);
  const { handleApiError } = useHandleApiError();

  useEffect(() => {
    if (isFetched.current) return;
    isFetched.current = true;

    hasSpotifyToken();

    const fetchAllData = async () => {
      try {
        setIsLoading(true);

        const randomMood = getRandomMood();
        setSelectedMood(randomMood);
        await fetchMoodTracks(randomMood);

        const result = await fetchUser();
        if (!result) throw new Error("사용자 정보 없음");

        if (result.grade === "premium") {
          const fetchedArtist = await fetchRandomMusic(result.id);

          if (!fetchedArtist || !fetchedArtist.id) {
            handleApiError({
              response: {
                data: { code: "401", msg: "최근 음악 없음, 빈 리스트 반환" },
              },
            });
            setRecentTracks([]);
          } else {
            await fetchRecentTracks(fetchedArtist.id, fetchedArtist.name);
          }
        }
      } catch (error) {
        handleApiError(error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchAllData();
  }, []);

  const hasSpotifyToken = async () => {
    const res = await axios.get(`${API_URL}/user/spotify-token`, {
      withCredentials: true,
    });
    const token = res.data;

    if (!token) {
      setSpotifyToken(false);
    }
  };

  const fetchUser = async () => {
    try {
      const userRes = await axios.get(`${API_URL}/user/byCookie`, {
        withCredentials: true,
      });
      const user = userRes.data.data;
      setUserName(user.nickName || user.name);

      await axios.post(
        `${API_URL}/membership/init`,
        {},
        {
          withCredentials: true,
        }
      );

      const res = await axios.get(`${API_URL}/membership/my`, {
        withCredentials: true,
      });

      setMembershipGrade(res.data.data.grade || "basic");
      return { id: user.id, grade: res.data.data.grade };
    } catch (error) {
      handleApiError(error);
    }
  };

  const fetchRandomMusic = async (userId: string) => {
    try {
      const res = await axios.get(`${API_URL}/music/recent/random/${userId}`, {
        withCredentials: true,
      });

      const { code, data } = res.data;
      if (code.startsWith("200")) {
        return { id: data.singerId, name: data.singer };
      }
    } catch (error) {
      handleApiError(error);
    }
  };

  const fetchRecentTracks = async (artistId: string, artistName: string) => {
    try {
      const idList = artistId.split(",").map((id) => id.trim());
      const nameList = artistName.split(",").map((name) => name.trim());
      const randomNum = Math.floor(Math.random() * idList.length);

      const selectedArtist = idList[randomNum];
      setSinger(nameList[randomNum]);

      const res = await axios.get(
        `${SPOTIFY_URL}/artist/${selectedArtist}/top-tracks`,
        {
          headers: {
            "Content-Type": "application/json",
          },
          withCredentials: true,
        }
      );

      const { code, data } = res.data;
      if (code.startsWith("200")) {
        setRecentTracks(data);
      }
    } catch (error) {
      handleApiError(error);
    }
  };

  const fetchMoodTracks = async (mood: string) => {
    try {
      const res = await axios.get(`${SPOTIFY_URL}/search?keyword=${mood}`, {
        withCredentials: true,
      });

      const { code, data } = res.data;
      if (code.startsWith("200")) {
        setMoodTracks(data);
      }
    } catch (error) {
      handleApiError(error);
    }
  };

  const getRandomMood = () => {
    const moodOptions = [
      "행복",
      "슬픔",
      "에너지",
      "편안",
      "사랑",
      "우울",
      "설렘",
    ];
    return moodOptions[Math.floor(Math.random() * moodOptions.length)];
  };

  const LoadingScreen = () => {
    return (
      <div
        className="absolute inset-0 flex justify-center items-center bg-white z-50"
        style={{ backgroundColor: "rgba(255, 255, 255, 0.7)" }}
      >
        <div className="text-center">
          <p className="text-lg font-semibold text-gray-800">
            🎵 추천 음악을 불러오는 중...
          </p>
          <div className="w-8 h-8 mt-2 border-t-4 border-blue-500 border-solid rounded-full animate-spin"></div>
        </div>
      </div>
    );
  };

  return (
    <Card className="m-10 bg-white border-0 p-0">
      {!spotifyToken ? (
        <div className="bg-yellow-50 border border-yellow-300 p-4 rounded text-yellow-800">
          <p className="font-semibold">스포티파이 연동 필요 🎧</p>
          <p className="text-sm mt-1">
            죄송하지만, 이 페이지는{" "}
            <a
              className="font-medium text-blue-500"
              href="https://open.spotify.com/"
            >
              스포티파이 연동
            </a>
            을 완료하셔야 볼 수 있어요!
          </p>
        </div>
      ) : (
        <>
          <div className="p-6 space-y-8">
            <PlaylistSection />
          </div>

          <div className="p-6 space-y-8">
            <div className="space-y-1">
              <h2 className="text-2xl font-bold">🎧 음악 추천</h2>
              <p className="text-gray-500">{userName}님 맞춤 노래 추천</p>
            </div>
            <div className="relative">
              {isLoading && <LoadingScreen />}
              {membershipGrade === "premium" && (
                <RecentTracks singer={singer} tracks={recentTracks} />
              )}
              <MoodTracks mood={selectedMood} tracks={moodTracks} />
            </div>
          </div>
        </>
      )}
    </Card>
  );
}
