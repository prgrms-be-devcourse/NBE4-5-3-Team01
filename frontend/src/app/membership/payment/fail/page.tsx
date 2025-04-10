"use client";

import { useEffect } from "react";
import { useSearchParams, useRouter } from "next/navigation";
import { useGlobalAlert } from "@/components/GlobalAlert";

export default function PaymentFailPage() {
  const params = useSearchParams();
  const router = useRouter();
  const { setAlert } = useGlobalAlert();

  const code = params.get("code") || "UNKNOWN_ERROR";
  const message = decodeURIComponent(
    params.get("message") || "결제가 실패했습니다."
  );

  useEffect(() => {
    setAlert({ code, message });

    const timer = setTimeout(() => {
      router.push("/membership/payment");
    }, 4000);

    return () => clearTimeout(timer);
  }, [code, message]);

  return (
    <div className="text-center mt-20">
      <h1 className="text-2xl font-bold text-red-500">❌ 결제 실패</h1>
      <p className="mt-4 text-gray-600">{message}</p>
      <p className="mt-2 text-sm text-gray-400">({code})</p>
      <p className="mt-6 text-sm text-gray-500">
        잠시 후 다시 결제 페이지로 이동합니다...
      </p>
    </div>
  );
}
