import { HomesList } from "@/components/homes-list"
import { Button } from "@/components/ui/button"
import { Plus } from "lucide-react"
import Link from "next/link"
import type { Metadata } from "next"

export const metadata: Metadata = {
  title: "Nhà - SmartHome",
  description: "Quản lý nhà trong hệ thống SmartHome",
}

export default function HomesPage() {
  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-3xl font-bold tracking-tight">Nhà</h2>
          <p className="text-muted-foreground">Quản lý tất cả nhà đã đăng ký</p>
        </div>
        <Link href="/dashboard/homes/new">
          <Button className="bg-green-600 hover:bg-green-700">
            <Plus className="mr-2 h-4 w-4" />
            Thêm nhà
          </Button>
        </Link>
      </div>
      <HomesList />
    </div>
  )
}

