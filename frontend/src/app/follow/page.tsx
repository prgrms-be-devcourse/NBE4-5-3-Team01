"use client"

import Image from 'next/image'
import React, { useState, useEffect } from "react";
import axios from "axios";
import { useParams, useRouter, useSearchParams } from "next/navigation";

const FollowPage = () => {
  const [activeTab, setActiveTab] = useState("following");
  const { id } = useParams<{ id: string }>(); 
  const [users, setUsers] = useState([]);
  const [currentUserId, setCurrentUserId] = useState<string | null>(null);
  const router = useRouter();
  const searchParams = useSearchParams(); 
  const userId = searchParams.get("userId");

  useEffect(() => {
    const fetchUsers = async () => {
      try {
        let loggedInUserId = (
          await axios.get(`http://localhost:8080/api/v1/user/getUsers`, {
            withCredentials: true,
          })
        ).data.data.id;
        setCurrentUserId(loggedInUserId);
        let userIdToUse = userId || loggedInUserId;
        console.log(userIdToUse);
        let response = null;
        if (currentUserId === userIdToUse && activeTab === "following") {
          response = await axios.get(
            `http://localhost:8080/api/v1/follows/my`,
            {
              withCredentials: true,
            }
          );
        } else if(currentUserId === userIdToUse && activeTab === "pending") {
          response = await axios.get(`http://localhost:8080/api/v1/follows/my/pending`,
          {
            withCredentials: true
          }
          );
        } else {
          response = await axios.get(`http://localhost:8080/api/v1/follows/${activeTab}/${userIdToUse}`,
          {
            withCredentials: true
          }
          );
        } 
        
        setUsers(response.data.data);
        console.log(users);
      } catch (error) {
        console.error("Error fetching users:", error);
      }
    };

    fetchUsers();
  }, [activeTab, userId]);

  const toggleFollow = async (e: React.MouseEvent, userId: string, isFollowing: string) => {
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

  const acceptFollow = async (e: React.MouseEvent, userId: string) => {
    e.stopPropagation();
    try {
      await axios.put(
        `http://localhost:8080/api/v1/follows/accept/${userId}`,
        {},
        { withCredentials: true }
      );

      setUsers((prevUsers) => prevUsers.filter((user) => user.user.id !== userId));
    } catch (error) {
      console.error("Error accepting follow request:", error);
    }
  };

  const rejectFollow = async (e: React.MouseEvent, userId: string) => {
    e.stopPropagation();
    try {
      await axios.delete(`http://localhost:8080/api/v1/follows/reject/${userId}`, {
        withCredentials: true,
      });

      setUsers((prevUsers) => prevUsers.filter((user) => user.user.id !== userId));
    } catch (error) {
      console.error("Error rejecting follow request:", error);
    }
  };

  const handleUserClick = (userId: string) => {
    router.push(`/calendar?userId=${userId}`);
  };

  const isCurrentUserProfile = userId === null || userId === currentUserId;

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
          className={`w-1/2 py-3 text-lg font-semibold transition-all ${activeTab === "follower"
              ? "bg-[#C8B6FF] text-white shadow"
              : "bg-white text-gray-800 hover:bg-gray-100"
              } ${!isCurrentUserProfile ? "rounded-r-lg" : ""}`}
            onClick={() => setActiveTab("follower")}
          >
            팔로워
          </button>
          {isCurrentUserProfile && (
            <button
              className={`w-1/2 py-3 text-lg font-semibold transition-all rounded-r-lg ${activeTab === "pending"
              ? "bg-[#C8B6FF] text-white shadow"
              : "bg-white text-gray-800 hover:bg-gray-100"
              }`}
              onClick={() => setActiveTab("pending")}
            >
              요청함
            </button>
          )}
        </div>

      {/* 사용자 리스트 */}
      <div className="w-full max-w-2xl mt-8 space-y-4">
          {users?.map((user, index) => {
            const isFollowing = user.isFollowing;
            const isFollower = user.isFollower;
            let buttonText = "팔로우";
            let buttonColor = "bg-[#C8B6FF] text-white hover:bg-[#B8C0FF]"

          if (isFollowing == "PENDING") {
            buttonText = "요청함"
            buttonColor = "bg-[#BBD0FF] text-gray-800 hover:bg-[#B8C0FF]"
          } 
          else if(isFollowing == "ACCEPT") { 
            buttonText = "팔로잉"
            buttonColor = "bg-[#BBD0FF] text-gray-800 hover:bg-[#B8C0FF]"
          }
          else if (isFollower == "ACCEPT") { 
            buttonText = "맞팔로우"
            buttonColor = "bg-[#FFD6FF] text-gray-800 hover:bg-[#E7C6FF]"
          }

            return (
            <div
              key={index}
              className="flex justify-between items-center p-2 pl-4 pr-4 bg-white border border-[#E7C6FF] rounded-lg shadow hover:shadow-lg transition cursor-pointer"
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

              {activeTab === "pending" ? (
                <div className="flex space-x-2">
                  <button
                    className="px-4 py-2 text-sm font-medium rounded-lg bg-[#BBD0FF] text-gray-800 hover:bg-[#B8C0FF]"
                    onClick={(e) => acceptFollow(e, user.user.id)}
                  >
                    수락
                  </button>
                  <button
                    className="px-4 py-2 text-sm font-medium rounded-lg bg-[#F8F7FF] text-gray-800 hover:bg-gray-100 "
                    onClick={(e) => rejectFollow(e, user.user.id)}
                  >
                    거절
                  </button>
                </div>
              ) : (
                <button
                  className={`px-4 py-2 text-sm font-medium rounded-lg transition ${buttonColor}`}
                  onClick={(e) => toggleFollow(e, user.user.id, isFollowing)}
                >
                  {buttonText}
                </button>
              )}
            </div>
          );
          })}
        </div>
      </div>
  );
};

export default FollowPage;