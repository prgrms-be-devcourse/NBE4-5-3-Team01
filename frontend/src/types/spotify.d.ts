// types/spotify.d.ts

export {};

declare global {
  interface Window {
    onSpotifyWebPlaybackSDKReady: () => void;
    Spotify: typeof Spotify;
  }

  namespace Spotify {
    interface PlayerInit {
      name: string;
      getOAuthToken: (cb: (token: string) => void) => void;
      volume?: number;
    }

    interface Player {
      connect(): Promise<boolean>;
      disconnect(): void;
      addListener(event: string, callback: (data: any) => void): boolean;
      removeListener(event: string, callback?: (data: any) => void): boolean;

      getCurrentState(): Promise<PlaybackState | null>;
      pause(): Promise<void>;
      resume(): Promise<void>;
      togglePlay(): Promise<void>;
      seek(position_ms: number): Promise<void>;
      setVolume(volume: number): Promise<void>;
    }

    interface PlaybackState {
      position: number;
      duration: number;
      paused: boolean;
      track_window: {
        current_track: Track;
        previous_tracks: Track[];
        next_tracks: Track[];
      };
    }

    interface Track {
      uri: string;
      id: string;
      name: string;
      artists: { name: string }[];
    }
  }
}
