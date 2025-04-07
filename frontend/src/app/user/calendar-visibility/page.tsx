"use client";

import { useEffect, useState } from "react";
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group";
import { Label } from "@/components/ui/label";
import { Button } from "@/components/ui/button";
import { AxiosError } from "axios";
import { fetchUser, updateCalendarVisibility } from "@/lib/api/user";
import { useGlobalAlert } from "@/components/GlobalAlert";
import { User } from "@/types/user";
import { cn } from "@/lib/utils";
import { USER_CALENDAR_VISIBILITY_OPTIONS } from "@/types/user";

export default function CalendarVisibilitySetting() {
  const [calendarVisibility, setCalendarVisibility] = useState<string | null>(null);
  const { setAlert } = useGlobalAlert();

  useEffect(() => {
    async function initCurrentUser() {
      try {
        const response = await fetchUser(`/user/byCookie`);

        const currentUser: User = response.data.data;

        setCalendarVisibility(currentUser.calendarVisibility.toLowerCase());
      } catch (error) {
        if (error instanceof AxiosError) {
          setAlert({
            code: error.response!.status.toString(),
            message: error.response!.data.msg,
          });
        }
      }
    }

    initCurrentUser();
  }, []);

  const handleSaveButtonClick = async () => {
    try {
      const response = await updateCalendarVisibility(calendarVisibility!);

      setAlert({
        code: response.status.toString(),
        message: response.data.msg,
      });
    } catch (error) {
      if (error instanceof AxiosError) {
        setAlert({
          code: "500",
          message: "캘린더 공개 여부 수정에 실패했습니다.",
        });
      }
    }
  };

  return (
    calendarVisibility !== null && (
      <div className="flex items-center justify-center min-h-screen bg-gray-50">
        <div className="w-full max-w-md p-6 space-y-6 rounded-2xl drop-shadow-md bg-white text-[#393D3F]">
          <div className="space-y-1">
            <h2 className="text-2xl font-semibold text-center">캘린더 공개 설정</h2>
            <h3 className="text-base text-[#889296] text-center">
              아래 설정 중 한 가지를 선택하세요
            </h3>
          </div>
          <RadioGroup
            value={calendarVisibility}
            onValueChange={setCalendarVisibility}
            className="my-10 space-y-2"
          >
            {USER_CALENDAR_VISIBILITY_OPTIONS.map((item) => (
              <div
                key={item.value}
                className={cn(
                  "rounded-2xl p-5 flex items-center justify-between",
                  "cursor-pointer transition-all drop-shadow-md",
                  calendarVisibility === item.value ? "bg-[#E8E0FF]" : "bg-white"
                )}
                onClick={() => setCalendarVisibility(item.value)}
              >
                <div className="flex items-center space-x-4">
                  <RadioGroupItem
                    value={item.value}
                    id={item.value}
                    className={cn(
                      "after:content-[''] after:absolute after:top-1 after:left-1",
                      "after:w-3 after:h-3 after:rounded-full after:bg-[#C8B6FF]",
                      "after:opacity-0 data-[state=checked]:after:opacity-100",
                      "sr-only"
                    )}
                  />
                  <Label
                    htmlFor={item.value}
                    className="text-lg font-medium text-[#393D3F]"
                  >
                    {item.label}
                  </Label>
                </div>
                <span className="text-base text-[#889296]">{item.description}</span>
              </div>
            ))}
          </RadioGroup>

          <Button
            onClick={handleSaveButtonClick}
            size="lg"
            className={cn(
              "w-full text-lg py-3",
              "bg-[#E8E0FF] hover:bg-[#C8B6FF] hover:text-white",
              "text-[#393D3F] rounded-2xl drop-shadow-md transition-all"
            )}
          >
            저장하기
          </Button>
        </div>
      </div>
    )
  );
}