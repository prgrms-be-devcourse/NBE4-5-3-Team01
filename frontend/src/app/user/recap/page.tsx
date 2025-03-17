"use client";

import React, { useState, useEffect } from "react";
import axios from "axios";
import "./recap.css";

import { Pie, Line } from "react-chartjs-2";
import {
  Chart as ChartJS,
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
    genreCount[item.genre] = (genreCount[item.genre] || 0) + 1;
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
    genreCount[item.genre] = (genreCount[item.genre] || 0) + 1;
  });
  const total = data.length;
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

  // view 값이 변경되거나 페이지 마운트 시에 API 호출
  useEffect(() => {
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

        const response = await axios.get<MusicRecordDto[]>(
          "http://localhost:8080/api/v1/recap",
          {
            params: { startDate, endDate },
            withCredentials: true,
          }
        );
        setRecords(response.data);
      } catch (err) {
        console.error(err);
        setError("음악 기록을 불러오는 중 오류가 발생했습니다.");
      } finally {
        setLoading(false);
      }
    };

    fetchRecords();
  }, [view]);

  // API가 이미 기간별 데이터를 반환하지만, 혹시 추가 필터링이 필요한 경우 사용
  const filteredData = filterRecords(records, view);

  // Recap 데이터 계산
  const recap: RecapData = {
    genre: getMostListenedGenre(filteredData),
    date: getMostRecordedDate(filteredData),
    artists: getFavoriteArtists(filteredData),
  };

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
      y: {
        beginAtZero: true,
        ticks: {
          stepSize: 1,
          callback: function (tickValue: string | number) {
            return Number(tickValue).toFixed(0);
          },
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

  return (
    <div id="recap-bar">
      <div id="container">
        <div className="header">
          <div>
            <div className="title">
              <h1>Music Calendar Recap</h1>
            </div>
            <div className="explan">
              <p>user님이 들었던 음악들의 Recap</p>
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
        {!loading && !error && (
          <>
            <div className="content1">
              <section>
                <h1 className="subject">
                  내가 좋아하는 <span>아티스트</span>는..?
                </h1>
                <ul>
                  {recap.artists.map((item, index) => (
                    <li key={index}>
                      <div className="artist">{item.artist}</div>
                      <div className="count">{item.count}회</div>
                    </li>
                  ))}
                </ul>
              </section>
            </div>
            <div className="content2">
              <div className="genre">
                <div>
                  내가 많이 들은 <span>장르</span>
                </div>
                <div className="chart-container">
                  <Pie data={pieChartData} />
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
                  <span>00</span>개의 노래를 기록했어요!!
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
