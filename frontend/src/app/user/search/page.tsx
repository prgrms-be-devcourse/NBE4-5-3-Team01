"use client"

import Image from 'next/image'
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
      setUsers(response.data.data);
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
      if (isFollowing == "ACCEPT" || isFollowing == "PENDING") {
        await axios.delete(`http://localhost:8080/api/v1/follows/delete/${userId}`, {
          withCredentials: true,
          headers: {
            "Content-Type": "application/json"
          }
        });
      } else {
        await axios.post(
          `http://localhost:8080/api/v1/follows/${userId}`,
          {},
          {
            withCredentials: true,
            headers: {
              "Content-Type": "application/json"
            }
          }
        );
      }

      setUsers((prevUsers) =>
        prevUsers.map((user) =>
          user.user.id === userId
            ? { ...user,
              isFollowing: isFollowing === "ACCEPT" || isFollowing === "PENDING" ? null : "PENDING", // 요청 상태로 변경
            }
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
          let buttonColor = "bg-[#C8B6FF] text-white hover:bg-[#B8C0FF]"; // 기본 팔로우 버튼 (보라색)

          if (isFollowing == "PENDING") {
            buttonText = "요청함";
            buttonColor = "bg-[#BBD0FF] text-gray-800 hover:bg-[#B8C0FF]"; // 요청함 (연한 블루)
          } else if (isFollowing == "ACCEPT") {
            buttonText = "팔로잉";
            buttonColor = "bg-[#BBD0FF] text-gray-800 hover:bg-[#B8C0FF]"; // 팔로잉 (연한 블루)
          } else if (isFollower == "ACCEPT") {
            buttonText = "맞팔로우";
            buttonColor = "bg-[#FFD6FF] text-gray-800 hover:bg-[#E7C6FF]"; // 맞팔로우 (연한 핑크)
          }


          return (
            <div
              key={index}
              className="flex justify-between items-center p-2 pl-4 pr-4 bg-white rounded-lg shadow hover:shadow-lg transition cursor-pointer"
              onClick={() => handleUserClick(user.user.id)}
            >
              <div className="flex items-center gap-4">
                <Image 
                  src={user.user.profileImg}
                  alt="프로필 이미지"
                  unoptimized
                  width={60}
                  height={60}
                  className="rounded-full object-cover border-1 border-gray-300"
                />
                <span className="text-lg font-medium text-gray-800">{user.user.name}</span>
              </div>
              <button
                className={`px-4 py-2 text-sm font-medium rounded-lg transition ${buttonColor}`}
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