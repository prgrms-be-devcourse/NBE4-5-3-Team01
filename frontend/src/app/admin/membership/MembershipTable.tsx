"use client";

import { useEffect, useState } from "react";
import axios from "axios";

interface Membership {
    id: number;
    userId: string;
    userName: string;
    grade: string;
    startDate: string;
    endDate: string;
    months: number;
    autoRenew: boolean;
}

export default function MembershipTable() {
    const [memberships, setMemberships] = useState<Membership[]>([]);
    const [editIndex, setEditIndex] = useState<number | null>(null);
    const [editedData, setEditedData] = useState<Partial<Membership>>({});

    useEffect(() => {
        const fetchMemberships = async () => {
            try {
                const res = await axios.get("http://localhost:8080/api/v1/membership/admin", {
                    withCredentials: true,
                });
                setMemberships(res.data.data);
            } catch (err) {
                console.error("멤버십 정보를 불러오지 못했습니다.");
            }
        };

        fetchMemberships();
    }, []);

    const handleEditClick = (index: number) => {
        setEditIndex(index);
        setEditedData(memberships[index]);
    };

    const handleChange = (field: keyof Membership, value: string | boolean) => {
        setEditedData((prev) => ({ ...prev, [field]: value }));
    };

    const handleSave = async (id: String) => {
        try {
            const dto = {
                grade: editedData.grade,
                startDate: editedData.startDate || null,
                endDate: editedData.endDate || null,
                autoRenew: editedData.autoRenew ?? false,
            };

            const res = await axios.patch(
                `http://localhost:8080/api/v1/membership/admin/${id}`,
                dto,
                { withCredentials: true }
            );

            const updated = [...memberships];
            if (editIndex !== null) updated[editIndex] = { ...updated[editIndex], ...dto } as Membership;

            console.log(res)

            setMemberships(updated);
            setEditIndex(null);
        } catch (error) {
            console.error("수정 실패", error);
        }
    };

    return (
        <div className="overflow-x-auto bg-white rounded-xl shadow-md p-6 border border-gray-200">
            <table className="min-w-full table-auto text-sm text-gray-700">
                <thead>
                    <tr className="bg-[#f7f2ff] text-gray-800">
                        <th className="px-4 py-2 text-left">유저 이름</th>
                        <th className="px-4 py-2 text-left">등급</th>
                        <th className="px-4 py-2 text-left">시작일</th>
                        <th className="px-4 py-2 text-left">종료일</th>
                        <th className="px-4 py-2 text-left">개월 수</th>
                        <th className="px-4 py-2 text-left">갱신 여부</th>
                        <th className="px-4 py-2 text-left"></th>
                    </tr>
                </thead>
                <tbody>
                    {memberships.map((m, i) => (
                        <tr key={m.id} className="hover:bg-[#f5f0ff] border-t">
                            <td className="px-4 py-2">{m.userName}</td>

                            {/* 등급 */}
                            <td className="px-4 py-2">
                                {editIndex === i ? (
                                    <select
                                        value={editedData.grade}
                                        onChange={(e) => handleChange("grade", e.target.value)}
                                        className="border rounded px-2 py-1"
                                    >
                                        <option value="basic">basic</option>
                                        <option value="premium">premium</option>
                                    </select>
                                ) : (
                                    <span className="font-semibold">{m.grade}</span>
                                )}
                            </td>

                            {/* 시작일 */}
                            <td className="px-4 py-2">
                                {editIndex === i ? (
                                    <input
                                        type="date"
                                        value={editedData.startDate}
                                        onChange={(e) => handleChange("startDate", e.target.value)}
                                        className="border rounded px-2 py-1"
                                    />
                                ) : (
                                    m.startDate || "-"
                                )}
                            </td>

                            {/* 종료일 */}
                            <td className="px-4 py-2">
                                {editIndex === i ? (
                                    <input
                                        type="date"
                                        value={editedData.endDate}
                                        onChange={(e) => handleChange("endDate", e.target.value)}
                                        className="border rounded px-2 py-1"
                                    />
                                ) : (
                                    m.endDate || "-"
                                )}
                            </td>

                            {/* 개월 수 */}
                            <td className="px-4 py-2">{m.months}개월</td>

                            {/* 갱신 여부 */}
                            <td className="px-4 py-2">
                                {editIndex === i ? (
                                    <select
                                        value={editedData.autoRenew ? "true" : "false"}
                                        onChange={(e) => handleChange("autoRenew", e.target.value === "true")}
                                        className="border rounded px-2 py-1"
                                    >
                                        <option value="true">ON</option>
                                        <option value="false">OFF</option>
                                    </select>
                                ) : m.autoRenew ? (
                                    <span className="text-green-600 font-medium">ON</span>
                                ) : (
                                    <span className="text-red-500 font-medium">OFF</span>
                                )}
                            </td>

                            {/* 수정 버튼 */}
                            <td className="px-4 py-2">
                                {editIndex === i ? (
                                    <button
                                        className="text-blue-600 hover:underline mr-2"
                                        onClick={() => handleSave(m.userId)}
                                    >
                                        저장
                                    </button>
                                ) : (
                                    <button
                                        className="text-indigo-600 hover:underline"
                                        onClick={() => handleEditClick(i)}
                                    >
                                        수정
                                    </button>
                                )}
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
}
