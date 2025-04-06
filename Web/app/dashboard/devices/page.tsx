import { DevicesList } from "@/components/devices-list"
import { Button } from "@/components/ui/button"
import { Plus } from "lucide-react"
import Link from "next/link"
import type { Metadata } from "next"

export const metadata: Metadata = {
  title: "Devices - SmartHome Management",
  description: "Manage devices in your SmartHome system",
}

export default function DevicesPage() {
  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-3xl font-bold tracking-tight">Devices</h2>
          <p className="text-muted-foreground">Manage all devices in SmartHome system</p>
        </div>
        <Link href="/dashboard/devices/new">
          <Button className="bg-green-600 hover:bg-green-700">
            <Plus className="mr-2 h-4 w-4" />
            Add Device
          </Button>
        </Link>
      </div>
      <DevicesList />
    </div>
  )
}

