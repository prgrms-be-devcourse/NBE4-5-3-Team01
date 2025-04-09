"use client";

import { useRef, useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import axios from "axios";
import Image from "next/image";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faChevronLeft, faChevronRight } from "@fortawesome/free-solid-svg-icons";
import { useHandleApiError } from "@/lib/useHandleApiError";

const API_URL = "http://localhost:8080/api/v1";

const moodMapping: Record<string, string> = {
  행복: "기분이 좋을 때",
  슬픔: "마음이 울적할 때",
  에너지: "활기차고 싶을 때",
  편안: "편안함을 느끼고 싶을 때",
  사랑: "사랑이 가득할 때",
  우울: "우울한 기분일 때",
  설렘: "설레는 순간일 때",
};

interface Track {
  id: string;
  name: string;
  singer: string;
  albumImage: string;
}

const MoodTracks = ({ mood, tracks }: { mood: string; tracks: Track[] }) => {
  const router = useRouter();
  const trackRef = useRef<HTMLDivElement>(null);
  const [isAtStart, setIsAtStart] = useState(true);
  const [isAtEnd, setIsAtEnd] = useState(false);
  const { handleApiError } = useHandleApiError();

  const handleSelectRecommendedTrack = async (trackId: string) => {
    try {
      const todayRecordRes = await axios.get(`${API_URL}/calendar/today`, {
        withCredentials: true,
      });

      const todayRecord = todayRecordRes.data;

      if (todayRecord.code === "200-1") {
        router.push(`/calendar/record?id=${todayRecord.data}&trackId=${trackId}`);
      } else {
        const { year, month, day } = todayRecord.data;
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
          <span className="point-color mr-1">{moodMapping[mood]}</span> 이런 음악 어때요?
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
            className={`px-3 ${isAtEnd ? "text-gray-300 cursor-default" : "text-black"}`}
            disabled={isAtEnd}
          >
            <FontAwesomeIcon icon={faChevronRight} />
          </button>
        </div>
      </div>
      <div className="relative">
        <div ref={trackRef} className="flex gap-4 overflow-x-auto hide-scrollbar whitespace-nowrap">
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
    </section>
  );
};

export default MoodTracks;
