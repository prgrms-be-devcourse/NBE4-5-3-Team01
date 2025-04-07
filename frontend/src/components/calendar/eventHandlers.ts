import type { DayCellMountArg, EventMountArg } from "@fullcalendar/core";
import { createPlusButton } from "@/components/calendar/plusButton";
import { isDarkImage } from "@/components/calendar/imageBrightness";

/**
 * 음악 기록이 있는 날짜 셀 스타일 처리
 */
export const handleEventDidMount = (arg: EventMountArg) => {
    const cell = arg.el.closest(".fc-daygrid-day");
    const albumImage = arg.event.extendedProps.albumImage;

    if (cell && albumImage) {
        cell.classList.add("has-record");

        arg.el.style.backgroundImage = `url(${albumImage})`;
        arg.el.style.backgroundSize = "cover";
        arg.el.style.backgroundPosition = "center";
        arg.el.style.opacity = "1";
        arg.el.style.pointerEvents = "none";

        const dateNumber = cell.querySelector(".fc-daygrid-day-number") as HTMLElement;

        const image = new Image();
        image.crossOrigin = "anonymous";
        image.src = albumImage;

        image.onload = async () => {
            const isDark = await isDarkImage(albumImage);

            if (dateNumber) {
                dateNumber.style.setProperty("color", isDark ? "#FFFFFF" : "#AE96FD", "important");
                dateNumber.style.setProperty("font-weight", "700", "important");
                if (isDark) dateNumber.style.setProperty("text-shadow", "0 0 3px rgba(0,0,0,0.5)", "important");
            }
        };
    }
};

/**
 * 마우스 호버 이벤트 등록
 */
export const handleDayCellDidMount = (
    arg: DayCellMountArg,
    isCalendarOwner: boolean
) => {
    const cell = arg.el as HTMLElement;
    const button = createPlusButton();

    cell.style.position = "relative";

    // 버튼 중복 추가 방지
    if (!cell.querySelector(".fc-plus-button")) {
        cell.appendChild(button);
    }

    // 마우스가 날짜 셀에 올라갈 때
    cell.addEventListener("mouseenter", () => {
        const isDisabled = cell.classList.contains("fc-day-disabled");

        if (isCalendarOwner && !isDisabled) {
            button.style.display = "block";
        }

        cell.style.background = "#C8B6FF";
    });

    // 마우스가 날짜 셀을 떠날 때
    cell.addEventListener("mouseleave", () => {
        const isDisabled = cell.classList.contains("fc-day-disabled");

        if (isCalendarOwner && !isDisabled) {
            button.style.display = "none";
        }

        cell.style.background = "";
    });
};