'use client'

import { useEffect, useState, useRef } from 'react'
import axios from 'axios'
import { Card } from '@/components/ui/card'
import PricingPage from './PricingPage'
import SubscriptionPage from './SubscriptionPage'
import { useGlobalAlert } from '@/components/GlobalAlert'

export default function MembershipPage() {
    const API_URL = "http://localhost:8080/api/v1";

    const [grade, setGrade] = useState<string | null>(null)
    const [loading, setLoading] = useState(true)
    const effectRan = useRef(false)
    const { setAlert } = useGlobalAlert()

    useEffect(() => {
        if (effectRan.current) return
        effectRan.current = true

        const fetchMembership = async () => {
            try {
                // 1. 유저 정보 먼저 가져오기
                const userRes = await axios.get(`${API_URL}/user/byCookie`, {
                    headers: { 'Content-Type': 'application/json' },
                    withCredentials: true,
                })
                const userId = userRes.data.id

                // 2. 기본 멤버십 설정 (null일 경우에만 backend에서 자동 처리됨)
                const initRes = await axios.post(`${API_URL}/membership/init`, userId, {
                    withCredentials: true,
                })

                // 3. 멤버십 정보 조회
                const membershipRes = await axios.get(`${API_URL}/membership/my`, {
                    withCredentials: true,
                })
                const { code, msg, data } = membershipRes.data
                setAlert({ code, message: msg })
                setGrade(data.grade)
            } catch (err) {
                console.error('멤버십 처리 실패:', err)
                setAlert({ code: '500-3', message: '멤버십 정보를 불러오지 못했어요.' })
            } finally {
                setLoading(false)
            }
        }

        fetchMembership()
    }, [])

    if (loading) {
        return <div className="p-10 text-center text-gray-500">로딩 중...</div>
    }

    return (
        <Card className="m-10 bg-white border-0 p-0">
            <div className="p-6 space-y-8">
                {grade === 'basic' && <PricingPage />}
                {grade === 'premium' && <SubscriptionPage />}
            </div>
        </Card>
    )
}
