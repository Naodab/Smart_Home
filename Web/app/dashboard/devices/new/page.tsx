"use client"
import { useRouter } from "next/navigation"
import { DeviceForm } from "@/components/device-form"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { DeviceApi } from "@/components/api-service"
import { useState } from "react"
import { useToast } from "@/hooks/use-toast"
import { ApiError } from "@/components/api-service"

export default function NewDevicePage() {
  const router = useRouter()
  const [isSubmitting, setIsSubmitting] = useState(false)
  const { toast } = useToast()

  const handleSubmit = async (data: any) => {
    setIsSubmitting(true)
    try {
      await DeviceApi.create(data)

      toast({
        title: "Success",
        description: "Device created successfully",
        variant: "success",
      })

      router.push("/dashboard/devices")
    } catch (error) {
      const apiError = error as ApiError
      toast({
        title: "Error",
        description: apiError.message || "Failed to create device. Please try again.",
        variant: "destructive",
      })
    } finally {
      setIsSubmitting(false)
    }
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
          <DeviceForm onSubmit={handleSubmit} onCancel={handleCancel} isSubmitting={isSubmitting} />
        </CardContent>
      </Card>
    </div>
  )
}

