import { apiClient } from "@/lib/api/apiClient";

export async function fetchUser(url: string) {
    return await apiClient.get(url);
}

export async function updateCalendarVisibility(calendarVisibility: string) {
    return await apiClient.patch("/user/calendar-visibility", {
        calendarVisibility: calendarVisibility.toUpperCase(),
    });
}