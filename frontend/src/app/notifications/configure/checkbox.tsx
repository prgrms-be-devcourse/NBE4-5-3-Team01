import styles from "@/components/style/notificationSettingConfigure.module.css";

interface CheckboxProps {
  checked: boolean;
  onChange: () => void;
}

const Checkbox = ({ checked, onChange }: CheckboxProps) => {
  return (
    <label className={styles.label}>
      <input
        type="checkbox"
        className={styles.checkbox}
        checked={checked}
        onChange={onChange}
      />
      <span
        className={styles.checked}
        style={{
          backgroundColor: checked ? "#ae96fd" : "transparent",
          border: checked ? "none" : "1px solid #727272",
        }}
      ></span>
    </label>
  );
};

export default Checkbox;
