/**
 * 먼슬리 캘린더에서 기록이 없는 날짜에 대해 기록 추가를 유도하는 [ + ] 버튼
 */
export const createPlusButton = () => {
    const button = document.createElement("button");

    button.textContent = "+";
    button.className = "fc-plus-button";

    Object.assign(button.style, {
        position: "absolute",
        top: "50%",
        left: "50%",
        transform: "translate(-50%, -50%)",
        border: "none",
        padding: "5px 10px",
        cursor: "pointer",
        fontSize: "1.5em",
        color: "#393D3F",
        display: "none",
    });

    return button;
};