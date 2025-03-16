"use client";

import { useState } from "react";
import { searchSpotifyTracks } from "@/app/utils/spotifyApi";
import "./style.css";

export default function MusicSearch({ onSelectTrack }) {
  const [query, setQuery] = useState("");
  const [results, setResults] = useState([]);
  const [selectedIndex, setSelectedIndex] = useState(-1);

  const handleSearch = async (event) => {
    const keyword = event.target.value;
    setQuery(keyword);
    setSelectedIndex(-1);

    if (keyword.length > 2) {
      const searchResults = await searchSpotifyTracks(keyword);
      setResults(searchResults);
    } else {
      setResults([]);
    }
  };

  const handleKeyDown = (event) => {
    if (event.key === "ArrowDown") {
      setSelectedIndex((prev) => Math.min(prev + 1, results.length - 1));
    } else if (event.key === "ArrowUp") {
      setSelectedIndex((prev) => Math.max(prev - 1, 0));
    } else if (event.key === "Enter" && selectedIndex >= 0) {
      handleSelectTrack(results[selectedIndex]);
    }
  };

  const handleSelectTrack = (track) => {
    onSelectTrack(track);
    setQuery("");
    setResults([]);
    setSelectedIndex(-1);
  };

  return (
    <div className="search-container">
      <input
        type="text"
        value={query}
        onChange={handleSearch}
        onKeyDown={handleKeyDown}
        placeholder="Spotify에서 검색할 곡 또는 가수를 입력하세요."
        className="search-input"
      />
      {results.length > 0 && (
        <ul className="search-results">
          {results.map((track, index) => (
            <li
              key={track.id}
              className={`search-item ${index === selectedIndex ? "selected" : ""}`}
              onClick={() => handleSelectTrack(track)}
            >
              <img src={track.albumImage} alt={track.name} />
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
