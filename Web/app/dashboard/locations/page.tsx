import { Button } from "@/components/ui/button"
import { Plus } from 'lucide-react'
import Link from "next/link"
import type { Metadata } from "next"
import { LocationsList } from "@/components/locations-list"

export const metadata: Metadata = {
  title: "Vị trí - SmartHome",
  description: "Quản lý vị trí trong hệ thống SmartHome",
}

export default function LocationsPage() {
  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-3xl font-bold tracking-tight">Vị trí</h2>
          <p className="text-muted-foreground">Quản lý tất cả các vị trí trong hệ thống SmartHome</p>
        </div>
        <Link href="/dashboard/locations/new">
          <Button className="bg-green-600 hover:bg-green-700">
            <Plus className="mr-2 h-4 w-4" />
            Thêm vị trí
          </Button>
        </Link>
      </div>
      <LocationsList />
    </div>
  )
}
