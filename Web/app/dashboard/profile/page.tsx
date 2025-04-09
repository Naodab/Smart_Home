import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import type { Metadata } from "next"

export const metadata: Metadata = {
  title: "Hồ sơ - SmartHome",
  description: "Quản lý cài đặt hồ sơ của bạn",
}

export default function ProfilePage() {
  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-3xl font-bold tracking-tight">Hồ sơ</h2>
        <p className="text-muted-foreground">Quản lý cài đặt tài khoản và tùy chọn của bạn</p>
      </div>
      <Card>
        <CardHeader>
          <CardTitle>Hồ sơ người dùng</CardTitle>
          <CardDescription>Xem và cập nhật thông tin cá nhân của bạn</CardDescription>
        </CardHeader>
        <CardContent>
          <p className="text-muted-foreground">
          </p>
        </CardContent>
      </Card>
    </div>
  )
}
