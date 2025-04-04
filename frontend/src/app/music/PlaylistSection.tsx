"use client";

import { useEffect, useState } from "react";
import axios from "axios";
import { useGlobalAlert } from "@/components/GlobalAlert";
import PlaylistTrackTable from "./PlaylistTrackTable";

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
    const { setAlert } = useGlobalAlert();

    useEffect(() => {
        fetchPlaylists();
    }, []);

    const fetchPlaylists = async () => {
        try {
            const res = await axios.get(`${SPOTIFY_URL}/playlists`, {
                withCredentials: true,
            });

            const { code, msg, data } = res.data;
            setAlert({ code, message: msg });

            if (code.startsWith("200")) {
                setPlaylists(data);
            }
        } catch (error) {
            setAlert({ code: "500-6", message: "Playlist 정보를 불러올 수 없습니다." });
            throw error;
        }
    };

    const fetchTracks = async (playlistId: string, name: string) => {
        try {
            const res = await axios.get(`${SPOTIFY_URL}/playlists/${playlistId}`, {
                withCredentials: true,
            });

            const { code, msg, data } = res.data;
            setAlert({ code, message: msg });

            if (code.startsWith("200")) {
                setSelectedTracks(data);
                setSelectedPlaylistName(name);
            }
        } catch (error) {
            setAlert({ code: "500-7", message: "해당 Playlist의 트랙을 불러올 수 없습니다." });
            throw error;
        }
    };

    return (
        <section className="mt-10">
            <div className="space-y-1 mb-5">
                <h2 className="text-2xl font-bold">🎧 My Playlist</h2>
                <p className="text-gray-500">유저의 Spotify Playlist</p>
            </div>

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
                <PlaylistTrackTable tracks={selectedTracks} playlistName={selectedPlaylistName} />
            )}
        </section>
    );
}
