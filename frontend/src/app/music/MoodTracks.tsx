"use client";

import { useRef, useState, useEffect } from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faChevronLeft, faChevronRight } from "@fortawesome/free-solid-svg-icons";

const moodMapping = {
    "행복": "기분이 좋을 때",
    "슬픔": "마음이 울적할 때",
    "에너지": "활기차고 싶을 때",
    "편안": "편안함을 느끼고 싶을 때",
    "사랑": "사랑이 가득할 때",
    "우울": "우울한 기분일 때",
    "설렘": "설레는 순간일 때",
};

const MoodTracks = ({ mood, tracks }) => {
    const trackRef = useRef(null);
    const [isAtStart, setIsAtStart] = useState(true);
    const [isAtEnd, setIsAtEnd] = useState(false);

    // 스크롤 상태 업데이트 함수
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

        const handleScroll = () => {
            updateScrollState();
        };

        element.addEventListener("scroll", handleScroll);
        updateScrollState();  // 초기 스크롤 상태 설정

        return () => {
            element.removeEventListener("scroll", handleScroll);
        };
    }, [tracks]); // tracks 변경될 때마다 스크롤 상태 업데이트    

    const scrollLeft = () => {
        if (trackRef.current) {
            trackRef.current.scrollTo({ left: 0, behavior: "smooth" });
            setTimeout(updateScrollState, 200);  // 스크롤 이후 상태 업데이트
        }
    };

    const scrollRight = () => {
        if (trackRef.current) {
            trackRef.current.scrollTo({ left: trackRef.current.scrollWidth, behavior: "smooth" });
            setTimeout(updateScrollState, 200);  // 스크롤 이후 상태 업데이트
        }
    };

    return (
        <section className="mb-7">
            <div className="flex justify-between items-center mb-5">
                <h3 className="text-xl font-semibold break-words w-full">
                    <span className="point-color mr-1">{moodMapping[mood]}</span> 이런 음악 어때요?
                </h3>
                <div className="flex space-x-2">
                    <button onClick={scrollLeft} className={`px-3 ${isAtStart ? "text-gray-300 cursor-default" : "text-black"}`} disabled={isAtStart}>
                        <FontAwesomeIcon icon={faChevronLeft} />
                    </button>
                    <button onClick={scrollRight} className={`px-3 ${isAtEnd ? "text-gray-300 cursor-default" : "text-black"}`} disabled={isAtEnd}>
                        <FontAwesomeIcon icon={faChevronRight} />
                    </button>
                </div>
            </div>
            <div className="relative">
                <div ref={trackRef} className="flex gap-4 overflow-x-auto hide-scrollbar whitespace-nowrap">
                    {tracks.map(track => (
                        <div key={track.id} className="w-40 flex-shrink-0">
                            <img src={track.albumImage} alt={track.name} className="rounded-lg w-full h-auto track-img" />
                            <p className="text-sm font-medium mt-2 break-words track-title">{track.name}</p>
                            <p className="text-xs text-gray-500 track-artist singer-name">{track.singer}</p>
                        </div>
                    ))}
                </div>
            </div>
        </section>
    );
};

export default MoodTracks;