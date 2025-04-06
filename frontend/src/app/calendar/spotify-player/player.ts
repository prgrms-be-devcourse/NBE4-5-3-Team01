// player.ts

export const loadSpotifyPlayer = (
  accessToken: string,
  onReady: (player: Spotify.Player, deviceId: string) => void
) => {
  if (typeof window === "undefined") return;

  const scriptTagId = "spotify-player-script";

  const initializePlayer = () => {
    console.log("Initializing Spotify Player...");
    const player = new (window as any).Spotify.Player({
      name: "My Web Player",
      getOAuthToken: (cb: (token: string) => void) => cb(accessToken),
      volume: 0.5,
    });

    player.addListener("ready", ({ device_id }: { device_id: string }) => {
      console.log("✅ player 내부 ready 이벤트:", device_id);
      onReady(player, device_id);
    });

    player.addListener(
      "initialization_error",
      ({ message }: { message: string }) =>
        console.error("Init Error:", message)
    );
    player.addListener(
      "authentication_error",
      ({ message }: { message: string }) =>
        console.error("Auth Error:", message)
    );
    player.addListener("account_error", ({ message }: { message: string }) =>
      console.error("Account Error:", message)
    );
    player.addListener("playback_error", ({ message }: { message: string }) =>
      console.error("Playback Error:", message)
    );

    player.connect();
  };

  // 👉 이미 SDK가 로드된 경우 (router.push 이후 등)
  if ((window as any).Spotify) {
    console.log("🟢 SDK 이미 로드됨 → initializePlayer 바로 실행");
    initializePlayer();
    return;
  }

  // 👉 아직 SDK가 로드되지 않은 경우
  if (!document.getElementById(scriptTagId)) {
    console.log("🟡 SDK 아직 로드 안됨 → script 삽입");
    const script = document.createElement("script");
    script.id = scriptTagId;
    script.src = "https://sdk.scdn.co/spotify-player.js";
    script.async = true;
    document.body.appendChild(script);
  }

  // 👉 SDK가 준비되면 실행할 콜백
  window.onSpotifyWebPlaybackSDKReady = () => {
    initializePlayer();
  };
};
