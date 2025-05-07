import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Home, Users, Cpu } from "lucide-react"

export function DashboardStats() {
  return (
    <>
      <Card>
        <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardTitle className="text-sm font-medium">Tổng số nhà</CardTitle>
          <Home className="h-4 w-4 text-green-600" />
        </CardHeader>
        <CardContent>
          <div className="text-2xl font-bold">1</div>
          <p className="text-xs text-muted-foreground">+1 từ tháng  trước</p>
        </CardContent>
      </Card>
      <Card>
        <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardTitle className="text-sm font-medium">Tổng số người dùng</CardTitle>
          <Users className="h-4 w-4 text-green-600" />
        </CardHeader>
        <CardContent>
          <div className="text-2xl font-bold">4</div>
          <p className="text-xs text-muted-foreground">+1 kể từ tháng trước</p>
        </CardContent>
      </Card>
      <Card>
        <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardTitle className="text-sm font-medium">Tổng số thiết bị</CardTitle>
          <Cpu className="h-4 w-4 text-green-600" />
        </CardHeader>
        <CardContent>
          <div className="text-2xl font-bold">10</div>
          <p className="text-xs text-muted-foreground">+10 kể từ tháng trước</p>
        </CardContent>
      </Card>
    </>
  )
}

