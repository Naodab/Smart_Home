"use client"
import { useRouter } from "next/navigation"
import { PersonForm } from "@/components/person-form"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"

export default function NewPersonPage() {
  const router = useRouter()

  const handleSubmit = (data: any) => {
    // In a real app, you would send this data to your API
    console.log("Creating new person:", data)

    // Navigate back to persons list
    router.push("/dashboard/persons")
  }

  const handleCancel = () => {
    router.push("/dashboard/persons")
  }

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-3xl font-bold tracking-tight">Add New Person</h2>
        <p className="text-muted-foreground">Create a new person in your SmartHome system</p>
      </div>
      <Card>
        <CardHeader>
          <CardTitle>Person Details</CardTitle>
          <CardDescription>Enter the details for the new person</CardDescription>
        </CardHeader>
        <CardContent>
          <PersonForm onSubmit={handleSubmit} onCancel={handleCancel} />
        </CardContent>
      </Card>
    </div>
  )
}

