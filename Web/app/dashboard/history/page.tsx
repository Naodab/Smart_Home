import { HistoryList } from "@/components/history-list"
import type { Metadata } from "next"

export const metadata: Metadata = {
  title: "Lịch sử - SmartHome",
  description: "Xem lịch sử thay đổi trạng thái thiết bị",
}

export default function HistoryPage() {
  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-3xl font-bold tracking-tight">Lịch sử thiết bị</h2>
        <p className="text-muted-foreground">Xem lịch sử thay đổi trạng thái thiết bị</p>
      </div>
      <HistoryList />
    </div>
  )
}

