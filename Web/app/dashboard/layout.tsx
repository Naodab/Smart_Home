import type React from "react"
import { DashboardNav } from "@/components/dashboard-nav"
import { UserNav } from "@/components/user-nav"

export default function DashboardLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <div className="flex min-h-screen flex-col">
      <header className="sticky top-0 z-10 border-b bg-white">
        <div className="flex h-16 items-center justify-between px-4 sm:px-6">
          <h1 className="text-2xl font-bold text-green-700">SmartHome</h1>
          <UserNav />
        </div>
      </header>
      <div className="flex flex-1">
        <aside className="w-64 border-r bg-gray-50">
          <DashboardNav />
        </aside>
        <main className="flex-1 p-6">{children}</main>
      </div>
    </div>
  )
}

