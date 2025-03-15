import SpotifyWebApi from "spotify-web-api-js";

const spotifyApi = new SpotifyWebApi();
const accessToken = localStorage.getItem("spotifyToken");

if (accessToken) {
  spotifyApi.setAccessToken(accessToken);
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
