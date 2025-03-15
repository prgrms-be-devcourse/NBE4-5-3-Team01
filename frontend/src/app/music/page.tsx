"use client";

import "@/app/music/style.css";
import { useState, useEffect, useRef } from "react";
import axios from "axios";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faChevronLeft, faChevronRight } from "@fortawesome/free-solid-svg-icons";
import { Button } from "@/components/ui/button";

export default function MusicRecommendation() {
  const API_URL = "http://localhost:8080/api/v1";
  const SPOTIFY_URL = "http://localhost:8080/api/v1/music/spotify";

  const [userId, setUserId] = useState("");
  const [userName, setUserName] = useState("사용자");
  const [singer, setSinger] = useState("");
  const [recentTracks, setRecentTracks] = useState([]);
  const [moodTracks, setMoodTracks] = useState([]);

  const [isLoading, setIsLoading] = useState(false);

  const recentTrackRef = useRef(null);
  const moodTrackRef = useRef(null);

  const moodOptions = ["행복", "슬픔", "에너지", "편안", "사랑", "우울", "설렘"];
  const moodMapping = {
    "행복": "기분이 좋을 때", 
    "슬픔": "마음이 울적할 때",
    "에너지": "활기차고 싶을 때",
    "편안": "편안함을 느끼고 싶을 때",
    "사랑": "사랑이 가득할 때",
    "우울": "우울한 기분일 때",
    "설렘": "설레는 순간일 때",
  };

  const [isAtStartRecent, setIsAtStartRecent] = useState(true);
  const [isAtEndRecent, setIsAtEndRecent] = useState(false);
  const [isAtStartMood, setIsAtStartMood] = useState(true);
  const [isAtEndMood, setIsAtEndMood] = useState(false);

  const getRandomMood = () => moodOptions[Math.floor(Math.random() * moodOptions.length)];
  const [selectedMood, setSelectedMood] = useState<string | null>(null);

  useEffect(() => {
    const fetchAllData = async () => {
      try {
        setIsLoading(true);

        const fetchedUserId = await fetchUser();
        const fetchedArtistId = await fetchRandomMusic(fetchedUserId);

        const randomMood = getRandomMood();
        setSelectedMood(randomMood);

        await fetchRecentTracks(fetchedArtistId);
        await fetchMoodTracks(selectedMood);
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
      const jwt = localStorage.getItem("accessToken");

      const res = await axios.get(`${API_URL}/user/byToken`, {
        headers: {
          Authorization: `Bearer ${jwt}`,
          "Content-Type": "application/json"
        }
      });

      const userId = res.data.id;
      const { name, nickName } = res.data;

      setUserId(userId);
      setUserName(nickName || name);
      return userId;
    } catch (error) {
      console.error("사용자 정보 조회 실패:", error);
      throw error;
    }
  };

  const fetchRandomMusic = async (userId) => {
    try {
      const jwt = localStorage.getItem("accessToken");
      const spotify = localStorage.getItem("spotifyToken");

      const randomRes = await axios.get(`${API_URL}/music/recent/random/${userId}`, {
        headers: {
          Authorization: `Bearer ${jwt}`,
          "Content-Type": "application/json"
        }
      });

      setSinger(randomRes.data.singer);
      return randomRes.data.singerId;
    } catch (error) {
      console.error("랜덤 음악 조회 실패:", error);
      throw error;
    }
  };

  const fetchRecentTracks = async (artistId) => {
    try {
      const jwt = localStorage.getItem("accessToken");
      const spotify = localStorage.getItem("spotifyToken");

      const res = await axios.get(
        `${SPOTIFY_URL}/artist/${artistId}/top-tracks`, {
          headers: {
            Authorization: `Bearer ${jwt}`,
            "Spotify-Token": spotify,
            "Content-Type": "application/json"
          }
        });
        
      setRecentTracks(res.data);
    } catch (error) {
      console.error("최근 음악 조회 실패:", error);
      throw error;
    }
  };

  const fetchMoodTracks = async (mood) => {
    try {
      const jwt = localStorage.getItem("accessToken");
      const spotify = localStorage.getItem("spotifyToken");

      const res = await axios.get(`${SPOTIFY_URL}/search?keyword=${mood}`, {
        headers: {
          Authorization: `Bearer ${jwt}`,
          "Spotify-Token": spotify,
          "Content-Type": "application/json"
        }
      });

      setMoodTracks(res.data);
    } catch (error) {
      console.error("기분 음악 조회 실패:", error);
      throw error;
    }
  };

  const updateScrollState = (ref, setStart, setEnd) => {
    if (ref.current) {
      const { scrollLeft, scrollWidth, clientWidth } = ref.current;
      setStart(scrollLeft === 0);
      setEnd(scrollLeft + clientWidth >= scrollWidth);
    }
  };

  useEffect(() => {
    if (recentTrackRef.current) {
      recentTrackRef.current.addEventListener("scroll", () =>
        updateScrollState(recentTrackRef, setIsAtStartRecent, setIsAtEndRecent)
      );
    }
    if (moodTrackRef.current) {
      moodTrackRef.current.addEventListener("scroll", () =>
        updateScrollState(moodTrackRef, setIsAtStartMood, setIsAtEndMood)
      );
    }
  }, []);

  const scrollLeft = (ref) => {
    if (ref.current) {
      ref.current.scrollTo({ left: 0, behavior: "smooth" });
    }
  };

  const scrollRight = (ref) => {
    if (ref.current) {
      ref.current.scrollTo({
        left: ref.current.scrollWidth,
        behavior: "smooth",
      });
    }
  };

  const LoadingScreen = () => {
    return (
      <div className="absolute inset-0 flex justify-center items-center bg-white z-50"
        style={{
          backgroundColor: "rgba(255, 255, 255, 0.7)"
        }}
      >
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
        <section className="mb-7">
          <div className="flex justify-between items-center mb-5">
            <h3 className="text-xl font-semibold break-words w-full">
              최근 들은 <span className="point-color mr-1">{singer}</span>의 인기 음악
            </h3>
            <div className="flex space-x-2">
              <button
                onClick={() => scrollLeft(recentTrackRef)}
                className={`px-3 transition-colors ${
                  isAtStartRecent ? "text-gray-300 cursor-default" : "text-black"
                }`}
                disabled={isAtStartRecent}
              >
                <FontAwesomeIcon icon={faChevronLeft} />
              </button>
              <button
                onClick={() => scrollRight(recentTrackRef)}
                className={`px-3 transition-colors ${
                  isAtEndRecent ? "text-gray-300 cursor-default" : "text-black"
                }`}
                disabled={isAtEndRecent}
              >
                <FontAwesomeIcon icon={faChevronRight} />
              </button>
            </div>
          </div>

          <div className="relative">
            <div
              ref={recentTrackRef}
              className="flex gap-4 overflow-x-auto hide-scrollbar whitespace-nowrap"
              onWheel={(e) => {
                e.preventDefault();
                recentTrackRef.current.scrollLeft += e.deltaY;
              }}
            >
              {recentTracks.map((track) => (
                <div key={track.id} className="w-40 flex-shrink-0">
                  <img
                    src={track.albumImage}
                    alt={track.name}
                    className="rounded-lg w-full h-auto"
                  />
                  <p className="text-sm font-medium mt-2 break-words track-title">
                    {track.name}
                  </p>
                  <p className="text-xs text-gray-500 track-artist">{track.singer}</p>
                </div>
              ))}
            </div>
          </div>
        </section>

        <section>
          <div className="flex justify-between items-center mb-5">
          <h3 className="text-xl font-semibold mb-2 text-[#393D3F]">
            <span className="point-color">{moodMapping[selectedMood]}</span> 이런 음악 어때요?
          </h3>

            <div className="flex space-x-2">
              <button
                onClick={() => scrollLeft(moodTrackRef)}
                className={`px-3 transition-colors ${
                  isAtStartMood ? "text-gray-300 cursor-default" : "text-black"
                }`}
                disabled={isAtStartMood}
              >
                <FontAwesomeIcon icon={faChevronLeft} />
              </button>
              <button
                onClick={() => scrollRight(moodTrackRef)}
                className={`px-3 transition-colors ${
                  isAtEndMood ? "text-gray-300 cursor-default" : "text-black"
                }`}
                disabled={isAtEndMood}
              >
                <FontAwesomeIcon icon={faChevronRight} />
              </button>
            </div>
          </div>

          <div className="relative">
            <div
              ref={moodTrackRef}
              className="flex gap-4 overflow-x-auto hide-scrollbar whitespace-nowrap"
              onWheel={(e) => {
                e.preventDefault();
                moodTrackRef.current.scrollLeft += e.deltaY;
              }}
            >
              {moodTracks.map((track) => (
                <div key={track.id} className="w-40 flex-shrink-0">
                  <img
                    src={track.albumImage}
                    alt={track.name}
                    className="rounded-lg w-full h-auto"
                  />
                  <p className="text-sm font-medium mt-2 break-words track-title">
                    {track.name}
                  </p>
                  <p className="text-xs text-gray-500 track-artist">{track.singer}</p>
                </div>
              ))}
            </div>
          </div>
        </section>
      </div>
    </div>
  );
}
