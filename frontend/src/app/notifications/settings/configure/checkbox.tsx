import { useState } from "react";
import styles from "@/components/style/notificationSettingConfigure.module.css"; // 스타일 파일 경로

const Checkbox = () => {
  const [checked, setChecked] = useState(false);

  return (
    <>
      <label className={styles.label}>
        <input
          type="checkbox"
          className={styles.checkbox}
          checked={checked}
          onChange={() => setChecked(!checked)}
        />
        <span
          className={styles.checked}
          style={{
            backgroundColor: checked ? "#1ed760" : "transparent",
            border: checked ? "none" : "1px solid #727272",
          }}
        ></span>
      </label>
    </>
  );
};

export default Checkbox;
