import { apiClient } from "@/lib/api/apiClient";

export async function fetchMonthlyData(
    year: number,
    month: number,
    calendarOwnerId?: string
) {
    const headers = calendarOwnerId
        ? { "Calendar-Owner-Id": calendarOwnerId }
        : {};

    return await apiClient.get(
        `/calendar?year=${year}&month=${month}`,
        {headers}
    );
}