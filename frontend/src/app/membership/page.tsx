"use client";

import axios from "axios";
import PricingPage from "./PricingPage";
import { Card } from "@/components/ui/card";

export default function MembershipPage() {
    return (
        <Card className="m-10 bg-white border-0 p-0">
            <div className="p-6 space-y-8">
                <PricingPage />
            </div>
        </Card>
    );
}