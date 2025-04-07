"use client"

import Calendar from "@/components/calendar/Calendar";
import {useSearchParams} from "next/navigation";

export default function MonthlyCalendar() {
    const params = useSearchParams();
    const key = params.toString();

    return (
        <main className={"bg-[#F8F7FF] h-screen"}>
            <Calendar key={key}/>
        </main>
    );
}