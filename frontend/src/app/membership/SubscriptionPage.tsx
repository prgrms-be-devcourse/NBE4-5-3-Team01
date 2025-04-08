"use client";

import "@/app/membership/style.css";
import { useEffect, useState, useRef } from "react";
import { useRouter } from 'next/navigation';
import axios from "axios";
import Link from "next/link"
import { Button } from "@/components/ui/button";
import { useGlobalAlert } from "@/components/GlobalAlert";
import AlertModal from "@/components/AlertModal";

interface MembershipData {
    grade: string;
    startDate: string | null;
    endDate: string | null;
    autoRenew: boolean;
}

export default function SubscriptionInfo() {
    const API_URL = "http://localhost:8080/api/v1";

    const [membership, setMembership] = useState<MembershipData | null>(null);

    const effectRan = useRef(false);
    const router = useRouter();

    const { setAlert } = useGlobalAlert();
    const [modalOpen, setModalOpen] = useState(false);
    const [modalContent, setModalContent] = useState<{
        type: "alert" | "confirm";
        title: string;
        description: string;
        confirmText: string;
        cancelText?: string;
        onConfirm?: () => void;
    }>({
        type: "alert",
        title: "",
        description: "",
        confirmText: "확인",
    });


    useEffect(() => {
        if (effectRan.current) return
        effectRan.current = true

        const fetchMembership = async () => {
            try {
                const res = await axios.get(`${API_URL}/membership/my`, {
                    withCredentials: true,
                });
                setMembership(res.data.data);
            } catch (error) {
                setAlert({ code: "500", message: "요금제 정보를 불러올 수 없습니다." });
            }
        };

        fetchMembership();
    }, []);

    if (!membership) {
        return (
            <div className="text-center text-gray-500 min-h-[300px] flex items-center justify-center">
                요금제 정보를 불러오는 중입니다...
            </div>
        );
    }

    const handleCancel = async () => {
        setModalContent({
            type: "confirm",
            title: "정기 결제를 해지할까요?",
            description: "해지하면 프리미엄 혜택을 더 이상 이용할 수 없어요.",
            confirmText: "해지하기",
            cancelText: "유지하기",
            onConfirm: async () => {
                try {
                    const res = await axios.post(`${API_URL}/membership/cancel`, {}, {
                        withCredentials: true,
                    });
                    const { code, msg, data } = res.data;

                    if (code.startsWith("2")) {
                        setModalContent({
                            type: "alert",
                            title: "해지 완료",
                            description: msg || "요금제가 정상적으로 해지되었습니다.",
                            confirmText: "확인",
                            onConfirm: () => window.location.reload(),
                        });
                        setModalOpen(true);
                    }
                } catch (err) {
                    setModalContent({
                        type: "alert",
                        title: "해지 실패",
                        description: "요금제 해지에 실패했어요.",
                        confirmText: "닫기",
                    });
                    setModalOpen(true);
                }
            },
        });
        setModalOpen(true);
    };

    const today = new Date();
    const expire = new Date(membership.endDate || "");
    const diffDays = Math.ceil((expire.getTime() - today.getTime()) / (1000 * 60 * 60 * 24));

    return (
        <section className="max-w-4xl mx-auto px-6 py-12">
            <div className="relative bg-gradient-to-br from-[#fef3f8] via-[#f0e9ff] to-[#e0f2fe] rounded-3xl p-10 shadow-2xl border border-purple-200 overflow-hidden">
                {/* 빛 효과 */}
                <div className="absolute inset-0 rounded-3xl bg-white/30 backdrop-blur-sm shadow-inner" />

                <div className="relative z-10">

                    {/* 상단 타이틀 */}
                    <div className="text-center mb-8">
                        <h2 className="text-4xl font-extrabold text-purple-700 drop-shadow-sm">👑 내 요금제</h2>
                        <p className="text-xl font-bold mt-2 flex justify-center items-center gap-2">
                            프리미엄 플랜
                            <span className="bg-yellow-400 text-yellow-900 text-sm px-3 py-1 rounded-full shadow">PREMIUM</span>
                        </p>
                        <p className="text-sm text-gray-600 mt-1">
                            만료일: <span className="font-medium">{membership.endDate}</span>{" "}
                            <span className="text-blue-600">({diffDays}일 남음)</span>
                        </p>
                    </div>

                    {/* 프리미엄 혜택 리스트 */}
                    <div className="bg-white/60 backdrop-blur-md p-6 rounded-xl shadow-lg mb-10 border border-white/20">
                        <h3 className="font-semibold text-gray-800 mb-3">🎧 프리미엄에서 누릴 수 있어요!</h3>
                        <ul className="text-sm text-gray-800 list-disc list-inside space-y-2 leading-relaxed">
                            <li><span className="text-purple-600 font-semibold">50곡</span>까지 음악 기록 저장</li>
                            <li><span className="text-pink-600 font-semibold">플레이리스트 전체</span> 기록 가능</li>
                            <li><span className="text-teal-600 font-semibold">최대 500자</span> 까지 메모 작성</li>
                            <li><span className="text-orange-500 font-semibold">광고 없이</span> 쾌적한 이용</li>
                        </ul>

                        {/* 이미지로 제공되는 기능 */}
                        <h3 className="font-semibold text-gray-800 mt-6 mb-3">🎵 Spotify 기반 추가 기능을 경험해보세요</h3>
                        <div className="premium-feature-image">
                            <Link href="/music" className="feature-card">
                                <img src="/preview/recommend.png" alt="아티스트 추천" />
                                <div className="feature-overlay">🎤 최근 들은 아티스트 추천 보러가기</div>
                            </Link>

                            <Link href="/user/recap" className="feature-card">
                                <img src="/preview/recap.png" alt="통계 미리보기" />
                                <div className="feature-overlay">📊 나의 음악 통계 보러가기</div>
                            </Link>
                        </div>
                    </div>

                    {/* 정기 결제 상태 */}
                    <div className="text-center">
                        <h3 className="text-base font-bold text-indigo-600 mb-1">🔁 정기 결제 상태</h3>
                        <p className="text-sm text-gray-800 mb-4">
                            현재 상태:{" "}
                            <span className={membership.autoRenew ? "text-green-600 font-semibold" : "text-red-500 font-semibold"}>
                                {membership.autoRenew ? "자동 갱신 중" : "갱신 안 함"}
                            </span>
                        </p>

                        {membership.autoRenew ? (
                            <Button
                                variant="outline"
                                className="text-sm px-4 py-1 text-gray-600 border-gray-300 hover:bg-gray-100 cursor-pointer"
                                onClick={handleCancel}
                            >
                                정기 결제 해지하기
                            </Button>
                        ) : (
                            <Button
                                className="bg-gradient-to-r from-purple-500 to-indigo-500 text-white font-semibold text-sm px-5 py-2 rounded-md shadow hover:brightness-110 transition cursor-pointer"
                                onClick={() => router.push("/membership/payment")}
                            >
                                프리미엄 다시 시작하기
                            </Button>
                        )}
                    </div>

                </div>
            </div>
            <AlertModal
                open={modalOpen}
                type={modalContent.type as "alert" | "confirm"}
                title={modalContent.title}
                description={modalContent.description}
                confirmText={modalContent.confirmText}
                cancelText={modalContent.cancelText}
                onConfirm={modalContent.onConfirm}
                onClose={() => setModalOpen(false)}
            />
        </section>
    )
}