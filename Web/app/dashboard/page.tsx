import { DashboardStats } from "@/components/dashboard-stats"
import type { Metadata } from "next"

export const metadata: Metadata = {
  title: "Bảng điều khiển - SmartHome",
  description: "Bảng điều khiển quản lý SmartHome",
}

export default function DashboardPage() {
  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-3xl font-bold tracking-tight">Bảng điều khiển</h2>
        <p className="text-muted-foreground">Chào mừng đến với bảng điều khiển quản lý SmartHome</p>
      </div>
      <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
        <DashboardStats />
      </div>
    </div>
  )
}

