import SpotifyWebApi from "spotify-web-api-js";
import { getCookie } from "./cookie";

const spotifyApi = new SpotifyWebApi();
const refreshToken = getCookie("refreshToken");

if (refreshToken) {
  console.log(refreshToken);
  spotifyApi.setAccessToken(refreshToken);
}

export const searchSpotifyTracks = async (query: string) => {
  if (!query) return [];

  try {
    const response = await spotifyApi.searchTracks(query, { market: "KR", limit: 5 });

    return response.tracks.items.map((track) => ({
      id: track.id,
      name: track.name,
      singer: track.artists.map((artist) => artist.name).join(", "),
      albumImage: track.album.images[0]?.url,
    }));
  } catch (error) {
    console.error("Spotify 검색 실패:", error);
    return [];
  }
};
