"use client";

import { useRouter, useSearchParams } from "next/navigation";
import { useEffect } from "react";
import axios from "axios";
import { useGlobalAlert } from "@/components/GlobalAlert";

const API_URL = "http://localhost:8080/api/v1";

export default function PaymentSuccessPage() {
  const router = useRouter();
  const { setAlert } = useGlobalAlert();
  const searchParams = useSearchParams();

  useEffect(() => {
    const paymentKey = searchParams.get("paymentKey");
    const orderId = searchParams.get("orderId");
    const amount = searchParams.get("amount");

    const authKey = searchParams.get("authKey");
    const customerKey = searchParams.get("customerKey");

    const isOneTime = paymentKey && orderId && amount;
    const isSubscription = authKey && customerKey;

    if (!isOneTime && !isSubscription) {
      setAlert({ code: "400", message: "결제 정보가 누락되었습니다." });
      return;
    }

    const request = async () => {
      try {
        let res;
        if (isOneTime) {
          res = await axios.post(
            `${API_URL}/payment/confirm`,
            {
              paymentKey,
              orderId,
              amount: Number(amount),
            },
            { withCredentials: true }
          );
        } else if (isSubscription) {
          res = await axios.post(
            `${API_URL}/payment/subscribe`,
            {
              authKey,
              customerKey,
            },
            { withCredentials: true }
          );
        }

        setAlert({ code: res.data.code, message: res.data.msg });
        router.push("/membership");
      } catch (error) {
        setAlert({ code: "500", message: "결제 처리 중 오류가 발생했습니다." });
        console.error(error);
      }
    };

    request();
  }, []);

  return <div className="text-center mt-20">결제 완료 처리 중입니다...</div>;
}
