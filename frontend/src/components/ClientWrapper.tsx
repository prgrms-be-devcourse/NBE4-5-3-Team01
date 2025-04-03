"use client";

import { GlobalAlertProvider } from "./GlobalAlert";

export default function ClientWrapper({ children }: { children: React.ReactNode }) {
    return <GlobalAlertProvider>{children}</GlobalAlertProvider>;
}
