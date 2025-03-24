"use client"
import { useState } from "react"
import { useRouter } from "next/navigation"
import { HomeForm } from "@/components/home-form"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { HomeApi, type ApiError } from "@/components/api-service"
import { useToast } from "@/hooks/use-toast"

export default function NewHomePage() {
  const router = useRouter()
  const { toast } = useToast()
  const [isSubmitting, setIsSubmitting] = useState(false)

  const handleSubmit = async (data: any) => {
    setIsSubmitting(true)
    try {
      await HomeApi.create(data)

      toast({
        title: "Success",
        description: "Home created successfully",
        variant: "success",
      })

      router.push("/dashboard/homes")
    } catch (error) {
      const apiError = error as ApiError
      toast({
        title: "Error",
        description: apiError.message || "Failed to create home. Please try again.",
        variant: "destructive",
      })
    } finally {
      setIsSubmitting(false)
    }
  }

  const handleCancel = () => {
    router.push("/dashboard/homes")
  }

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-3xl font-bold tracking-tight">Add New Home</h2>
        <p className="text-muted-foreground">Create a new home with email in your SmartHome system</p>
      </div>
      <Card>
        <CardHeader>
          <CardTitle>Home Details</CardTitle>
          <CardDescription>Enter the details for the new home</CardDescription>
        </CardHeader>
        <CardContent>
          <HomeForm onSubmit={handleSubmit} onCancel={handleCancel} isSubmitting={isSubmitting} />
        </CardContent>
      </Card>
    </div>
  )
}

