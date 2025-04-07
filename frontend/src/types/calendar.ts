export interface CalendarDate {
    id: number; // 캘린더 아이디
    date: string; // 'yyyy-MM-dd' 형식
    hasMemo: boolean; // 메모 작성 여부
    albumImage: string; // 앨범 이미지 링크
}

export interface Monthly {
    monthly: CalendarDate[];
}