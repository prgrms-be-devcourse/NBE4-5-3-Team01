"use client";

import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faTimes } from "@fortawesome/free-solid-svg-icons";
import "./style.css";

export default function MusicList({ selectedTracks, onRemoveTrack }) {
  return (
    <div>
      <h3 className="text-lg font-semibold mb-2">음악 목록</h3>
      <div className="music-list-container">
        {selectedTracks.map((track) => (
          <div key={track.id} className="music-item">
            <img src={track.albumImage} alt={track.name} />
            <button className="remove-button" onClick={() => onRemoveTrack(track.id)}>
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
