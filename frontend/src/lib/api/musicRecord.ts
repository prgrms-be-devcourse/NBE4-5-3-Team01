import { ParamValue } from "next/dist/server/request/params";
import { apiClient } from "@/lib/api/apiClient";

export async function fetchMusicRecords(id: ParamValue) {
    return await apiClient.get(`/calendar/${id}`);
}