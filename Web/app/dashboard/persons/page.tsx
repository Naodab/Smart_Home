import { PersonsList } from "@/components/persons-list"
import { Button } from "@/components/ui/button"
import { Plus } from "lucide-react"
import Link from "next/link"
import type { Metadata } from "next"

export const metadata: Metadata = {
  title: "Persons - SmartHome Management",
  description: "Manage persons in your SmartHome system",
}

export default function PersonsPage() {
  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-3xl font-bold tracking-tight">Persons</h2>
          <p className="text-muted-foreground">Manage all persons in your SmartHome system</p>
        </div>
        <Link href="/dashboard/persons/new">
          <Button className="bg-green-600 hover:bg-green-700">
            <Plus className="mr-2 h-4 w-4" />
            Add Person
          </Button>
        </Link>
      </div>
      <PersonsList />
    </div>
  )
}

