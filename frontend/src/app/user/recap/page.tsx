"use client";

import React, { useState } from "react";
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

interface MusicRecord {
  date: string;
  genre: string;
  artist: string;
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

// 예시 데이터 (API 또는 DB로 대체)
const dummyData: MusicRecord[] = [
  { date: "2025-03-07", genre: "Rock", artist: "Artist A" },
  { date: "2025-03-10", genre: "Rock", artist: "Artist A" },
  { date: "2025-03-10", genre: "Pop", artist: "Artist B" },
  { date: "2025-03-11", genre: "Rock", artist: "Artist A" },
  { date: "2025-03-12", genre: "Jazz", artist: "Artist C" },
  { date: "2025-03-12", genre: "Rock", artist: "Artist A" },
  { date: "2025-03-12", genre: "Pop", artist: "Artist B" },
  { date: "2025-03-14", genre: "Pop", artist: "Artist A" },
  { date: "2025-03-15", genre: "Rock", artist: "Artist D" },
  { date: "2025-03-15", genre: "Trot", artist: "Artist D" },
  { date: "2025-03-16", genre: "Trot", artist: "Artist C" },
  { date: "2025-03-16", genre: "Pop", artist: "Artist B" },
  { date: "2025-03-16", genre: "Jazz", artist: "Artist B" },
  { date: "2025-03-16", genre: "Pop", artist: "Artist B" },
  { date: "2025-03-17", genre: "Jazz", artist: "Artist B" },
  { date: "2025-03-18", genre: "Pop", artist: "Artist B" },
  // ... 추가 데이터
];

/* 데이터 필터링 함수
   - weekly: 오늘부터 6일 전(7일간) 데이터만
   - monthly: 이번 달 데이터만 */
const filterRecords = (
  data: MusicRecord[],
  view: "weekly" | "monthly"
): MusicRecord[] => {
  const now = new Date();
  if (view === "weekly") {
    const start = new Date();
    start.setDate(now.getDate() - 6);
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
const getMostListenedGenre = (data: MusicRecord[]): string => {
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

// 가장 많이 기록한 날짜 계산 함수 (필터된 데이터 내에서)
const getMostRecordedDate = (data: MusicRecord[]): string => {
  const dateCount: Record<string, number> = {};
  data.forEach((item) => {
    dateCount[item.date] = (dateCount[item.date] || 0) + 1;
  });
  return Object.entries(dateCount).reduce(
    (maxDate, [date, count]) => (count > dateCount[maxDate] ? date : maxDate),
    Object.keys(dateCount)[0] || ""
  );
};

// 아티스트별 기록 횟수를 계산 후 내림차순 정렬하는 함수
const getFavoriteArtists = (data: MusicRecord[]): ArtistCount[] => {
  const artistCount: Record<string, number> = {};
  data.forEach((item) => {
    artistCount[item.artist] = (artistCount[item.artist] || 0) + 1;
  });
  return Object.entries(artistCount)
    .map(([artist, count]) => ({ artist, count }))
    .sort((a, b) => b.count - a.count);
};

// 장르 분포 계산 함수 (퍼센트로)
const getGenreDistribution = (
  data: MusicRecord[]
): { labels: string[]; percentages: number[] } => {
  const genreCount: Record<string, number> = {};
  data.forEach((item) => {
    genreCount[item.genre] = (genreCount[item.genre] || 0) + 1;
  });
  const total = data.length;
  const labels = Object.keys(genreCount);
  const percentages = labels.map((genre) =>
    Math.round((genreCount[genre] / total) * 100)
  );
  return { labels, percentages };
};

/* 날짜별 기록 개수를 계산하는 함수
   지정된 view(weekly: 최근 7일, monthly: 이번 달)의 전체 범위를
   날짜별로 순서대로 만들어서, 해당 날짜의 기록 수가 없으면 0을 채워줌 */
const getRecordsPerDateWithRange = (
  data: MusicRecord[],
  view: "weekly" | "monthly"
): { dates: string[]; counts: number[] } => {
  const recordMap: Record<string, number> = {};
  data.forEach((item) => {
    recordMap[item.date] = (recordMap[item.date] || 0) + 1;
  });
  const now = new Date();
  let startDate: Date, endDate: Date;
  if (view === "weekly") {
    endDate = now;
    startDate = new Date();
    startDate.setDate(now.getDate() - 6); // 7일간 (오늘 포함)
  } else {
    startDate = new Date(now.getFullYear(), now.getMonth(), 1);
    endDate = new Date(now.getFullYear(), now.getMonth() + 1, 0); // 이번 달 마지막 날
  }
  const dates: string[] = [];
  const counts: number[] = [];
  const current = new Date(startDate);
  while (current <= endDate) {
    const dateStr = current.toISOString().split("T")[0];
    dates.push(dateStr);
    counts.push(recordMap[dateStr] || 0);
    current.setDate(current.getDate() + 1);
  }
  return { dates, counts };
};

const RecapPage = () => {
  const [view, setView] = useState<"weekly" | "monthly">("weekly");

  // 선택된 뷰에 따라 데이터 필터링
  const filteredData = filterRecords(dummyData, view);

  const recap: RecapData = {
    genre: getMostListenedGenre(filteredData),
    date: getMostRecordedDate(filteredData),
    artists: getFavoriteArtists(filteredData),
  };

  // 필터된 데이터를 기준으로 차트 데이터 구성
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
        data: dateDistribution.counts, // 정수값
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
        <div className="content1">
          <section>
            <h1 className="subject">
              내가 좋아하는 <span>아티스트</span>는..?
            </h1>
            <ul>
              {recap.artists.map((item, index) => (
                <li key={index}>
                  <div className="artist">{item.artist}</div>{" "}
                  <div className="count">{item.count}회</div>
                </li>
              ))}
            </ul>
          </section>
        </div>
        <div className="content2">
          <div className="genre">
            <div>
              가장 많이 들은 <span>장르</span>
            </div>
            <div className="chart-container">
              <Pie data={pieChartData} />
            </div>
          </div>
          <div className="date">
            <div>
              가장 많이 기록한 <span>날짜</span>
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
      </div>
    </div>
  );
};

export default RecapPage;
