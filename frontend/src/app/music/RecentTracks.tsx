"use client";

import { useRef, useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import axios from "axios";
import Image from "next/image";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faChevronLeft, faChevronRight } from "@fortawesome/free-solid-svg-icons";
import { useHandleApiError } from "@/lib/useHandleApiError";

const API_URL = "http://localhost:8080/api/v1";

interface Track {
  id: string;
  name: string;
  singer: string;
  albumImage: string;
}

const RecentTracks = ({ singer, tracks }: { singer: string; tracks: Track[] }) => {
  const router = useRouter();
  const trackRef = useRef<HTMLDivElement>(null);
  const [isAtStart, setIsAtStart] = useState(true);
  const [isAtEnd, setIsAtEnd] = useState(false);
  const { handleApiError } = useHandleApiError();

  const handleSelectRecommendedTrack = async (trackId: string) => {
    try {
      const res = await axios.get(`${API_URL}/calendar/today`, {
        withCredentials: true,
      });

      const { code, data } = res.data;

      if (code === "200-1") {
        router.push(`/calendar/record?id=${data}&trackId=${trackId}`);
      } else {
        const { year, month, day } = data;
        router.push(`/calendar/record?year=${year}&month=${month}&day=${day}&trackId=${trackId}`);
      }
    } catch (error) {
      handleApiError(error);
    }
  };

  const updateScrollState = () => {
    if (trackRef.current) {
      const { scrollLeft, scrollWidth, clientWidth } = trackRef.current;
      setIsAtStart(Math.round(scrollLeft) === 0);
      setIsAtEnd(Math.round(scrollLeft + clientWidth) >= Math.round(scrollWidth));
    }
  };

  useEffect(() => {
    const element = trackRef.current;
    if (!element) return;

    const handleScroll = () => updateScrollState();
    element.addEventListener("scroll", handleScroll);
    updateScrollState();

    return () => {
      element.removeEventListener("scroll", handleScroll);
    };
  }, [tracks]);

  const scrollLeft = () => {
    if (trackRef.current) {
      trackRef.current.scrollTo({ left: 0, behavior: "smooth" });
      setTimeout(updateScrollState, 200);
    }
  };

  const scrollRight = () => {
    if (trackRef.current) {
      trackRef.current.scrollTo({ left: trackRef.current.scrollWidth, behavior: "smooth" });
      setTimeout(updateScrollState, 200);
    }
  };

  return (
    <section className="mb-7">
      <div className="flex justify-between items-center mb-5">
        <h3 className="text-xl font-semibold break-words w-full">
          최근 들은 <span className="point-color mr-1">{singer}</span>의 인기 음악
        </h3>
        <div className="flex space-x-2">
          <button
            onClick={scrollLeft}
            className={`px-3 ${isAtStart ? "text-gray-300 cursor-default" : "text-black"}`}
            disabled={isAtStart}
          >
            <FontAwesomeIcon icon={faChevronLeft} />
          </button>
          <button
            onClick={scrollRight}
            className={`px-3 ${isAtEnd || tracks.length === 0 ? "text-gray-300 cursor-default" : "text-black"}`}
            disabled={isAtEnd || tracks.length === 0}
          >
            <FontAwesomeIcon icon={faChevronRight} />
          </button>
        </div>
      </div>

      {tracks.length === 0 ? (
        <div className="p-6 text-center text-gray-500">
          아직 음악을 기록하신 적이 없어요!
          <br />
          새롭게 기록하면 추천해드릴게요! 😊
        </div>
      ) : (
        <div className="relative">
          <div
            ref={trackRef}
            className="flex gap-4 overflow-x-auto hide-scrollbar whitespace-nowrap"
          >
            {tracks.map((track) => (
              <div
                key={track.id}
                onClick={() => handleSelectRecommendedTrack(track.id)}
                className="track-item w-40 flex-shrink-0"
              >
                <Image
                  src={track.albumImage}
                  alt={track.name}
                  width={160}
                  height={160}
                  className="rounded-lg w-full h-auto track-img"
                />
                <p className="text-sm font-medium mt-2 break-words track-title">{track.name}</p>
                <p className="text-xs text-gray-500 track-artist singer-name">{track.singer}</p>
                <span className="add-button">+</span>
              </div>
            ))}
          </div>
        </div>
      )}
    </section>
  );
};

export default RecentTracks;
