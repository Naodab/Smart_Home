import { DevicesList } from "@/components/devices-list"
import { Button } from "@/components/ui/button"
import { Plus } from "lucide-react"
import Link from "next/link"
import type { Metadata } from "next"

export const metadata: Metadata = {
  title: "Thiết bị - SmartHome",
  description: "Quản lý thiết bị trong hệ thống SmartHome",
}

export default function DevicesPage() {
  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-3xl font-bold tracking-tight">Thiết bị</h2>
          <p className="text-muted-foreground">Quản lý tất cả thiết bị trong hệ thống SmartHome</p>
        </div>
        <Link href="/dashboard/devices/new">
          <Button className="bg-green-600 hover:bg-green-700">
            <Plus className="mr-2 h-4 w-4" />
            Thêm  thiết bị
          </Button>
        </Link>
      </div>
      <DevicesList />
    </div>
  )
}

