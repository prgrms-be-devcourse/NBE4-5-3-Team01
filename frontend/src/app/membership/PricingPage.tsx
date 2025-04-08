"use client";

import "@/app/membership/style.css";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faArrowRight } from "@fortawesome/free-solid-svg-icons";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Sparkles } from "lucide-react";

export default function PricingPage() {
    const pricingPlans = [
        {
            title: "Basic",
            price: "₩0",
            description: "누구나 부담 없이 시작할 수 있는 무료 플랜",
            features: [
                "하루 최대 20개의 음악 기록 저장",
                "기분 기반 음악 추천",
                "Spotify 플레이리스트 제공",
                "최대 200자 메모 가능",
                "일부 광고 포함",
            ],
            isPopular: false,
            comingSoon: false,
            isCurrent: true,
        },
        {
            title: "Premium",
            price: "₩1,900 / 월",
            description: "더 많은 기능과 편리함을 제공하는 인기 플랜",
            features: [
                "하루 최대 50개의 음악 기록 저장",
                "기분 + 최근 감상 아티스트 기반 추천",
                "플레이리스트 전체 기록 추가 기능",
                "최대 500자 메모 가능",
                "광고 제거",
                "개인 음악 기록 통계 제공",
            ],
            isPopular: true,
            comingSoon: false,
            isCurrent: false,
        },
        {
            title: "Pro",
            price: "₩9,900 / 월",
            description: "제한 없는 사용과 AI 기능을 모두 누려보세요",
            features: [
                "하루 음악 기록 무제한 저장",
                "AI 기반 자동 음악 기록 추천",
                "Spotify 플레이리스트 자동 추천",
                "메모 무제한 기록 가능",
                "AI 기반 자동 메모 생성 기능",
                "AI 분석 기반 음악 기록 통계 제공",
            ],
            isPopular: false,
            comingSoon: true,
            isCurrent: false,
        }
    ];

    return (
        <Card className="m-10 bg-white border-0 p-0">
            <div className="p-6 space-y-8">
                <div className="max-w-7xl mx-auto mt-14 px-6 pb-24">
                    <div className="text-center mb-14">
                        <h1 className="text-5xl font-bold tracking-tight">요금제 안내</h1>
                        <p className="text-gray-500 mt-3 text-lg">필요에 맞는 요금제를 선택해보세요!</p>
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                        {pricingPlans.map((plan) => (
                            <div
                                key={plan.title}
                                className={`flex flex-col justify-between h-full p-8 pt-10 rounded-2xl border-2 
                  ${plan.isPopular
                                        ? "border-purple-500 hover:shadow-purple-300 hover:shadow-xl hover:scale-[1.03]"
                                        : "border-gray-200 hover:shadow-md hover:scale-[1.02]"
                                    }
                  transition-all duration-300 bg-white relative`}  // 부모 div에 relative 추가
                            >
                                {/* 인기 마크 */}
                                {plan.isPopular && (
                                    <div className="absolute -top-4 left-1/2 -translate-x-1/2 bg-gradient-to-r from-purple-500 to-pink-500 text-white px-4 py-1 text-sm rounded-full shadow-lg flex items-center gap-2 z-10">
                                        <Sparkles className="w-4 h-4" />
                                        인기
                                    </div>
                                )}

                                {/* 카드 내용 */}
                                <h2 className="text-3xl font-bold mb-2">{plan.title}</h2>
                                <p className="text-2xl font-semibold text-gray-700 mb-4">{plan.price}</p>
                                <p className="text-gray-500 mb-6">{plan.description}</p>

                                <ul className="space-y-2 mb-8 text-sm text-gray-700">
                                    {plan.features.map((feature) => (
                                        <li key={feature}>• {feature}</li>
                                    ))}
                                </ul>

                                {/* 버튼 */}
                                <div className="mt-auto pt-6">
                                    {plan.comingSoon ? (
                                        <Button className="w-full h-12 bg-gray-200 text-gray-500 cursor-not-allowed" disabled>
                                            현재 서비스 준비 중입니다
                                        </Button>
                                    ) : plan.isCurrent ? (
                                        <Button className="w-full h-12 bg-green-100 text-green-700 cursor-default" disabled>
                                            현재 사용 중인 요금제
                                        </Button>
                                    ) : (
                                        <button className="w-full h-12 shiny-button justify-center">
                                            <span className="shiny-text">이 요금제로 업그레이드</span>
                                            <span className="shiny-icon">
                                                <FontAwesomeIcon icon={faArrowRight} />
                                            </span>
                                        </button>
                                    )}
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            </div>
        </Card>
    );
}
