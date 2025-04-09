"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import axios from "axios";
import { useGlobalAlert } from "@/components/GlobalAlert";

interface Track {
    id: string;
    name: string;
    singer: string;
    albumImage: string;
    releaseDate: string;
}

interface Props {
    tracks: Track[];
    playlistId: string;
    playlistName: string;
    membershipGrade: string;
}

const API_URL = "http://localhost:8080/api/v1";
const ITEMS_PER_PAGE = 5;

export default function PlaylistTrackTable({ tracks, playlistId, playlistName, membershipGrade }: Props) {
    const [page, setPage] = useState(1);

    const totalPages = Math.ceil(tracks.length / ITEMS_PER_PAGE);
    const startIndex = (page - 1) * ITEMS_PER_PAGE;
    const currentTracks = tracks.slice(startIndex, startIndex + ITEMS_PER_PAGE);

    const router = useRouter();
    const { setAlert } = useGlobalAlert();

    const changePage = (newPage: number) => {
        if (newPage >= 1 && newPage <= totalPages) {
            setPage(newPage);
        }
    };

    const handleSelectTrack = async (trackId: string) => {
        try {
            const todayRecordRes = await axios.get(`${API_URL}/calendar/today`, {
                headers: { "Content-Type": "application/json" },
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
            console.error("오늘 기록 확인 오류:", error);
            setAlert({ code: "500-1", message: "오늘 기록 확인에 실패했습니다." });
        }
    };

    const handleAddPlaylistToCalendar = async (playlistId: string) => {
        try {
            const todayRecordRes = await axios.get(`${API_URL}/calendar/today`, {
                headers: { "Content-Type": "application/json" },
                withCredentials: true,
            });

            const todayRecord = todayRecordRes.data;

            if (todayRecord.code === "200-1") {
                router.push(`/calendar/record?id=${todayRecord.data}&playlistId=${playlistId}`);
            } else {
                const { year, month, day } = todayRecord.data;
                router.push(`/calendar/record?year=${year}&month=${month}&day=${day}&playlistId=${playlistId}`);
            }
        } catch (error) {
            console.error("오늘 기록 확인 오류:", error);
            setAlert({ code: "500-2", message: "오늘 기록 확인에 실패했습니다." });
        }
    };

    return (
        <div className="mt-8">
            <div className="flex justify-between items-center mb-2">
                <h2 className="text-xl font-bold text-[#393D3F]">
                    {playlistName} <span className="text-gray-500 text-sm">({tracks.length}곡)</span>
                </h2>
                {tracks.length > 0 && membershipGrade === "premium" && (
                    <button
                        onClick={() => handleAddPlaylistToCalendar(playlistId)}
                        className="px-4 py-2 bg-gradient-to-r from-purple-500 to-indigo-500 text-white font-semibold rounded-lg shadow-md hover:brightness-105 hover:scale-[1.02] transition-transform duration-200"
                    >
                        🎶 플레이리스트 전체 기록하기
                    </button>
                )}
            </div>

            <div className="overflow-x-auto rounded-lg shadow">
                <table className="min-w-full bg-white border border-gray-200">
                    <thead className="bg-gray-100 text-sm text-gray-600 text-left">
                        <tr>
                            <th className="p-3 pl-5 w-10 border-b border-gray-200">#</th>
                            <th className="p-3 border-b border-gray-200">제목</th>
                            <th className="p-3 border-b border-gray-200">아티스트</th>
                            <th className="p-3 border-b border-gray-200">발매일</th>
                        </tr>
                    </thead>
                    <tbody>
                        {currentTracks.length === 0 ? (
                            <tr>
                                <td colSpan={4} className="py-6 text-center text-sm text-gray-500 italic">
                                    🎵 아직 플레이리스트에 음악이 존재하지 않습니다.
                                </td>
                            </tr>
                        ) : (
                            currentTracks.map((track, index) => (
                                <tr
                                    key={track.id}
                                    onClick={() => handleSelectTrack(track.id)}
                                    className="hover:bg-gray-50 border-b border-gray-200 cursor-pointer"
                                >
                                    <td className="p-3 pl-5 text-sm text-gray-700">
                                        {startIndex + index + 1}
                                    </td>
                                    <td className="p-3 flex items-center gap-3 text-sm text-[#393D3F] font-medium">
                                        <img
                                            src={track.albumImage}
                                            alt={track.name}
                                            className="w-10 h-10 rounded-sm object-cover"
                                        />
                                        {track.name}
                                    </td>
                                    <td className="p-3 text-sm text-[#393D3F]">{track.singer}</td>
                                    <td className="p-3 text-sm text-gray-500">
                                        {track.releaseDate ? track.releaseDate.replaceAll("-", ".") : "-"}
                                    </td>
                                </tr>
                            ))
                        )}
                    </tbody>
                </table>
            </div>

            {/* Pagination */}
            {totalPages > 1 && (
                <div className="flex items-center justify-center mt-4 space-x-2">
                    <button
                        onClick={() => changePage(page - 1)}
                        disabled={page === 1}
                        className={`px-3 py-1 rounded ${page === 1 ? "text-gray-300 cursor-not-allowed" : "text-[#393D3F]"
                            }`}
                    >
                        이전
                    </button>
                    <span className="text-sm text-gray-600">
                        {page} / {totalPages}
                    </span>
                    <button
                        onClick={() => changePage(page + 1)}
                        disabled={page === totalPages}
                        className={`px-3 py-1 rounded ${page === totalPages ? "text-gray-300 cursor-not-allowed" : "text-[#393D3F]"
                            }`}
                    >
                        다음
                    </button>
                </div>
            )}
        </div>
    );
}
