"use client";

import { useSearchParams, useRouter } from "next/navigation";
import { useEffect } from "react";
import axios from "axios";
import { useGlobalAlert } from "@/components/GlobalAlert";

const API_URL = "http://localhost:8080/api/v1";

export default function PaymentSuccessPage() {
  const params = useSearchParams();
  const router = useRouter();
  const { setAlert } = useGlobalAlert();

  useEffect(() => {
    const authKey = params.get("authKey");
    const customerKey = params.get("customerKey");
    const orderId = `order_${Date.now()}`;

    if (!authKey || !orderId || !customerKey) {
      setAlert({ code: "400", message: "결제 정보가 누락되었습니다." });
      return;
    }

    const requestSubscribe = async () => {
      try {
        const res = await axios.post(
          `${API_URL}/payment/subscribe`,
          {
            authKey,
            customerKey,
            amount: 1900,
          },
          {
            withCredentials: true,
          }
        );

        setAlert({ code: res.data.code, message: res.data.message });
        router.push("/membership");
      } catch (error) {
        setAlert({ code: "500", message: "결제 처리 중 오류가 발생했습니다." });
        console.error(error);
      }
    };

    requestSubscribe();
  }, []);

  return <div className="text-center mt-20">결제 완료 처리 중입니다...</div>;
}
