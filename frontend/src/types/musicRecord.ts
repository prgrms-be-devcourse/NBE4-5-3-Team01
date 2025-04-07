export interface Music {
    id: string;
    name: string;
    singer: string;
    singerId: string;
    releaseDate: string;
    albumImage: string;
    genre: string;
    uri: string;
}

export interface MusicRecord {
    id: number;
    date: string;
    memo: string;
    musics: Music[];
    calendarPermission: "EDIT" | "VIEW";
}