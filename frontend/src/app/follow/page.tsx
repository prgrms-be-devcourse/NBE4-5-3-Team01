"use client"

import React, { useState, useEffect } from "react";
import axios from "axios";
import { useParams, useRouter, useSearchParams } from "next/navigation";

const FollowPage = () => {
  const [activeTab, setActiveTab] = useState("following");
  const { id } = useParams<{ id: string }>();
  const [users, setUsers] = useState([]);
  const router = useRouter();
  const searchParams = useSearchParams();
  const userId = searchParams.get("userId");

  useEffect(() => {
    const fetchUsers = async () => {
      try {
        if (!userId) return;
        const response = await axios.get(`http://localhost:8080/api/v1/follows/${activeTab}/${userId}`,
          { withCredentials: true });
        console.log("response: ", response.data);
        setUsers(response.data);
        console.log(users);
      } catch (error) {
        console.error("Error fetching users:", error);
      }
    };

    fetchUsers();
  }, [activeTab, userId]);

  const toggleFollow = async (e: React.MouseEvent, userId: string, isFollowing: boolean) => {
    e.stopPropagation();

    try {
      if (isFollowing) {
        await axios.delete(`http://localhost:8080/api/v1/follows/${userId}`, {
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
            ? { ...user, isFollowing: !isFollowing } // 기존 isFollower 값 유지
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
      {/* 탭 버튼 */}
      <div className="flex w-full max-w-lg bg-white rounded-lg shadow-md">
        <button
          className={`w-1/2 py-3 text-lg font-semibold transition-all rounded-l-lg ${activeTab === "following"
            ? "bg-[#C8B6FF] text-white shadow"
            : "bg-white text-gray-800 hover:bg-gray-100"
            }`}
          onClick={() => setActiveTab("following")}
        >
          팔로잉
        </button>
        <button
          className={`w-1/2 py-3 text-lg font-semibold transition-all rounded-r-lg ${activeTab === "follower"
            ? "bg-[#C8B6FF] text-white shadow"
            : "bg-white text-gray-800 hover:bg-gray-100"
            }`}
          onClick={() => setActiveTab("follower")}
        >
          팔로워
        </button>
      </div>

      {/* 사용자 리스트 */}
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
              className="flex justify-between items-center p-4 bg-white border border-[#E7C6FF] rounded-lg shadow hover:shadow-lg transition cursor-pointer"
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

export default FollowPage;