"use client"

import Link from "next/link"
import { usePathname } from "next/navigation"
import { cn } from "@/lib/utils"
import { Button } from "@/components/ui/button"
import { Home, Users, Cpu, LayoutDashboard, History, DoorOpen } from "lucide-react"

const navItems = [
  {
    title: "Bảng điều khiển",
    href: "/dashboard",
    icon: LayoutDashboard,
  },
  {
    title: "Nhà",
    href: "/dashboard/homes",
    icon: Home,
  },
  {
    title: "Vị trí",
    href: "/dashboard/locations",
    icon: DoorOpen,
  },
  {
    title: "Người dùng",
    href: "/dashboard/persons",
    icon: Users,
  },
  {
    title: "Thiết bị",
    href: "/dashboard/devices",
    icon: Cpu,
  }
]

export function DashboardNav() {
  const pathname = usePathname()

  return (
    <nav className="grid gap-2 p-4">
      {navItems.map((item) => (
        <Link key={item.href} href={item.href}>
          <Button
            variant="ghost"
            className={cn(
              "w-full justify-start gap-2 font-normal",
              pathname === item.href && "bg-green-100 text-green-700 hover:bg-green-100 hover:text-green-700",
            )}
          >
            <item.icon className="h-4 w-4" />
            {item.title}
          </Button>
        </Link>
      ))}
    </nav>
  )
}

