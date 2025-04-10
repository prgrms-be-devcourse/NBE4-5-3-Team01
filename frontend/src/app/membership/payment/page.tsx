"use client";

import { use, useEffect, useState } from "react";
import axios from "axios";
import Script from "next/script";
import { useGlobalAlert } from "@/components/GlobalAlert";

const API_URL = "http://localhost:8080/api/v1";

declare global {
  interface Window {
    TossPayments: any;
  }
}

export default function PaymentPage() {
  const { setAlert } = useGlobalAlert();
  const [scriptLoaded, setScriptLoaded] = useState(false);
  const [customerKey, setCustomerKey] = useState<string | null>(null);
  const clientKey = process.env.NEXT_PUBLIC_TOSS_CLIENT_KEY!;

  useEffect(() => {
    const fetchUser = async () => {
      try {
        const res = await axios.get(`${API_URL}/user/byCookie`, {
          withCredentials: true,
        });
        const user = res.data.data;
        setCustomerKey(user.id);
      } catch {
        setAlert({ code: "401", message: "로그인 상태가 아니에요." });
      }
    };
    fetchUser();
  }, []);

  useEffect(() => {
    if (!scriptLoaded) {
      return;
    }

    if (
      !scriptLoaded ||
      !customerKey ||
      typeof window.TossPayments !== "function"
    ) {
      setAlert({
        code: "500",
        message: scriptLoaded + "결제 모듈이 아직 로드되지 않았어요.",
      });
      return;
    }

    const tossPayments = window.TossPayments(clientKey);

    tossPayments
      .requestBillingAuth("카드", {
        customerKey,
        successUrl: `${window.location.origin}/membership/payment/success`,
        failUrl: `${window.location.origin}/membership/payment/fail`,
        orderId: `order_${Date.now()}`,
      })
      .catch(() => {
        setAlert({ code: "400", message: "결제를 취소했어요." });
      });
  }, [scriptLoaded, customerKey]);

  return (
    <div className="text-center mt-20">
      <Script
        src="https://js.tosspayments.com/v1"
        strategy="afterInteractive"
        onLoad={() => setScriptLoaded(true)}
      />
      <h1 className="text-3xl font-bold mb-4">프리미엄 요금제 결제</h1>
      <p className="text-gray-500">결제창을 불러오는 중입니다...</p>
    </div>
  );
}
