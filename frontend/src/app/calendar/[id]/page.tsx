"use client";

import {
  Carousel,
  CarouselContent,
  CarouselItem,
  CarouselNext,
  CarouselPrevious,
} from "@/components/ui/carousel";
import { Card, CardContent } from "@/components/ui/card";
import { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import { MusicRecord } from "@/types/musicRecord";
import { AxiosError } from "axios";
import { fetchMusicRecords } from "@/lib/api/musicRecord";
import { getSpotifyAccessToken } from "@/app/utils/getSpotifyAccessToken";
import {useModal} from "@/hooks/useModal";

export default function MusicDetailPage() {
  const { showAlert, ModalComponent } = useModal();

  const [musicRecord, setMusicRecord] = useState<MusicRecord>();
  const [currentYear, setCurrentYear] = useState<number>(
    new Date().getFullYear()
  );
  const [currentMonth, setCurrentMonth] = useState<number>(
    new Date().getMonth() + 1
  );
  const [currentDay, setCurrentDay] = useState<number>(new Date().getDay());
  const [calendarPermission, setCalendarPermission] = useState<string | null>();
  const [isPremium, setIsPremium] = useState<boolean | null>(null);

  const params = useParams();
  const router = useRouter();

  useEffect(() => {
    async function initMusicRecords() {
      try {
        const response = await fetchMusicRecords(params.id);

        const musicRecord: MusicRecord = response.data.data;
        const [year, month, day] = musicRecord.date.split("-");

        setMusicRecord(musicRecord);
        setCurrentYear(parseInt(year, 10));
        setCurrentMonth(parseInt(month, 10));
        setCurrentDay(parseInt(day, 10));
        setCalendarPermission(musicRecord.calendarPermission);
      } catch (error) {
        if (error instanceof AxiosError)
          await showAlert({
            title: error.response!.data.msg,
            description: "확인 버튼을 누르면 내 캘린더로 이동합니다.",
            confirmText: "확인",
            onConfirm: () => {
              router.push("/calendar");
            },
          });

        return;
      }
    }

    initMusicRecords();

    const checkPremiumStatus = async () => {
      const token = getSpotifyAccessToken();
      if (!token) return;

      try {
        const res = await fetch("https://api.spotify.com/v1/me", {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        const data = await res.json();

        setIsPremium(data.product === "premium");
      } catch (err) {
        console.error("❌ Spotify 사용자 정보 조회 실패:", err);
        setIsPremium(false); // 실패 시 기본값: 무료 계정
      }
    };

    checkPremiumStatus();
  }, []);

  const handleEditButtonClick = () => {
    if (musicRecord) {
      router.push(`/calendar/record?id=${musicRecord.id}`);
    }
  };

  return (
    <div className="flex flex-col items-center justify-center w-full max-w-3xl mx-auto mt-3">
      <div className="w-full max-w-2xl flex justify-between mb-3">
        <h2 className="text-lg text-[#393D3F]">
          {currentYear}년 {currentMonth}월 {currentDay}일
        </h2>
        {calendarPermission === "EDIT" && (
          <button
            className="text-lg text-[#393D3F] bg-[#C8B6FF] rounded-lg px-2"
            onClick={handleEditButtonClick}
          >
            수정하기
          </button>
        )}
      </div>
      <Carousel className="w-full max-w-2xl">
        <CarouselContent>
          {musicRecord?.musics?.map((music) => (
            <CarouselItem key={music.id} className="flex justify-center h-full">
              <Card className="w-full h-full border-1 border-gray-300">
                <CardContent className="flex flex-row items-center p-4">
                  <img
                    src={music.albumImage}
                    alt={music.name}
                    className="w-1/3 h-1/3 object-contain rounded-lg"
                  />

                  <div className="flex flex-col items-start m-10 space-y-4">
                    <div className="space-y-2">
                      <h2 className="text-3xl font-semibold text-[#393D3F]">
                        {music.name}
                      </h2>
                      <p className="text-lg text-[#393D3F]">{music.singer}</p>
                    </div>
                    <div className="space-y-2">
                      <p className="text-lg text-[#393D3F]">{music.genre}</p>
                      <p className="text-lg text-gray-400">
                        {music.releaseDate.replaceAll("-", ".")}
                      </p>
                    </div>
                  </div>
                </CardContent>
              </Card>
            </CarouselItem>
          ))}
        </CarouselContent>
        <CarouselPrevious />
        <CarouselNext />
      </Carousel>

      {/* 메모 영역 */}
      <div className="w-full max-w-2xl mt-6 p-4 rounded-lg shadow">
        <h3 className="text-md text-gray-400">메모</h3>
        <p className="mt-2 text-md text-[#393D3F]">{musicRecord?.memo}</p>
      </div>

      {musicRecord && isPremium === true && (
        <button
          className="mt-6 w-full max-w-2xl bg-green-500 text-white py-2 px-4 rounded-lg"
          onClick={() => {
            sessionStorage.setItem(
              "spotify-music-record",
              JSON.stringify(musicRecord)
            );
            router.push("/calendar/spotify-player/premium");
          }}
        >
          ▶️ 이날의 플레이리스트 듣기
        </button>
      )}

      {musicRecord && isPremium === false && (
        <button
          className="mt-6 w-full max-w-2xl bg-green-500 text-white py-2 px-4 rounded-lg"
          onClick={() => {
            sessionStorage.setItem(
              "spotify-music-record",
              JSON.stringify(musicRecord)
            );
            router.push("/calendar/spotify-player/free");
          }}
        >
          ▶️ Spotify에서 플레이리스트 듣기
        </button>
      )}
      {ModalComponent}
    </div>
  );
}
