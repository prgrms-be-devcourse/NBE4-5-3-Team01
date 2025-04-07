/**
 * 날짜 셀에 저장될 이미지의 밝기 측정
 * @param url
 * @param cropRatio
 * @param threshold
 */
export async function isDarkImage(url: string, cropRatio = 0.2, threshold = 150): Promise<boolean> {
    return new Promise((resolve) => {
        const image = new Image();
        image.crossOrigin = "anonymous";
        image.src = url;

        image.onload = () => {
            const canvas = document.createElement("canvas");
            const ctx = canvas.getContext("2d");

            if (!ctx) return resolve(false);

            // 날짜가 있는 우측 상단 영역 자르기
            const cropWidth = image.width * cropRatio;
            const cropHeight = image.height * cropRatio;
            const cropX = image.width - cropWidth;
            const cropY = 0;

            canvas.width = cropWidth;
            canvas.height = cropHeight;

            ctx.drawImage(image, cropX, cropY, cropWidth, cropHeight, 0, 0, cropWidth, cropHeight);

            const imageData = ctx.getImageData(0, 0, cropWidth, cropHeight);
            const data = imageData.data;

            let totalLuminance = 0;
            const pixelCount = cropWidth * cropHeight;

            for (let i = 0; i < data.length; i += 4) {
                const r = data[i];
                const g = data[i + 1];
                const b = data[i + 2];
                const luminance = 0.2126 * r + 0.7152 * g + 0.0722 * b;
                totalLuminance += luminance;
            }

            const avgLuminance = totalLuminance / pixelCount;
            resolve(avgLuminance < threshold);
        };

        image.onerror = () => resolve(false); // 에러 발생 시 밝은 이미지로 간주
    });
}