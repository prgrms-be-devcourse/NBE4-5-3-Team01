"use client"; // 클라이언트 컴포넌트임을 명시

import { useState } from "react";

const TestApiPage: React.FC = () => {
  const [responseData, setResponseData] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  const handleApiRequest = async () => {
    setLoading(true);
    setError(null);

    const accessToken = localStorage.getItem("accessToken"); // 로컬 스토리지에서 액세스 토큰 가져오기

    if (!accessToken) {
      setError("No access token found");
      setLoading(false);
      return;
    }

    try {
      const response = await fetch(
        "http://localhost:8080/api/v1/user/testApi",
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${accessToken}`, // 액세스 토큰을 Authorization 헤더에 추가
          },
        }
      );

      if (!response.ok) {
        throw new Error("Failed to fetch data");
      }

      const data = await response.json();
      setResponseData(JSON.stringify(data, null, 2)); // Format response JSON
    } catch (err) {
      setError("Error fetching data: " + err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <h1>Test API Request</h1>
      <button onClick={handleApiRequest} disabled={loading}>
        {loading ? "Loading..." : "Fetch Data"}
      </button>

      {error && <p style={{ color: "red" }}>{error}</p>}
      {responseData && (
        <div>
          <h2>API Response</h2>
          <pre>{responseData}</pre>
        </div>
      )}
    </div>
  );
};

export default TestApiPage;
