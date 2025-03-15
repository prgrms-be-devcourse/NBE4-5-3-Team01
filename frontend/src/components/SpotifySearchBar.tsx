"use client";

import { useState } from "react";
import { searchSpotifyTracks } from "@/utils/spotifyApi";

export default function SpotifySearchBar() {
  const [query, setQuery] = useState("");
  const [results, setResults] = useState([]);

  const handleSearch = async (event) => {
    const keyword = event.target.value;
    setQuery(keyword);

    if (keyword.length > 2) {
      const searchResults = await searchSpotifyTracks(keyword);
      setResults(searchResults);
    } else {
      setResults([]);
    }
  };

  return (
    <div className="relative w-full max-w-md">
      <input
        type="text"
        value={query}
        onChange={handleSearch}
        placeholder="노래 제목을 입력하세요..."
        className="w-full p-2 border border-gray-300 rounded-md focus:outline-none focus:ring focus:ring-blue-400"
      />
      {results.length > 0 && (
        <ul className="absolute left-0 w-full bg-white border border-gray-300 mt-1 rounded-md shadow-lg z-10">
          {results.map((track) => (
            <li
              key={track.id}
              className="p-2 cursor-pointer hover:bg-gray-200 flex items-center"
              onClick={() => alert(`선택한 노래: ${track.name} - ${track.singer}`)}
            >
              <img src={track.albumImage} alt={track.name} className="w-10 h-10 mr-2 rounded" />
              <div>
                <p className="font-medium">{track.name}</p>
                <p className="text-sm text-gray-500">{track.singer}</p>
              </div>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
