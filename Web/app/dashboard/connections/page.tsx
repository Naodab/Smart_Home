import { ConnectionsManager } from "@/components/connections-manager"
import type { Metadata } from "next"

export const metadata: Metadata = {
  title: "Person-Home Connections - SmartHome Management",
  description: "Manage connections between persons and homes",
}

export default function ConnectionsPage() {
  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-3xl font-bold tracking-tight">Person-Home Connections</h2>
        <p className="text-muted-foreground">Manage which persons are connected to which homes</p>
      </div>
      <ConnectionsManager />
    </div>
  )
}

