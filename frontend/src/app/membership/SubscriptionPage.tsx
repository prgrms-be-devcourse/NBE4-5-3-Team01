"use client";

import { useEffect, useState } from "react";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { ChevronDown, ChevronUp } from "lucide-react";
import Link from "next/link";

export default function SubscriptionInfo() {
    const subscription = {
        planName: "프리미엄 플랜",
        expireDate: "2025-04-30",
        isAutoRenew: true,
    };

    const today = new Date();
    const expire = new Date(subscription.expireDate);
    const diffDays = Math.ceil((expire.getTime() - today.getTime()) / (1000 * 60 * 60 * 24));

    return (
        <div className="max-w-5xl mx-auto px-6 py-12">
            <div className="bg-gradient-to-br from-pink-100 via-purple-100 to-blue-100 shadow-xl rounded-3xl p-10">
                <div className="text-center mb-8">
                    <h2 className="text-3xl font-bold text-purple-700 mb-1">👑 내 요금제</h2>
                    <p className="text-xl font-semibold">
                        {subscription.planName}
                        <span className="ml-2 bg-yellow-300 text-yellow-900 text-sm px-2 py-1 rounded-full">PREMIUM</span>
                    </p>
                    <p className="text-gray-600 mt-1">
                        만료일: {subscription.expireDate}{" "}
                        <span className="text-blue-600">({diffDays}일 남음)</span>
                    </p>
                </div>

                {/* 혜택 내용 - 항상 펼쳐진 상태 */}
                <div className="mb-10">
                    <h3 className="text-xl font-bold text-purple-700 mb-4">✨ 제공 서비스</h3>
                    <ul className="grid grid-cols-1 sm:grid-cols-2 gap-6">
                        <li className="bg-white p-4 rounded-xl shadow hover:shadow-md transition">
                            <Link href="/calendar">
                                <img src="/images/unlimited.png" alt="무제한 기록" className="w-full h-24 object-contain mb-2" />
                                <p className="text-center font-medium">🎵 무제한 음악 기록</p>
                            </Link>
                        </li>
                        <li className="bg-white p-4 rounded-xl shadow hover:shadow-md transition">
                            <Link href="/music">
                                <img src="/images/recommend.gif" alt="추천 음악" className="w-full h-24 object-contain mb-2" />
                                <p className="text-center font-medium">🎧 맞춤형 음악 추천</p>
                            </Link>
                        </li>
                        <li className="bg-white p-4 rounded-xl shadow hover:shadow-md transition">
                            <Link href="/music">
                                <img src="/images/mood.png" alt="기분 기반 추천" className="w-full h-24 object-contain mb-2" />
                                <p className="text-center font-medium">🎯 기분 기반 트랙 추천</p>
                            </Link>
                        </li>
                        <li className="bg-white p-4 rounded-xl shadow hover:shadow-md transition">
                            <Link href="/connect/spotify">
                                <img src="/images/spotify.png" alt="스포티파이 연동" className="w-full h-24 object-contain mb-2" />
                                <p className="text-center font-medium">🔗 스포티파이 연동 기능</p>
                            </Link>
                        </li>
                    </ul>
                </div>

                {/* 정기 결제 */}
                <div className="border-t pt-6">
                    <h3 className="text-lg font-bold text-indigo-600 mb-2">🔁 정기 결제 상태</h3>
                    <p className="text-gray-700 mb-4">
                        현재 상태:{" "}
                        <span className="text-green-600 animate-pulse font-semibold">자동 갱신 중</span>
                    </p>
                    <Button
                        variant="destructive"
                        className="bg-red-500 hover:bg-red-600 text-white font-semibold"
                        onClick={() => alert("정기 결제가 해지되었습니다.")}
                    >
                        정기 결제 해지하기
                    </Button>
                </div>
            </div>
        </div>
    );
}
