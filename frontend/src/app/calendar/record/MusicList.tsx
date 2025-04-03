"use client";

import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faTimes } from "@fortawesome/free-solid-svg-icons";
import "./style.css";

const MAX_MUSIC_COUNT = 20;

export default function MusicList({ selectedTracks, onRemoveTrack }) {
  return (
    <div>
      <div className="flex items-center mb-2">
        <h3 className="text-2xl font-semibold ml-1">음악 목록</h3>
        <span className="text-sm text-gray-500 ml-3">({selectedTracks.length}/{MAX_MUSIC_COUNT})</span>
      </div>

      <div className="music-list-container">
        {selectedTracks.map((track) => (
          <div key={track.id} className="music-item">
            <img src={track.albumImage} alt={track.name} />
            <button
              className="remove-button"
              onClick={() => onRemoveTrack(track.id)}
            >
              <FontAwesomeIcon icon={faTimes} />
            </button>
            <p className="mt-2">{track.name}</p>
            <p className="text-sm text-gray-500">{track.singer}</p>
          </div>
        ))}
      </div>
    </div>
  );
}
