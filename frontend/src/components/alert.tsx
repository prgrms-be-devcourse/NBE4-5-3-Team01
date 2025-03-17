import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert";
import { cn } from "@/lib/utils";
import { Info, XCircle, CheckCircle, AlertTriangle } from "lucide-react";

type AlertVariant = "default" | "destructive" | "success" | "warning";

interface AlertComponentProps {
    title: string;
    description?: string;
    variant?: AlertVariant;
}

const variantIcons = {
    default: <Info className="h-5 w-5 text-blue-500" />,
    destructive: <XCircle className="h-5 w-5 text-red-500" />,
    success: <CheckCircle className="h-5 w-5 text-green-500" />,
    warning: <AlertTriangle className="h-5 w-5 text-yellow-500" />,
};

const variantStyles = {
    default: "border-blue-500 text-blue-600 bg-blue-50",
    destructive: "border-red-500 text-red-600 bg-red-50",
    success: "border-green-500 text-green-600 bg-green-50",
    warning: "border-yellow-500 text-yellow-600 bg-yellow-50",
};

export function AlertComponent({ title, description, variant = "default" }: AlertComponentProps) {
    return (
        <Alert className={cn("flex items-center space-x-3 border p-4 rounded-lg mb-5", variantStyles[variant])}>
            {variantIcons[variant]}
            <div>
                <AlertTitle>{title}</AlertTitle>
                {description && <AlertDescription>{description}</AlertDescription>}
            </div>
        </Alert>
    );
}
