"use client";

import { useEffect, useState } from "react";
import axios from "axios";
import { useGlobalAlert } from "@/components/GlobalAlert";
import PlaylistTrackTable from "./PlaylistTrackTable";

const API_URL = "http://localhost:8080/api/v1";
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
    const [selectedPlaylistId, setSelectedPlaylistId] = useState("");
    const [selectedPlaylistName, setSelectedPlaylistName] = useState("");
    const [membershipGrade, setMembershipGrade] = useState<string>("");
    const { setAlert } = useGlobalAlert();

    useEffect(() => {
        fetchPlaylists();
    }, []);

    const fetchPlaylists = async () => {
        try {
            const res = await axios.get(`${SPOTIFY_URL}/playlist`, {
                withCredentials: true,
            });

            const { code, msg, data } = res.data;
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
            const playlistRes = await axios.get(`${SPOTIFY_URL}/playlist/${playlistId}`, {
                withCredentials: true,
            });

            const memberRes = await axios.get(`${API_URL}/membership/my`, {
                withCredentials: true,
            });

            const { code, msg, data } = playlistRes.data;
            if (code.startsWith("200")) {
                setSelectedTracks(data);
                setSelectedPlaylistId(playlistId);
                setSelectedPlaylistName(name);
                setMembershipGrade(memberRes.data.data.grade);
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

            {selectedPlaylistId && (
                <PlaylistTrackTable tracks={selectedTracks} playlistId={selectedPlaylistId} playlistName={selectedPlaylistName} membershipGrade={membershipGrade} />
            )}
        </section>
    );
}
