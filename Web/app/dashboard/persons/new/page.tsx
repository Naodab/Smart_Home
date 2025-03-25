"use client"
import { useRouter } from "next/navigation"
import { PersonForm } from "@/components/person-form"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { ApiError, PersonApi } from "@/components/api-service"
import { useToast } from "@/hooks/use-toast"
import { useState } from "react"

export default function NewPersonPage() {
  const router = useRouter()
  const { toast } = useToast()
  const [isSubmitting, setIsSubmitting] = useState(false)

  const handleSubmit = async (data: any) => {
    setIsSubmitting(true)
    try {
      await PersonApi.create(data)

      toast({
        title: "Success",
        description: "Person created successfully",
        variant: "success",
      })

      router.push("/dashboard/persons")
    } catch (error) {
      const apiError = error as ApiError
      toast({
        title: "Error",
        description: apiError.message || "Failed to create person. Please try again.",
        variant: "destructive",
      })
    } finally {
      setIsSubmitting(false)
    }
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
          <PersonForm onSubmit={handleSubmit} onCancel={handleCancel} isSubmitting={isSubmitting}/>
        </CardContent>
      </Card>
    </div>
  )
}

