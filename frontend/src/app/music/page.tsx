"use client";

import "@/app/music/style.css";
import { useState, useEffect, useRef } from "react";
import axios from "axios";

import RecentTracks from "./RecentTracks";
import MoodTracks from "./MoodTracks";
import PlaylistSection from "./PlaylistSection";

import { Card } from "@/components/ui/card";
import { useGlobalAlert } from "@/components/GlobalAlert";

const API_URL = "http://localhost:8080/api/v1";
const SPOTIFY_URL = "http://localhost:8080/api/v1/music/spotify";

export default function MusicPage() {
  const [userName, setUserName] = useState("사용자");
  const [singer, setSinger] = useState("아티스트");
  const [recentTracks, setRecentTracks] = useState([]);
  const [moodTracks, setMoodTracks] = useState([]);
  const [selectedMood, setSelectedMood] = useState("");
  const [membershipGrade, setMembershipGrade] = useState("basic");

  const [isLoading, setIsLoading] = useState(false);
  const { setAlert } = useGlobalAlert();
  const isFetched = useRef(false);

  useEffect(() => {
    const fetchAllData = async () => {
      if (isFetched.current) return;
      isFetched.current = true;

      try {
        setIsLoading(true);

        const result = await fetchUser();

        if (result.grade === "premium") {
          const fetchedArtist = await fetchRandomMusic(result.id);

          if (!fetchedArtist || !fetchedArtist.id) {
            setAlert({ code: "401", message: "최근 음악 없음, 빈 리스트 반환" });
            setRecentTracks([]);
          } else {
            await fetchRecentTracks(fetchedArtist.id, fetchedArtist.name);
          }
        }

        const randomMood = getRandomMood();
        setSelectedMood(randomMood);
        await fetchMoodTracks(randomMood);
      } catch (error) {
        setAlert({ code: "500", message: "데이터를 불러오는 중 오류가 발생했습니다." });
      } finally {
        setIsLoading(false);
      }
    };
    fetchAllData();
  }, []);

  const fetchUser = async () => {
    try {
      const userRes = await axios.get(`${API_URL}/user/byCookie`, {
        withCredentials: true,
      })
      const user = userRes.data.data;
      setUserName(user.nickName || user.name);

      const membershipRes = await axios.get(`${API_URL}/membership/my`, {
        withCredentials: true,
      })
      const membership = membershipRes.data.data;
      setMembershipGrade(membership?.grade || "basic");

      return { id: user.id, grade: membership.grade };
    } catch (error) {
      setAlert({ code: "500-2", message: "사용자 정보를 가져오지 못했어요." });
      throw error;
    }
  };

  const fetchRandomMusic = async (userId: any) => {
    try {
      const res = await axios.get(`${API_URL}/music/recent/random/${userId}`, {
        headers: {
          "Content-Type": "application/json",
        },
        withCredentials: true,
      });

      const { code, msg, data } = res.data;
      if (code.startsWith("200")) {
        return { id: data.singerId, name: data.singer };
      }
    } catch (error) {
      setAlert({ code: "500-3", message: "랜덤 음악을 불러오는 데 실패했어요." });
      throw error;
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

      const { code, msg, data } = res.data;
      if (code.startsWith("200")) {
        setRecentTracks(data);
      }
    } catch (error) {
      setAlert({ code: "500-4", message: "최근 기록에 대한 음악 정보를 불러올 수 없어요." });
      throw error;
    }
  };

  const fetchMoodTracks = async (mood: string) => {
    try {
      const res = await axios.get(`${SPOTIFY_URL}/search?keyword=${mood}`, {
        headers: {
          "Content-Type": "application/json",
        },
        withCredentials: true,
      });

      const { code, msg, data } = res.data;
      if (code.startsWith("200")) {
        setMoodTracks(data);
      }
    } catch (error) {
      setAlert({ code: "500-5", message: "기분에 맞는 음악을 불러오는 데 실패했어요." });
      throw error;
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
    </Card>
  );
}
