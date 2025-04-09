"use client";

import { formatDistanceToNow } from "date-fns";
import "./style.css";

type Props = {
  selectedTracks: any[];
  onRemoveTrack: (trackId: string) => void;
};

export default function RecentlyPlayedList({
  selectedTracks,
  onRemoveTrack,
}: Props) {
  if (selectedTracks.length === 0) return;

  return (
    <div className="music-list-container">
      {selectedTracks.map((track) => (
        <a
          key={track.id}
          // href={track.trackUrl}
          target="_blank"
          rel="noopener noreferrer"
          className="recently-music-item bg-white rounded-xl 
              shadow hover:scale-105 transition-transform duration-300 overflow-hidden"
        >
          <img src={track.albumImage} alt={track.name} />
          <button
            className="remove-button"
            onClick={() => onRemoveTrack(track.id)}
          >
            ×
          </button>
          <p className="mt-2 text-base font-medium truncate">{track.name}</p>
          <p className="text-sm text-gray-500 truncate">{track.singer}</p>
          <p className="text-xs text-gray-400 mt-1">
            {formatDistanceToNow(new Date(track.playedAt), {
              addSuffix: true,
            })}
          </p>
        </a>
      ))}
    </div>
  );
}
