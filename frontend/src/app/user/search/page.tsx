"use client"

import React, { useState, useEffect } from "react";
import axios from "axios";
import { useParams, useRouter } from "next/navigation";

const SearchPage = () => {
  const [activeTab, setActiveTab] = useState("following");
  const [nickName, setNickName] = useState("");
  const { id } = useParams<{ id: string }>();
  const [users, setUsers] = useState([]);
  const router = useRouter();

  const searchUsers = async () => {
    try {
      const response = await axios.get(`http://localhost:8080/api/v1/user/search?q=${nickName}`,
        { withCredentials: true }
      );
      setUsers(response.data);
      console.log(users);
    } catch (error) {
      console.error("Error fetching users:", error);
    }
  };

  useEffect(() => {
    searchUsers();
  }, [activeTab]);

  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter") {
      searchUsers();
    }
  };

  const toggleFollow = async (e: React.MouseEvent, userId: string, isFollowing: boolean) => {
    e.stopPropagation();

    try {
      if (isFollowing) {
        await axios.delete(`http://localhost:8080/api/v1/follows/${userId}`, {
          withCredentials: true
        });
      } else {
        await axios.post(
          `http://localhost:8080/api/v1/follows/${userId}`,
          {},
          { withCredentials: true }
        );
      }

      setUsers((prevUsers) =>
        prevUsers.map((user) =>
          user.user.id === userId
            ? { ...user, isFollowing: !isFollowing }
            : user
        )
      );
    } catch (error) {
      console.error("Error updating follow status:", error);
    }
  };

  const handleUserClick = (userId: string) => {
    router.push(`/calendar?userId=${userId}`);
  };

  return (
    <div className="flex flex-col items-center min-h-screen py-10">
      {/* 검색창 */}
      <div className="w-full max-w-lg">
        <input
          type="text"
          placeholder="사용자 검색..."
          className="w-full px-5 py-3 text-lg text-gray-800 bg-white border border-[#B8C0FF] rounded-lg shadow-md focus:outline-none focus:ring-2 focus:ring-[#E7C6FF] transition"
          onChange={(e) => setNickName(e.target.value)}
          onKeyDown={handleKeyDown}
        />
      </div>

      {/* 검색 결과 리스트 */}
      <div className="w-full max-w-2xl mt-8 space-y-4">
        {users.map((user, index) => {
          const isFollowing = user.isFollowing;
          const isFollower = user.isFollower;
          let buttonText = "팔로우";

          if (isFollowing) buttonText = "팔로잉";
          else if (isFollower) buttonText = "맞팔로우";

          return (
            <div
              key={index}
              className="flex justify-between items-center p-4 bg-white rounded-lg shadow hover:shadow-lg transition cursor-pointer"
              onClick={() => handleUserClick(user.user.id)}
            >
              <span className="text-lg font-medium text-gray-800">{user.user.name}</span>
              <button
                className={`px-4 py-2 text-sm font-medium rounded-lg transition ${isFollowing
                  ? "bg-[#BBD0FF] text-gray-800 hover:bg-[#B8C0FF]" // 팔로잉 → 연한 블루
                  : isFollower
                    ? "bg-[#FFD6FF] text-gray-800 hover:bg-[#E7C6FF]" // 맞팔로우 → 연한 핑크
                    : "bg-[#C8B6FF] text-white hover:bg-[#B8C0FF]" // 기본 팔로우 버튼
                  }`}
                onClick={(e) => toggleFollow(e, user.user.id, isFollowing)}
              >
                {buttonText}
              </button>
            </div>
          );
        })}
      </div>
    </div>
  );
};

export default SearchPage;