"use client";

import "@/app/music/style.css";
import { useState, useEffect, useRef } from "react";
import axios from "axios";
import { getCookie } from "@/app/utils/cookie";
import RecentTracks from "./RecentTracks";
import MoodTracks from "./MoodTracks";

const API_URL = "http://localhost:8080/api/v1";
const SPOTIFY_URL = "http://localhost:8080/api/v1/music/spotify";

export default function MusicRecommendation() {
  const [userName, setUserName] = useState("사용자");
  const [singer, setSinger] = useState("");
  const [recentTracks, setRecentTracks] = useState([]);
  const [moodTracks, setMoodTracks] = useState([]);
  const [selectedMood, setSelectedMood] = useState(null);

  const [isLoading, setIsLoading] = useState(false);
  const isFetched = useRef(false);

  useEffect(() => {
    const fetchAllData = async () => {
      if (isFetched.current) return;
      isFetched.current = true;

      try {
        setIsLoading(true);

        const fetchedUserId = await fetchUser();
        await new Promise(resolve => setTimeout(resolve, 1000));

        const fetchedArtist = await fetchRandomMusic(fetchedUserId);
        await new Promise(resolve => setTimeout(resolve, 1000));

        const randomMood = getRandomMood();
        setSelectedMood(randomMood);

        await fetchRecentTracks(fetchedArtist.id, fetchedArtist.name);
        await new Promise(resolve => setTimeout(resolve, 1000));

        await fetchMoodTracks(randomMood);
      } catch (error) {
        console.error("데이터 로드 중 오류 발생:", error);
      } finally {
        setIsLoading(false);
      }
    };
    fetchAllData();
  }, []);

  const fetchUser = async () => {
    try {
      const jwt = getCookie("accessToken");
      const res = await axios.get(`${API_URL}/user/byToken`, {
        headers: { Authorization: `Bearer ${jwt}`, "Content-Type": "application/json" }
      });
      setUserName(res.data.nickName || res.data.name);
      return res.data.id;
    } catch (error) {
      console.error("사용자 정보 조회 실패:", error);
      throw error;
    }
  };

  const fetchRandomMusic = async (userId) => {
    try {
      const jwt = getCookie("accessToken");
      const res = await axios.get(`${API_URL}/music/recent/random/${userId}`, {
        headers: { Authorization: `Bearer ${jwt}`, "Content-Type": "application/json" }
      });
      return { id: res.data.singerId, name: res.data.singer };
    } catch (error) {
      console.error("랜덤 음악 조회 실패:", error);
      throw error;
    }
  };

  const fetchRecentTracks = async (artistId, artistName) => {
    try {
      const jwt = getCookie("accessToken");

      const idList = artistId.split(",").map(id => id.trim());
      const nameList = artistName.split(",").map(name => name.trim());
      const randomNum = Math.floor(Math.random() * idList.length);

      const selectedArtist = idList[randomNum];
      setSinger(nameList[randomNum]);

      console.log("[Artist] jwt:", jwt);
      console.log("[Artist] randomNum:", randomNum);
      console.log("[Artist] Artist:", selectedArtist);

      const res = await axios.get(`${SPOTIFY_URL}/artist/${selectedArtist}/top-tracks`, {
        headers: { Authorization: `Bearer ${jwt}`, "Content-Type": "application/json" }
      });
      setRecentTracks(res.data);
      console.log("[Artist] result:", res.data);
    } catch (error) {
      console.error("최근 음악 조회 실패:", error);
      throw error;
    }
  };

  const fetchMoodTracks = async (mood) => {
    try {
      const jwt = getCookie("accessToken");

      console.log("[Mood] jwt:", jwt);
      console.log("[Mood] mood:", mood);
      const res = await axios.get(`${SPOTIFY_URL}/search?keyword=${mood}`, {
        headers: { Authorization: `Bearer ${jwt}`, "Content-Type": "application/json" }
      });
      setMoodTracks(res.data);
      console.log("[Mood] result:", res.data);
    } catch (error) {
      console.error("기분 음악 조회 실패:", error);
      throw error;
    }
  };

  const getRandomMood = () => {
    const moodOptions = ["행복", "슬픔", "에너지", "편안", "사랑", "우울", "설렘"];
    return moodOptions[Math.floor(Math.random() * moodOptions.length)];
  };

  const LoadingScreen = () => {
    return (
      <div className="absolute inset-0 flex justify-center items-center bg-white z-50"
        style={{ backgroundColor: "rgba(255, 255, 255, 0.7)" }}>
        <div className="text-center">
          <p className="text-lg font-semibold text-gray-800">🎵 추천 음악을 불러오는 중...</p>
          <div className="w-8 h-8 mt-2 border-t-4 border-blue-500 border-solid rounded-full animate-spin"></div>
        </div>
      </div>
    );
  };

  return (
    <div className="p-6 space-y-8">
      <div className="space-y-1">
        <h2 className="text-2xl font-bold">음악 추천</h2>
        <p className="text-gray-500">{userName}님 맞춤 노래 추천</p>
      </div>
      <div className="relative">
        {isLoading && <LoadingScreen />}
        <RecentTracks singer={singer} tracks={recentTracks} />
        <MoodTracks mood={selectedMood} tracks={moodTracks} />
      </div>
    </div>
  );
}
