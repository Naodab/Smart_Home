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
        title: "Thành công",
        description: "Người dùng đã được tạo thành công",
        variant: "success",
      })

      router.push("/dashboard/persons")
    } catch (error) {
      const apiError = error as ApiError
      toast({
        title: "Thất bại",
        description: apiError.message || "Không thể tạo người dùng. Vui lòng thử lại.",
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
        <h2 className="text-3xl font-bold tracking-tight">Thêm người dùng mới</h2>
        <p className="text-muted-foreground">Tạo một người dùng mới trong hệ thống SmartHome</p>
      </div>
      <Card>
        <CardHeader>
          <CardTitle>Thông tin người dùng</CardTitle>
          <CardDescription>Nhập thông tin cho người dùng</CardDescription>
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

