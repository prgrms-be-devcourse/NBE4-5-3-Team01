"use client";

import React, { useState, useEffect } from "react";
import axios from "axios";
import "./recap.css";

import { Pie, Line, Bar } from "react-chartjs-2";
import {
  Chart as ChartJS,
  BarElement,
  ArcElement,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
} from "chart.js";

ChartJS.register(
  BarElement,
  ArcElement,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend
);

// MusicRecordDto 인터페이스 (서버에서 전달받은 데이터 구조)
interface MusicRecordDto {
  calendarDateId: number;
  date: string; // ISO 형식 (YYYY-MM-DD)
  memo: string;
  musicId: string;
  musicName: string;
  singer: string;
  singerId: string;
  releaseDate: string;
  albumImage: string;
  genre: string;
}

interface ArtistCount {
  artist: string;
  count: number;
}

interface RecapData {
  genre: string;
  date: string;
  artists: ArtistCount[];
}

/* 클라이언트측 필터링 함수
   API가 이미 기간에 맞게 필터링해줄 수 있지만, 혹시 모를 경우 대비 */
const filterRecords = (
  data: MusicRecordDto[],
  view: "weekly" | "monthly"
): MusicRecordDto[] => {
  const now = new Date();
  if (view === "weekly") {
    const start = new Date();
    start.setDate(now.getDate() - 7);
    return data.filter((item) => {
      const itemDate = new Date(item.date);
      return itemDate >= start && itemDate <= now;
    });
  } else {
    return data.filter((item) => {
      const itemDate = new Date(item.date);
      return (
        itemDate.getFullYear() === now.getFullYear() &&
        itemDate.getMonth() === now.getMonth()
      );
    });
  }
};

// 가장 많이 들은 장르 계산 함수
const getMostListenedGenre = (data: MusicRecordDto[]): string => {
  const genreCount: Record<string, number> = {};

  data.forEach((item) => {
    const genres = item.genre
      ? item.genre.split(",").map((g) => g.trim())
      : ["None"]; // 장르가 없으면 "None" 추가

    genres.forEach((genre) => {
      genreCount[genre] = (genreCount[genre] || 0) + 1;
    });
  });

  return Object.entries(genreCount).reduce(
    (maxGenre, [genre, count]) =>
      count > genreCount[maxGenre] ? genre : maxGenre,
    Object.keys(genreCount)[0] || ""
  );
};

// 가장 많이 기록한 날짜 계산 함수
const getMostRecordedDate = (data: MusicRecordDto[]): string => {
  const dateCount: Record<string, number> = {};
  data.forEach((item) => {
    dateCount[item.date] = (dateCount[item.date] || 0) + 1;
  });
  return Object.entries(dateCount).reduce(
    (maxDate, [date, count]) => (count > dateCount[maxDate] ? date : maxDate),
    Object.keys(dateCount)[0] || ""
  );
};

// 특정 날짜의 기록 개수 찾기
const getMostRecordedDateCount = (
  data: MusicRecordDto[],
  targetDate: string
): number => {
  return data.filter((item) => item.date === targetDate).length;
};

// 아티스트별 기록 횟수를 계산 (여기서는 singer를 아티스트로 사용)
const getFavoriteArtists = (data: MusicRecordDto[]): ArtistCount[] => {
  const artistCount: Record<string, number> = {};
  data.forEach((item) => {
    artistCount[item.singer] = (artistCount[item.singer] || 0) + 1;
  });
  return Object.entries(artistCount)
    .map(([artist, count]) => ({ artist, count }))
    .sort((a, b) => b.count - a.count);
};

// 장르 분포 계산 함수 (퍼센트)
const getGenreDistribution = (
  data: MusicRecordDto[]
): { labels: string[]; percentages: number[] } => {
  const genreCount: Record<string, number> = {};

  data.forEach((item) => {
    const genres = item.genre
      ? item.genre.split(",").map((g) => g.trim())
      : ["None"]; // 장르가 없으면 "None" 추가

    genres.forEach((genre) => {
      genreCount[genre] = (genreCount[genre] || 0) + 1;
    });
  });

  const total = Object.values(genreCount).reduce(
    (sum, count) => sum + count,
    0
  );
  const labels = Object.keys(genreCount);
  const percentages = labels.map((genre) =>
    total ? Math.round((genreCount[genre] / total) * 100) : 0
  );

  return { labels, percentages };
};

// YYYY-MM-DD 형식으로 날짜를 반환하는 헬퍼 함수 (로컬 타임존 기준)
const formatDate = (date: Date): string => {
  const year = date.getFullYear();
  const month = (date.getMonth() + 1).toString().padStart(2, "0");
  const day = date.getDate().toString().padStart(2, "0");
  return `${year}-${month}-${day}`;
};

/* 날짜별 기록 개수를 계산하는 함수
   지정된 기간(주간: 최근 7일, 월간: 이번 달)의 범위를 만들어 해당 날짜의 기록 수를 채워줌 */
const getRecordsPerDateWithRange = (
  data: MusicRecordDto[],
  view: "weekly" | "monthly"
): { dates: string[]; counts: number[] } => {
  const recordMap: Record<string, number> = {};
  data.forEach((item) => {
    // API에서 받은 날짜 문자열 그대로 사용
    const dateStr = item.date;
    recordMap[dateStr] = (recordMap[dateStr] || 0) + 1;
  });

  const now = new Date();
  let startDate: Date, endDate: Date;
  if (view === "weekly") {
    endDate = now;
    startDate = new Date();
    startDate.setDate(now.getDate() - 6);
  } else {
    startDate = new Date(now.getFullYear(), now.getMonth(), 1);
    endDate = new Date(now.getFullYear(), now.getMonth() + 1, 0);
  }

  const dates: string[] = [];
  const counts: number[] = [];
  const current = new Date(startDate);

  while (current <= endDate) {
    const dateStr = formatDate(current); // formatDate는 로컬 날짜를 "YYYY-MM-DD"로 변환
    dates.push(dateStr);
    counts.push(recordMap[dateStr] || 0);
    current.setDate(current.getDate() + 1);
  }

  return { dates, counts };
};

const RecapPage = () => {
  const [view, setView] = useState<"weekly" | "monthly">("weekly");
  const [records, setRecords] = useState<MusicRecordDto[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [selectedTrack, setSelectedTrack] = useState<MusicRecordDto | null>(
    null
  );
  const [membershipGrade, setMembershipGrade] = useState<string | null>(null);

  // view 값이 변경되거나 페이지 마운트 시에 API 호출
  useEffect(() => {
    const fetchData = async () => {
      try {
        const res = await axios.get("http://localhost:8080/api/v1/membership/my", {
          withCredentials: true,
        });
        const grade = res.data.data?.grade || "basic";
        setMembershipGrade(grade);

        if (grade === "premium") {
          await fetchRecords();
        }
      } catch (err) {
        console.error("멤버십 정보를 가져오지 못했어요.");
        setMembershipGrade("basic");
      }
    };

    const fetchRecords = async () => {
      setLoading(true);
      setError(null);
      try {
        const today = new Date();
        let startDate: string;
        let endDate: string;

        if (view === "weekly") {
          const start = new Date();
          start.setDate(today.getDate() - 6);
          startDate = start.toISOString().split("T")[0];
          endDate = today.toISOString().split("T")[0];
        } else {
          const firstDay = new Date(today.getFullYear(), today.getMonth(), 1);
          const lastDay = new Date(
            today.getFullYear(),
            today.getMonth() + 1,
            0
          );
          startDate = firstDay.toISOString().split("T")[0];
          endDate = lastDay.toISOString().split("T")[0];
        }

        const response = await axios.get("http://localhost:8080/api/v1/recap", {
          params: { startDate, endDate },
          withCredentials: true,
        });
        setRecords(response.data.data);
      } catch (err) {
        console.error(err);
        setError("음악 기록을 불러오는 중 오류가 발생했습니다.");
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [view]);

  // API가 이미 기간별 데이터를 반환하지만, 혹시 추가 필터링이 필요한 경우 사용
  const filteredData = filterRecords(records, view);

  // Recap 데이터 계산
  const recap: RecapData = {
    genre: getMostListenedGenre(filteredData),
    date: getMostRecordedDate(filteredData),
    artists: getFavoriteArtists(filteredData),
  };

  const mostRecordedDateCount = getMostRecordedDateCount(
    filteredData,
    recap.date
  );

  const genreDistribution = getGenreDistribution(filteredData);
  const pieChartData = {
    labels: genreDistribution.labels,
    datasets: [
      {
        label: "Genre Distribution (%)",
        data: genreDistribution.percentages,
        backgroundColor: [
          "#FF6384",
          "#36A2EB",
          "#FFCE56",
          "#4BC0C0",
          "#9966FF",
          "#FF9F40",
        ],
        hoverBackgroundColor: [
          "#FF6384",
          "#36A2EB",
          "#FFCE56",
          "#4BC0C0",
          "#9966FF",
          "#FF9F40",
        ],
      },
    ],
  };

  const dateDistribution = getRecordsPerDateWithRange(filteredData, view);
  const lineChartOptions = {
    scales: {
      x: {
        ticks: {
          callback: function (value: any): string {
            const dateStr = dateDistribution.dates[value]; // "YYYY-MM-DD"
            const [, month, day] = dateStr.split("-");
            return `${month}-${day}`; // "MM-DD"
          },
          font: {
            weight: 700,
            size: 14,
          },
          color: "#222222",
        },
      },
      y: {
        beginAtZero: true,
        ticks: {
          stepSize: 1,
          callback: function (tickValue: string | number) {
            return Number(tickValue).toFixed(0);
          },
          font: {
            weight: 700,
            size: 14,
          },
          color: "#222222",
        },
      },
    },
  };
  const lineChartData = {
    labels: dateDistribution.dates,
    datasets: [
      {
        label: "기록 개수",
        data: dateDistribution.counts,
        fill: false,
        borderColor: "#36A2EB",
        backgroundColor: "#36A2EB",
        tension: 0.1,
      },
    ],
  };

  const pieChartOptions = {
    plugins: {
      legend: {
        onClick: () => null, // 범례 클릭 이벤트 막기
      },
    },
  };

  const totalRecordCount = filteredData.length;

  const barColors = [
    "#FF6384",
    "#36A2EB",
    "#FFCE56",
    "#4BC0C0",
    "#9966FF",
    "#FF9F40",
    "#8BC34A",
    "#E91E63",
    "#00BCD4",
    "#9C27B0",
    "#CDDC39",
  ];

  const topArtists = recap.artists.slice(0, 7);

  const barChartData = {
    labels: topArtists.map((item) => item.artist),
    datasets: [
      {
        label: "기록 횟수",
        data: topArtists.map((item) => item.count),
        backgroundColor: barColors.slice(0, topArtists.length),
        borderWidth: 1,
      },
    ],
  };

  const barChartOptions = {
    indexAxis: "y" as const, // 가로 막대
    scales: {
      x: {
        beginAtZero: true,
        ticks: {
          stepSize: 1,
          precision: 0,
          font: {
            weight: 700,
            size: 14,
          },
          color: "#222222",
        },
        title: {
          display: true,
          text: "횟수",
          font: {
            weight: 700,
            size: 14,
          },
          color: "#222222",
        },
      },
      y: {
        ticks: {
          font: {
            weight: 700,
            size: 14,
          },
          color: "#222222",
        },
      },
    },
    plugins: {
      legend: {
        labels: {
          font: {
            weight: 700,
            size: 14,
          },
          color: "#222222",
        },
      },
    },
  };

  const topArtist = recap.artists[0]?.artist;

  const topArtistTracks = filteredData.filter(
    (item) => item.singer === topArtist
  );

  if (membershipGrade !== "premium") {
    return (
      <div className="flex flex-col items-center justify-center min-h-screen text-center px-4 py-20 bg-white">
        <h1 className="text-2xl md:text-3xl font-bold text-gray-800 mb-4">
          프리미엄 회원만 이용할 수 있는 기능이에요!
        </h1>
        <p className="text-gray-600 mb-6">
          통계 기능을 사용하시려면 프리미엄 요금제로 업그레이드 해보세요 😊
        </p>
        <button
          onClick={() => (window.location.href = "/membership")}
          className="bg-purple-600 hover:bg-purple-700 text-white px-6 py-2 rounded-lg transition"
        >
          프리미엄 요금제 보러가기 →
        </button>
      </div>
    );
  }

  return (
    <div id="recap-bar">
      <div id="container">
        <div className="header">
          <div>
            <div className="title">
              <h1>Music Calendar Recap</h1>
            </div>
            <div className="explan">
              <p>회원님이 들었던 노래 Recap</p>
            </div>
          </div>
          <div className="button">
            <div
              style={{ display: "flex", justifyContent: "center", gap: "10px" }}
            >
              <button
                onClick={() => setView("weekly")}
                className={`recap-button ${view === "weekly" ? "active" : ""}`}
              >
                Weekly
              </button>
              <button
                onClick={() => setView("monthly")}
                className={`recap-button ${view === "monthly" ? "active" : ""}`}
              >
                Monthly
              </button>
            </div>
          </div>
        </div>
        {loading && <div>음악 기록을 불러오는 중...</div>}
        {error && <div>{error}</div>}
        {/* 로딩과 에러가 없고, 데이터가 없는 경우 메시지 출력 */}
        {!loading && !error && filteredData.length === 0 && (
          <div className="no-records">
            <p>음악 기록을 시작해보세요!!</p>
          </div>
        )}
        {/* 로딩과 에러가 없고, 데이터가 있을 때 기존 통계 콘텐츠 렌더링 */}
        {!loading && !error && filteredData.length > 0 && (
          <>
            <div className="content1">
              <div className="like-artist">
                <section>
                  <h1 className="subject">
                    내가 좋아하는 <span>아티스트</span>는..?
                  </h1>
                  <div>
                    <Bar data={barChartData} options={barChartOptions} />
                  </div>
                </section>
                <div
                  className="artist-count"
                  style={{
                    marginBottom: "20px",
                    fontWeight: "bold",
                    fontSize: "30px",
                    textAlign: "center",
                  }}
                >
                  총 {view === "weekly" ? "일주일에 " : "이번 달에 "}
                  <span>{recap.artists.length}</span>명의 아티스트를 기록했고,{" "}
                  <span>{totalRecordCount}</span>개의 음악을 기록했어요!
                </div>
              </div>
              {topArtist && topArtistTracks.length > 0 && (
                <div className="top-artist-tracks">
                  <h2 className="track-section-title">
                    🎤 회원님이 가장 좋아하는 {topArtist}의 등록한 노래
                  </h2>

                  {/* 캐러셀 또는 그리드 방식 */}
                  <div className="track-carousel">
                    {topArtistTracks.map((track, index) => (
                      <div
                        key={index}
                        className="track-card"
                        onClick={() => setSelectedTrack(track)}
                      >
                        <img
                          src={track.albumImage}
                          alt={track.musicName}
                          className="album-image"
                        />
                        <div className="music-name">{track.musicName}</div>
                        <div className="record-date">{track.date}</div>
                      </div>
                    ))}
                  </div>

                  {/* 모달 */}
                  {selectedTrack && (
                    <div
                      className="modal-overlay"
                      onClick={() => setSelectedTrack(null)}
                    >
                      <div
                        className="modal-content"
                        onClick={(e) => e.stopPropagation()}
                      >
                        <img
                          src={selectedTrack.albumImage}
                          alt={selectedTrack.musicName}
                        />
                        <h3>{selectedTrack.musicName}</h3>
                        <p>아티스트: {selectedTrack.singer}</p>
                        <p>등록 날짜: {selectedTrack.date}</p>
                        <button onClick={() => setSelectedTrack(null)}>
                          닫기
                        </button>
                      </div>
                    </div>
                  )}
                </div>
              )}
            </div>
            <div className="content2">
              <div className="genre">
                <div>
                  내가 많이 들은 <span>장르</span>
                </div>
                <div className="chart-container" style={{ width: "600px" }}>
                  <Pie data={pieChartData} options={pieChartOptions} />
                </div>
              </div>
              <div className="date">
                <div>
                  내가 많이 기록한 <span>날짜</span>
                </div>
                <div className="chart-container">
                  <Line data={lineChartData} options={lineChartOptions} />
                </div>
                <div>
                  {recap.date} <br />
                  <span>{mostRecordedDateCount}</span>개의 노래를 기록했어요!!
                </div>
              </div>
            </div>
          </>
        )}
      </div>
    </div>
  );
};

export default RecapPage;
