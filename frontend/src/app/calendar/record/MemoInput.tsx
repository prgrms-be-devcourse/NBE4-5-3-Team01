"use client";

import "./style.css";

export default function MemoInput({ memo, setMemo }) {
  return (
    <div>
      <h3 className="text-xl font-semibold mb-2 ml-2">메모</h3>
      <textarea
        className="memo-input"
        placeholder="메모를 입력하세요."
        value={memo}
        onChange={(e) => setMemo(e.target.value)}
      />
    </div>
  );
}
