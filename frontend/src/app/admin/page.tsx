"use client";

import Link from "next/link";
import { Card, CardContent } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faShieldAlt, faCheck } from "@fortawesome/free-solid-svg-icons";

export default function AdminPage() {
    return (
        <section className="max-w-5xl mx-auto px-6 py-16">
            <div className="text-center mb-10">
                <h2 className="text-4xl font-bold text-gray-800 mb-2">관리자 페이지</h2>
                <p className="text-gray-600 text-sm">관리자만 접근 가능한 페이지입니다.</p>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                <Link href="/admin/membership" className="transition-transform hover:-translate-y-1">
                    <Card className="rounded-2xl shadow-xl border border-[#e7c6ff] bg-[#fdfbff]">
                        <CardContent className="p-6 flex flex-col gap-4 h-[130px]">
                            <div className="flex items-center gap-3">
                                <FontAwesomeIcon icon={faShieldAlt} className="text-purple-600 mr-2" />
                                <h3 className="text-lg font-semibold text-gray-800">멤버십 관리</h3>
                                <Badge className="bg-[#ffd6ff] text-[#333]">권한 필요</Badge>
                            </div>
                            <p className="text-sm text-gray-600">
                                유저 멤버십 등급, 시작일, 종료일, 갱신 여부 등을 확인하고 수정할 수 있어요.
                            </p>
                        </CardContent>
                    </Card>
                </Link>
                <Link href="/admin" className="transition-transform hover:-translate-y-1">
                    <Card className="rounded-2xl shadow-xl border border-[#e7c6ff] bg-[#fdfbff]">
                        <CardContent className="p-6 flex flex-col gap-4 h-[130px]">
                            <div className="flex items-center gap-3">
                                <FontAwesomeIcon icon={faCheck} className="text-purple-600 mr-2" />
                                <h3 className="text-lg font-semibold text-gray-800">타이틀</h3>
                                <Badge className="bg-[#ffd6ff] text-[#333]">권한 필요</Badge>
                            </div>
                            <p className="text-sm text-gray-600">
                                내용
                            </p>
                        </CardContent>
                    </Card>
                </Link>
            </div>
        </section>
    );
}