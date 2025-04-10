import axios from "axios";
import { useGlobalAlert } from "@/components/GlobalAlert";

export const useHandleApiError = () => {
    const { setAlert } = useGlobalAlert();

    const handleApiError = (error: unknown) => {
        if (axios.isAxiosError(error) && error.response?.data) {
            const { code, msg } = error.response.data;
            setAlert({ code, message: msg });
        } else {
            setAlert({ code: "500", message: "알 수 없는 오류가 발생했습니다." });
        }
    };

    return { handleApiError };
};
