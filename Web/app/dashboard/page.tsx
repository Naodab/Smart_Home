import { DashboardStats } from "@/components/dashboard-stats"
import type { Metadata } from "next"

export const metadata: Metadata = {
  title: "Dashboard - SmartHome Management",
  description: "SmartHome management dashboard",
}

export default function DashboardPage() {
  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-3xl font-bold tracking-tight">Dashboard</h2>
        <p className="text-muted-foreground">Welcome to your SmartHome management dashboard</p>
      </div>
      <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
        <DashboardStats />
      </div>
    </div>
  )
}

