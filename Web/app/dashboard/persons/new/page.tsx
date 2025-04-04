"use client"
import { useRouter } from "next/navigation"
import { PersonForm } from "@/components/person-form"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { ApiError, HomeApi, Person, PersonApi } from "@/components/api-service"
import { useToast } from "@/hooks/use-toast"
import { useEffect, useState } from "react"
import { HomeToSelect } from "@/components/api-service"

export default function NewPersonPage() {
  const [isLoading, setIsLoading] = useState(false)
  const router = useRouter()
  const { toast } = useToast()
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [homes, setHomes] = useState<HomeToSelect[]>([])

  useEffect(() => {
    (async function fetchHomes() {
      try {
        setIsLoading(true)
        const data = await HomeApi.getEmails();
        setHomes(data);
        setIsLoading(false)
      } catch (error) {
        console.error("Failed to fetch homes", error)
        setIsLoading(false)
      }
    })()
  }, [])

  const handleSubmit = async (data: Person) => {
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
        <p className="text-muted-foreground">Create a new person in SmartHome system</p>
      </div>
      <Card>
        <CardHeader>
          <CardTitle>Person Details</CardTitle>
          <CardDescription>Enter the details for the new person</CardDescription>
        </CardHeader>
        <CardContent>
          <PersonForm
            onSubmit={handleSubmit}
            onCancel={handleCancel}
            isSubmitting={isSubmitting}
            homes={homes}
          />
        </CardContent>
      </Card>
    </div>
  )
}

