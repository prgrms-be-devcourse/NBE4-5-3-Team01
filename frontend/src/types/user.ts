export interface User {
    id: string;
    name: string;
    profileImg: string;
    originalName: string;
    calendarVisibility: "PUBLIC" | "FOLLOWER_ONLY" | "PRIVATE";
}

export interface UserCalendarVisibilityOption {
    value: string;
    label: string;
    description: string;
}

export const USER_CALENDAR_VISIBILITY_OPTIONS: UserCalendarVisibilityOption[] = [
    {
        value: "public",
        label: "전체 공개",
        description: "누구나 볼 수 있어요",
    },
    {
        value: "follower_only",
        label: "팔로워 공개",
        description: "내 팔로워만 볼 수 있어요",
    },
    {
        value: "private",
        label: "비공개",
        description: "나만 볼 수 있어요",
    },
];