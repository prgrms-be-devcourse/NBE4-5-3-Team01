"use client";

import { useEffect, useState } from "react";
import axios from "axios";
import Image from "next/image";
import PlaylistTrackTable from "./PlaylistTrackTable";
import { useHandleApiError } from "@/lib/useHandleApiError";

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
  releaseDate: string;
}

export default function PlaylistSection() {
  const [playlists, setPlaylists] = useState<Playlist[]>([]);
  const [selectedTracks, setSelectedTracks] = useState<Track[]>([]);
  const [selectedPlaylistId, setSelectedPlaylistId] = useState("");
  const [selectedPlaylistName, setSelectedPlaylistName] = useState("");
  const [membershipGrade, setMembershipGrade] = useState<string>("");
  const { handleApiError } = useHandleApiError();

  useEffect(() => {
    fetchPlaylists();
  }, []);

  const fetchPlaylists = async () => {
    try {
      const res = await axios.get(`${SPOTIFY_URL}/playlist`, {
        withCredentials: true,
      });

      const { code, data } = res.data;
      if (code.startsWith("200")) {
        setPlaylists(data);
      }
    } catch (error) {
      handleApiError(error);
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

      const { code, data } = playlistRes.data;
      if (code.startsWith("200")) {
        setSelectedTracks(data);
        setSelectedPlaylistId(playlistId);
        setSelectedPlaylistName(name);
        setMembershipGrade(memberRes.data.data.grade);
      }
    } catch (error) {
      handleApiError(error);
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
            <Image
              src={playlist.image || "/default.jpg"}
              alt={playlist.name}
              width={160}
              height={160}
              className="rounded-lg w-full h-40 object-cover"
            />
            <p className="mt-2 font-medium truncate">{playlist.name}</p>
            <p className="text-sm text-gray-500">{playlist.trackCount}곡</p>
          </div>
        ))}
      </div>

      {selectedPlaylistId && (
        <PlaylistTrackTable
          tracks={selectedTracks}
          playlistId={selectedPlaylistId}
          playlistName={selectedPlaylistName}
          membershipGrade={membershipGrade}
        />
      )}
    </section>
  );
}
