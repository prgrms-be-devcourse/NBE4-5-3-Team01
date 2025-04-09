import React from "react";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";

interface AlertModalProps {
    open: boolean;
    type?: "alert" | "confirm";
    title?: string;
    description?: string;
    confirmText?: string;
    cancelText?: string;
    onClose: () => void;
    onConfirm?: () => void;
}

export default function AlertModal({
    open,
    type = "alert",
    title = "알림",
    description = "",
    confirmText = "확인",
    cancelText = "취소",
    onClose,
    onConfirm,
}: AlertModalProps) {
    const isConfirm = type === "confirm";

    return (
        <Dialog open={open} onOpenChange={onClose}>
            <DialogContent className="max-w-sm sm:max-w-md w-full p-6 rounded-xl bg-white shadow-xl z-50">
                <DialogHeader className="bg-[#C8B6FF] -mx-6 -mt-6 px-6 py-4 rounded-t-xl">
                    <DialogTitle className="text-lg font-bold">{title}</DialogTitle>
                </DialogHeader>
                <p className="text-sm text-gray-600 my-4 whitespace-pre-line">{description}</p>
                <DialogFooter className="flex justify-center gap-4">
                    {isConfirm && (
                        <button
                            onClick={onClose}
                            className="px-6 py-2 text-gray-700 bg-gray-100 hover:bg-gray-200 rounded-md cursor-pointer border border-gray-300 transition-colors"
                        >
                            {cancelText}
                        </button>
                    )}
                    <button
                        onClick={() => {
                            if (onConfirm) onConfirm();
                            onClose();
                        }}
                        className="px-6 py-2 text-black bg-[#C8B6FF] hover:bg-[#845BFF] hover:text-white rounded-md cursor-pointer transition-colors"
                    >
                        {confirmText}
                    </button>
                </DialogFooter>
            </DialogContent>
        </Dialog>
    );
}
