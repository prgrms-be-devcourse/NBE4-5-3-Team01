import { useState } from "react";
import AlertModal from "@/components/AlertModal";

type ModalOptions = {
  type?: "alert" | "confirm";
  title?: string;
  description?: string;
  confirmText?: string;
  cancelText?: string;
  onConfirm?: () => void;
  canClose?: boolean;
};

export const useModal = () => {
  const [open, setOpen] = useState(false);
  const [options, setOptions] = useState<ModalOptions>({});
  const [onResolve, setOnResolve] = useState<((result: boolean) => void) | (() => void) | null>(
    null
  );

  const showAlert = (opts: ModalOptions) => {
    return new Promise<void>((resolve) => {
      setOptions({ ...opts, type: "alert" });
      setOnResolve(() => () => resolve());
      setOpen(true);
    });
  };

  const showConfirm = (opts: ModalOptions) => {
    return new Promise<boolean>((resolve) => {
      setOptions({ ...opts, type: "confirm" });
      setOnResolve(() => () => resolve(true));
      setOpen(true);
    });
  };

  const handleClose = () => {
    if (options.type === "confirm" && onResolve) {
      onResolve(false);
    }
    setOpen(false);
  };

  const handleConfirm = () => {
    if (options.onConfirm) {
      options.onConfirm();
    }

    if (onResolve) {
      if (options.type === "confirm") {
        (onResolve as (result: boolean) => void)(true);
      } else {
        (onResolve as () => void)();
      }
    }
    setOpen(false);
  };

  const ModalComponent = (
    <AlertModal
      open={open}
      type={options.type}
      title={options.title}
      description={options.description}
      confirmText={options.confirmText}
      cancelText={options.cancelText}
      onClose={handleClose}
      onConfirm={handleConfirm}
      canClose={options.canClose}
    />
  );

  return { showAlert, showConfirm, ModalComponent };
};
