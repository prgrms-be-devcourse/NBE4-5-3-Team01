'use client'

import axios from 'axios'
import { Button } from '@/components/ui/button'
import { useGlobalAlert } from '@/components/GlobalAlert'

export default function MembershipPage() {
    const API_URL = "http://localhost:8080/api/v1"
    const { setAlert } = useGlobalAlert();

    const handleUpgrade = async () => {
        try {
            const res = await axios.post(`${API_URL}/membership/upgrade`, {}, {
                withCredentials: true,
            });
            const { code, msg, data } = res.data
            setAlert({ code, message: msg })

            if (code.startsWith("2")) {
                window.location.href = "/membership";
            }
        } catch (e) {
            console.error('프리미엄 업그레이드 실패:', e)
            setAlert({ code: '500-1', message: '프리미엄으로 업그레이드를 실패 했어요.' })
        }
    }

    return (
        <div>
            <span>payment</span>
            <Button onClick={handleUpgrade}>Premium</Button>
        </div>
    )
}
