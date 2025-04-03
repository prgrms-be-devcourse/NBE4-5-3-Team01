"use client";

import { createContext, useContext, useState, useEffect } from "react";
import { v4 as uuidv4 } from "uuid"; // 고유 ID 생성을 위해 설치 필요

interface Alert {
    id: string;
    code: string;
    message: string;
}

interface AlertContextType {
    setAlert: (alert: Omit<Alert, "id">) => void;
}

const AlertContext = createContext<AlertContextType | undefined>(undefined);

export const useGlobalAlert = () => {
    const context = useContext(AlertContext);
    if (!context) throw new Error("useGlobalAlert는 GlobalAlertProvider 안에서 사용해야 합니다.");
    return context;
};

export const GlobalAlertProvider = ({ children }: { children: React.ReactNode }) => {
    const [alerts, setAlerts] = useState<Alert[]>([]);

    const setAlert = ({ code, message }: Omit<Alert, "id">) => {
        const id = uuidv4();
        const newAlert = { id, code, message };
        setAlerts((prev) => [...prev, newAlert]);

        setTimeout(() => removeAlert(id), 3000);
    };

    const removeAlert = (id: string) => {
        setAlerts((prev) => prev.filter((a) => a.id !== id));
    };

    const getColor = (code: string) => {
        if (!code) return "bg-gray-300";

        if (code.startsWith("204")) return "bg-blue-400";
        if (code.startsWith("2")) return "bg-green-500";
        if (code.startsWith("4")) return "bg-yellow-400";
        if (code.startsWith("5")) return "bg-red-500";

        return "bg-gray-500";
    };

    return (
        <AlertContext.Provider value={{ setAlert }}>
            {children}

            <div className="fixed bottom-6 right-6 z-50 space-y-3 w-[320px]">
                {alerts.map((alert) => (
                    <FadeOutAlert
                        key={alert.id}
                        alert={alert}
                        color={getColor(alert.code)}
                        onClose={() => removeAlert(alert.id)}
                    />
                ))}
            </div>
        </AlertContext.Provider>
    );
};

// 🎨 개별 알림 컴포넌트
function FadeOutAlert({
    alert,
    color,
    onClose,
}: {
    alert: Alert;
    color: string;
    onClose: () => void;
}) {
    const [isVisible, setIsVisible] = useState(true); // 보여지고 있는지 상태

    // 자동 제거
    useEffect(() => {
        const timer = setTimeout(() => {
            setIsVisible(false); // ⬅️ 먼저 fadeOut 시작 (opacity-0 적용)
        }, 2700);

        return () => clearTimeout(timer);
    }, []);

    // opacity-0 되면 DOM에서 제거
    useEffect(() => {
        if (!isVisible) {
            const timeout = setTimeout(() => {
                onClose();
            }, 300); // ⏱️ fade-out 애니메이션 길이와 일치
            return () => clearTimeout(timeout);
        }
    }, [isVisible, onClose]);

    const handleCloseNow = () => {
        onClose(); // 바로 제거
    };

    return (
        <div
            className={`${color} text-white px-4 py-3 rounded-lg shadow-md relative transition-opacity duration-300 ${isVisible ? "opacity-100" : "opacity-0"
                }`}
        >
            <button
                onClick={handleCloseNow}
                className="absolute top-2 right-3 text-white text-sm hover:opacity-70 cursor-pointer"
            >
                ×
            </button>
            <p className="font-semibold mb-1">{alert.message}</p>
            <p className="text-sm opacity-80">코드: {alert.code}</p>

            {/* 🔽 진행바 */}
            <div className="absolute bottom-0 left-0 w-full h-1 bg-white/30 overflow-hidden rounded-b-lg">
                <div className="h-full bg-white animate-progressBar"></div>
            </div>
        </div>
    );
}
