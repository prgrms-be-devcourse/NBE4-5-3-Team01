import axios from "axios";
import SpotifyWebApi from "spotify-web-api-js";
import { preprocessKoreanQuery } from "@/app/utils/koreanPreprocess";

const spotifyApi = new SpotifyWebApi();

const fetchSpotifyToken = async () => {
  try {
    const response = await axios.get("http://localhost:8080/api/v1/user/spotify-token", {
      withCredentials: true,
    });
    const spotifyToken = response.data;

    if (spotifyToken) {
      spotifyApi.setAccessToken(spotifyToken);
      console.log("✅ Spotify Token 설정 완료!");
    }
  } catch (error) {
    console.error("❌ Spotify Token 가져오기 실패:", error);
  }
};
fetchSpotifyToken();

export const searchSpotifyTracks = async (query: string) => {
  if (!query) return [];

  try {
    const keyword = preprocessKoreanQuery(query);
    const response = await spotifyApi.searchTracks(keyword, { market: "KR", limit: 5 });

    return response.tracks.items.map((track) => ({
      id: track.id,
      name: track.name,
      singer: track.artists.map((artist) => artist.name).join(", "),
      singerId: track.artists.map((artist) => artist.id).join(", "),
      releaseDate: track.album.release_date,
      albumImage: track.album.images[0]?.url,
      genre: null,
    }));
  } catch (error) {
    console.error("Spotify 검색 실패:", error);
    return [];
  }
};
