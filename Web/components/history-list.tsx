"use client"

import { useState } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Badge } from "@/components/ui/badge"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"

// Mock data for history
const initialHistory = [
  {
    id: "1",
    deviceId: "1",
    deviceName: "Living Room Thermostat",
    personId: "1",
    personName: "John Doe",
    homeId: "1",
    homeName: "main.residence@example.com",
    newStatus: "Online",
    timestamp: "2023-05-15 14:30:22",
  },
  {
    id: "2",
    deviceId: "2",
    deviceName: "Kitchen Smart Light",
    personId: "2",
    personName: "Jane Smith",
    homeId: "1",
    homeName: "main.residence@example.com",
    newStatus: "Offline",
    timestamp: "2023-05-14 08:15:10",
  },
  {
    id: "3",
    deviceId: "2",
    deviceName: "Kitchen Smart Light",
    personId: "2",
    personName: "Jane Smith",
    homeId: "1",
    homeName: "main.residence@example.com",
    newStatus: "Online",
    timestamp: "2023-05-14 18:45:33",
  },
  {
    id: "4",
    deviceId: "3",
    deviceName: "Front Door Lock",
    personId: "2",
    personName: "Jane Smith",
    homeId: "1",
    homeName: "main.residence@example.com",
    newStatus: "Offline",
    timestamp: "2023-05-16 22:10:05",
  },
  {
    id: "5",
    deviceId: "4",
    deviceName: "Beach House Camera",
    personId: "3",
    personName: "Bob Johnson",
    homeId: "2",
    homeName: "beach.house@example.com",
    newStatus: "Online",
    timestamp: "2023-05-12 09:20:15",
  },
]

export function HistoryList() {
  const [history, setHistory] = useState(initialHistory)
  const [filters, setFilters] = useState({
    deviceName: "",
    personName: "",
    homeName: "",
    status: "all",
  })

  const handleFilterChange = (field: string, value: string) => {
    setFilters({
      ...filters,
      [field]: value,
    })
  }

  const filteredHistory = history.filter((item) => {
    return (
      (filters.deviceName === "" || item.deviceName.toLowerCase().includes(filters.deviceName.toLowerCase())) &&
      (filters.personName === "" || item.personName.toLowerCase().includes(filters.personName.toLowerCase())) &&
      (filters.homeName === "" || item.homeName.toLowerCase().includes(filters.homeName.toLowerCase())) &&
      (filters.status === "all" || item.newStatus === filters.status)
    )
  })

  return (
    <Card>
      <CardHeader>
        <CardTitle>Lịch sử thay đổi trạng thái thiết bị</CardTitle>
        <CardDescription>Bản ghi tất cả thay đổi trạng thái thiết bị trong hệ thống SmartHome.</CardDescription>
      </CardHeader>
      <CardContent>
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
          <div className="space-y-2">
            <Label htmlFor="deviceName">Tên thiết bị</Label>
            <Input
              id="deviceName"
              placeholder="Lọc theo tên thiết bị"
              value={filters.deviceName}
              onChange={(e) => handleFilterChange("deviceName", e.target.value)}
            />
          </div>
          <div className="space-y-2">
            <Label htmlFor="personName">Tên người thay đổi</Label>
            <Input
              id="personName"
              placeholder="Lọc theo tên người thay đổi"
              value={filters.personName}
              onChange={(e) => handleFilterChange("personName", e.target.value)}
            />
          </div>
          <div className="space-y-2">
            <Label htmlFor="homeName">Email nhà</Label>
            <Input
              id="homeName"
              placeholder="Lọc theo email nhà"
              value={filters.homeName}
              onChange={(e) => handleFilterChange("homeName", e.target.value)}
            />
          </div>
          <div className="space-y-2">
            <Label htmlFor="status">Trạng thái</Label>
            <Select value={filters.status} onValueChange={(value) => handleFilterChange("status", value)}>
              <SelectTrigger>
                <SelectValue placeholder="Tất cả trạng thái" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">Tất cả</SelectItem>
                <SelectItem value="on">Bật</SelectItem>
                <SelectItem value="off">Tắt</SelectItem>
              </SelectContent>
            </Select>
          </div>
        </div>

        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Thiết bị</TableHead>
              <TableHead>Người dùng</TableHead>
              <TableHead>Email nhà</TableHead>
              <TableHead>Trạng thái thay đổi</TableHead>
              <TableHead>Thời gian</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {filteredHistory.length > 0 ? (
              filteredHistory.map((item) => (
                <TableRow key={item.id}>
                  <TableCell>{item.deviceName}</TableCell>
                  <TableCell>{item.personName}</TableCell>
                  <TableCell>{item.homeName}</TableCell>
                  <TableCell>
                    <Badge
                      variant={item.newStatus === "Online" ? "default" : "secondary"}
                      className={item.newStatus === "Online" ? "bg-green-500" : "bg-gray-500"}
                    >
                      {item.newStatus}
                    </Badge>
                  </TableCell>
                  <TableCell>{item.timestamp}</TableCell>
                </TableRow>
              ))
            ) : (
              <TableRow>
                <TableCell colSpan={5} className="text-center py-4 text-muted-foreground">
                  Không có kết quả nào phù hợp với bộ lọc hiện tại.
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </CardContent>
    </Card>
  )
}

