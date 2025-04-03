"use client";

import { useEffect, useState } from "react";
import axios from "axios";

const SPOTIFY_URL = "http://localhost:8080/api/v1/music/spotify";

interface Playlist {
    id: string;
    name: string;
    image: string;
    trackCount: number;
}

interface Track {
    id: string;
    name: string;
    singer: string;
    albumImage: string;
}

export default function PlaylistSection() {
    const [playlists, setPlaylists] = useState<Playlist[]>([]);
    const [selectedTracks, setSelectedTracks] = useState<Track[]>([]);
    const [selectedPlaylistName, setSelectedPlaylistName] = useState("");

    useEffect(() => {
        fetchPlaylists();
    }, []);

    const fetchPlaylists = async () => {
        try {
            const res = await axios.get(`${SPOTIFY_URL}/playlists`, {
                withCredentials: true,
            });
            setPlaylists(res.data.data); // RsData 기반
        } catch (err) {
            console.error("플레이리스트 로딩 실패:", err);
        }
    };

    const fetchTracks = async (playlistId: string, name: string) => {
        try {
            const res = await axios.get(`${SPOTIFY_URL}/playlists/${playlistId}`, {
                withCredentials: true,
            });
            console.log(res);
            setSelectedTracks(res.data.data);
            setSelectedPlaylistName(name);
        } catch (err) {
            console.error("트랙 로딩 실패:", err);
        }
    };

    return (
        <section className="mt-10">
            <h3 className="text-xl font-semibold mb-4">🎧 내 플레이리스트</h3>

            <div className="flex flex-wrap gap-4">
                {playlists.map((playlist) => (
                    <div
                        key={playlist.id}
                        className="cursor-pointer w-40"
                        onClick={() => fetchTracks(playlist.id, playlist.name)}
                    >
                        <img
                            src={playlist.image || "/default.jpg"}
                            alt={playlist.name}
                            className="rounded-lg w-full h-40 object-cover"
                        />
                        <p className="mt-2 font-medium truncate">{playlist.name}</p>
                        <p className="text-sm text-gray-500">{playlist.trackCount}곡</p>
                    </div>
                ))}
            </div>

            {selectedTracks.length > 0 && (
                <div className="mt-8">
                    <h4 className="text-lg font-semibold mb-4">📀 {selectedPlaylistName}의 트랙들</h4>
                    <div className="flex gap-4 overflow-x-auto whitespace-nowrap hide-scrollbar">
                        {selectedTracks.map((track) => (
                            <div key={track.id} className="w-40 flex-shrink-0">
                                <img
                                    src={track.albumImage}
                                    alt={track.name}
                                    className="rounded-lg w-full h-auto"
                                />
                                <p className="text-sm font-medium mt-2 break-words">{track.name}</p>
                                <p className="text-xs text-gray-500">{track.singer}</p>
                            </div>
                        ))}
                    </div>
                </div>
            )}
        </section>
    );
}
