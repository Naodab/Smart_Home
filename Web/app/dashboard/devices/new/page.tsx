"use client"
import { useRouter } from "next/navigation"
import { DeviceForm } from "@/components/device-form"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"

export default function NewDevicePage() {
  const router = useRouter()

  const handleSubmit = (data: any) => {
    // In a real app, you would send this data to your API
    console.log("Creating new device:", data)

    // Navigate back to devices list
    router.push("/dashboard/devices")
  }

  const handleCancel = () => {
    router.push("/dashboard/devices")
  }

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-3xl font-bold tracking-tight">Add New Device</h2>
        <p className="text-muted-foreground">Create a new device in your SmartHome system</p>
      </div>
      <Card>
        <CardHeader>
          <CardTitle>Device Details</CardTitle>
          <CardDescription>Enter the details for the new device</CardDescription>
        </CardHeader>
        <CardContent>
          <DeviceForm onSubmit={handleSubmit} onCancel={handleCancel} />
        </CardContent>
      </Card>
    </div>
  )
}

