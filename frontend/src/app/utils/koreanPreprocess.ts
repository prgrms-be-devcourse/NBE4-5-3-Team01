// utils/koreanPreprocess.ts

// 공백 제거 + 자모 결합 처리 등 간단한 전처리
export function preprocessKoreanQuery(query: string): string {
    let processed = query.trim();

    // 유니코드 정규화 (자모 분리된 한글을 하나로 결합)
    processed = processed.normalize("NFC");

    // 추가적으로 불필요한 특수문자 제거
    processed = processed.replace(/[^\wㄱ-ㅎ가-힣\s]/g, "");

    return processed;
}
