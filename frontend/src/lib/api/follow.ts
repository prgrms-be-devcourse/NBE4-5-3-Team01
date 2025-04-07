import { apiClient } from "@/lib/api/apiClient";

export async function fetchFollowCount(userId: string) {
    return await apiClient.get(`/follows/count/${userId}`);
}