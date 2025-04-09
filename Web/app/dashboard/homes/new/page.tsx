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
        title: "Thành công",
        description: "Nhà được tạo thành công",
        variant: "success",
      })

      router.push("/dashboard/homes")
    } catch (error) {
      const apiError = error as ApiError
      toast({
        title: "Lỗi",
        description: apiError.message || "Không thể tạo nhà. Vui lòng thử lại.",
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
        <h2 className="text-3xl font-bold tracking-tight">Thêm nhà mới</h2>
        <p className="text-muted-foreground">Tạo một ngôi nhà mới với email trong hệ thống SmartHome</p>
      </div>
      <Card>
        <CardHeader>
          <CardTitle>Thông tin nhà</CardTitle>
          <CardDescription>Nhập thông tin cho nhà mới</CardDescription>
        </CardHeader>
        <CardContent>
          <HomeForm onSubmit={handleSubmit} onCancel={handleCancel} isSubmitting={isSubmitting} />
        </CardContent>
      </Card>
    </div>
  )
}

