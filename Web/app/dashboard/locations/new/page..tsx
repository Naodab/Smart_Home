"use client"
import { useState } from "react"
import { useRouter } from "next/navigation"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { LocationApi, type ApiError } from "@/components/api-service"
import { useToast } from "@/hooks/use-toast"
import { LocationForm } from "@/components/location-form"

export default function NewLocationPage() {
  const router = useRouter()
  const { toast } = useToast()
  const [isSubmitting, setIsSubmitting] = useState(false)

  const handleSubmit = async (data: any) => {
    setIsSubmitting(true)
    try {
      await LocationApi.create(data)

      toast({
        title: "Thành công",
        description: "Vị trí đã được tạo thành công",
        variant: "success",
      })

      router.push("/dashboard/locations")
    } catch (error) {
      const apiError = error as ApiError
      toast({
        title: "Lỗi",
        description: apiError.message || "Không thể tạo vị trí. Vui lòng thử lại.",
        variant: "destructive",
      })
    } finally {
      setIsSubmitting(false)
    }
  }

  const handleCancel = () => {
    router.push("/dashboard/locations")
  }

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-3xl font-bold tracking-tight">Thêm vị trí mới</h2>
        <p className="text-muted-foreground">Tạo một vị trí mới trong hệ thống SmartHome</p>
      </div>
      <Card>
        <CardHeader>
          <CardTitle>Thông tin vị trí</CardTitle>
          <CardDescription>Nhập thông tin cho vị trí mới</CardDescription>
        </CardHeader>
        <CardContent>
          <LocationForm onSubmit={handleSubmit} onCancel={handleCancel} isSubmitting={isSubmitting} />
        </CardContent>
      </Card>
    </div>
  )
}
