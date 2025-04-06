import { HistoryList } from "@/components/history-list"
import type { Metadata } from "next"

export const metadata: Metadata = {
  title: "History - SmartHome Management",
  description: "View device status change history",
}

export default function HistoryPage() {
  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-3xl font-bold tracking-tight">Device History</h2>
        <p className="text-muted-foreground">View the history of device status changes</p>
      </div>
      <HistoryList />
    </div>
  )
}

