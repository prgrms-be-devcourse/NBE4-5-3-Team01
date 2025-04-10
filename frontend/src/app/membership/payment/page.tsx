"use client";

import { use, useEffect, useState } from "react";
import axios from "axios";
import Script from "next/script";
import { useGlobalAlert } from "@/components/GlobalAlert";
import { describe } from "node:test";

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
  const [grade, setGrade] = useState(null);
  const clientKey = process.env.NEXT_PUBLIC_TOSS_CLIENT_KEY!;

  useEffect(() => {
    const fetchUser = async () => {
      try {
        const res = await axios.get(`${API_URL}/user/byCookie`, {
          withCredentials: true,
        });
        const user = res.data.data;
        setCustomerKey(user.id);

        const membershipRes = await axios.get(`${API_URL}/membership/my`, {
          withCredentials: true,
        });
        const membership = membershipRes.data.data;
        setGrade(membership.grade);
      } catch {
        setAlert({ code: "401", message: "로그인 상태가 아니에요." });
      }
    };
    fetchUser();
  }, []);

  useEffect(() => {
    if (!scriptLoaded) return;

    if (typeof window.TossPayments !== "function") {
      setAlert({ code: "500", message: "결제 모듈 로드 실패" });
      return;
    }
  }, [scriptLoaded, customerKey]);

  const handlePayment = (type: "ONE_TIME" | "SUBSCRIPTION") => {
    const tossPayments = window.TossPayments(clientKey);
    const orderId = `order_${Date.now()}`;
    const orderName =
      type === "ONE_TIME"
        ? "프리미엄 멤버십 단건 결제"
        : "프리미엄 멤버십 정기 결제";

    const paymentOptions = {
      amount: 1900,
      orderId,
      orderName,
      successUrl: `${window.location.origin}/membership/payment/success`,
      failUrl: `${window.location.origin}/membership/payment/fail`,
    };

    if (type === "SUBSCRIPTION") {
      tossPayments.requestBillingAuth("카드", {
        ...paymentOptions,
        customerKey,
      });
    } else {
      tossPayments.requestPayment("카드", paymentOptions);
    }
  };

  return (
    <div className="space-y-4 text-center mt-8">
      <h2 className="text-xl font-bold">원하시는 결제 방식을 선택하세요</h2>
      <div className="flex justify-center gap-4 mt-4">
        {/* 단건 결제 버튼 */}
        {grade !== "premium" ? (
          <button onClick={() => handlePayment("ONE_TIME")}>단건 결제</button>
        ) : null}
        <button
          onClick={() => handlePayment("SUBSCRIPTION")}
          className="px-4 py-2 rounded bg-blue-500 text-white hover:bg-blue-600"
        >
          정기 결제
        </button>
        <Script
          src="https://js.tosspayments.com/v1"
          strategy="afterInteractive"
          onLoad={() => setScriptLoaded(true)}
        />
      </div>
    </div>
  );
}
