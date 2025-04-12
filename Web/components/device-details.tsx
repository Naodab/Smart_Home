"use client"

import { useState, useEffect } from "react"
import { Card, CardContent } from "@/components/ui/card"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Blinds, Cpu, DoorOpen, Fan, History, LightbulbIcon, Loader2, MapPin, Power } from "lucide-react"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Label } from "@/components/ui/label"
import { Device, DeviceApi, type ApiError } from "@/components/api-service"
import { useToast } from "@/hooks/use-toast"
interface DeviceDetailsProps {
  device: Device
  onStatusChange: (deviceId: string, newStatus: string) => void
}

export function DeviceDetails({ device, onStatusChange }: DeviceDetailsProps) {
  const [deviceHistory, setDeviceHistory] = useState<any[]>([])
  const [isChangeStatusDialogOpen, setIsChangeStatusDialogOpen] = useState(false)
  const [newStatus, setNewStatus] = useState(device.status)
  const [selectedPerson, setSelectedPerson] = useState("")
  const { toast } = useToast()
  const [isSubmitting, setIsSubmitting] = useState(false)

  // Mock data for persons who can change device status
  const persons = [
    { id: "1", name: "John Doe" },
    { id: "2", name: "Jane Smith" },
    { id: "3", name: "Bob Johnson" },
  ]

  useEffect(() => {
    setDeviceHistory(device.histories || [])
    setNewStatus(device.status)
  }, [device.id, device.status])

  const handleStatusChange = async () => {
    if (!selectedPerson) return

    setIsSubmitting(true)
    try {
      await DeviceApi.updateStatus(device.id, newStatus ? "Hoạt động" : "Không hoạt động", selectedPerson)

      onStatusChange(device.id, newStatus)

      const newHistoryEntry = {
        id: `new-${Date.now()}`,
        personId: selectedPerson,
        personName: persons.find((p) => p.id === selectedPerson)?.name ?? "Unknown",
        newStatus,
        timestamp: new Date().toISOString().replace("T", " ").substring(0, 19),
      }

      setDeviceHistory([newHistoryEntry, ...deviceHistory])

      toast({
        title: "Thành công",
        description: `Thiết bị được thay đổi đến ${newStatus}`,
        variant: "success",
      })
    } catch (error) {
      const apiError = error as ApiError
      toast({
        title: "Thất bị",
        description: apiError.message || "Không thể thay đổi trạng thái của thiết bị. Vui lòng thử lại.",
        variant: "destructive",
      })
    } finally {
      setIsSubmitting(false)
      setIsChangeStatusDialogOpen(false)
    }
  }

  const getDeviceTypeIcon = () => {
    switch (device.type) {
      case "light":
        return <LightbulbIcon className="h-6 w-6 text-yellow-500" />
      case "door":
        return <DoorOpen className="h-6 w-6 text-blue-500" />
      case "curtain":
        return <Blinds className="h-6 w-6 text-purple-500" />
      case "fan":
        return <Fan className="h-6 w-6 text-green-500" />
      default:
        return <Cpu className="h-6 w-6 text-green-600" />
    }
  }

  const getDeviceTypeDisplay = () => {
    return device.type.charAt(0).toUpperCase() + device.type.slice(1)
  }

  return (
    <Card>
      <CardContent className="pt-6">
        <div className="flex items-center gap-4 mb-6">
          <div className="p-2 bg-green-100 rounded-full">{getDeviceTypeIcon()}</div>
          <h3 className="text-xl font-semibold">{device.name}</h3>
        </div>

        <div className="grid grid-cols-4 gap-4 mb-6">
          <div className="space-y-1">
            <p className="text-sm text-muted-foreground">Mã thiết bị</p>
            <p className="font-medium">{device.id}</p>
          </div>
          <div className="space-y-1">
            <p className="text-sm text-muted-foreground">Loại</p>
            <p className="font-medium">{getDeviceTypeDisplay()}</p>
          </div>
          <div className="space-y-1">
            <p className="text-sm text-muted-foreground">Trạng thái</p>
            {device.type === "fan" ? (
              <Badge
                variant={device.status === "0" ? "secondary" : "default"}
                className={
                  device.status === "0"
                    ? "bg-gray-500"
                    : device.status === "1"
                      ? "bg-green-300"
                      : device.status === "2"
                        ? "bg-green-500"
                        : "bg-green-700"
                }
              >
                Mức độ {device.status}
              </Badge>
            ) : (
              <Badge
                variant={device.status === "on" ? "default" : "secondary"}
                className={device.status === "off" ? "bg-green-500" : "bg-gray-500"}
              >
                {device.status === "on" ? "bật" : "tắt"}
              </Badge>
            )}
          </div>
          <div className="space-y-1">
            <p className="text-sm text-muted-foreground">Email nhà</p>
            <p className="font-medium">{device.location.home.email}</p>
          </div>
          <div className="space-y-1">
            <p className="text-sm text-muted-foreground">Vị trí</p>
            <div className="flex items-center gap-2">
              {device.location.name ? (
                <>
                  <MapPin className="h-4 w-4 text-green-600" />
                  <p className="font-medium">{device.location.name}</p>
                </>
              ) : (
                <p className="text-muted-foreground italic">Chưa gán vị trí</p>
              )}
            </div>
          </div>
        </div>
        
        <div className="flex justify-end mb-6">
          <Button
            className="bg-green-600 hover:bg-green-700"
            onClick={() => setIsChangeStatusDialogOpen(true)}
          >
            <Power className="mr-2 h-4 w-4" />
            Change Status
          </Button>
        </div>

        <Tabs defaultValue="history" className="mt-6">
          <TabsList className="grid w-full grid-cols-1">
            <TabsTrigger value="history" className="flex items-center gap-2">
              <History className="h-4 w-4" />
              Lịch sử thay đổi trạng thái
            </TabsTrigger>
          </TabsList>

          <TabsContent value="history" className="mt-4">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Người dùng</TableHead>
                  <TableHead>Trạng thái thay đổi</TableHead>
                  <TableHead>Thời gian</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {deviceHistory.length > 0 ? (
                  deviceHistory.map((history) => (
                    <TableRow key={history.id}>
                      <TableCell>{history.personName}</TableCell>
                      <TableCell>
                        {history.newStatus === "0" ||
                        history.newStatus === "1" ||
                        history.newStatus === "2" ||
                        history.newStatus === "3" ? (
                          <Badge
                            variant={history.newStatus === "0" ? "secondary" : "default"}
                            className={
                              history.newStatus === "0"
                                ? "bg-gray-500"
                                : history.newStatus === "1"
                                  ? "bg-green-300"
                                  : history.newStatus === "2"
                                    ? "bg-green-500"
                                    : "bg-green-700"
                            }
                          >
                            Mức độ {history.newStatus}
                          </Badge>
                        ) : (
                          <Badge
                            variant={history.newStatus === "on" ? "default" : "secondary"}
                            className={history.newStatus === "off" ? "bg-green-500" : "bg-gray-500"}
                          >
                            {history.newStatus === "on" ? "bật" : "tắt"}
                          </Badge>
                        )}
                      </TableCell>
                      <TableCell>{history.timestamp}</TableCell>
                    </TableRow>
                  ))
                ) : (
                  <TableRow>
                    <TableCell colSpan={3} className="text-center py-4 text-muted-foreground">
                      Không có lịch sử thay đổi trạng thái
                    </TableCell>
                  </TableRow>
                )}
              </TableBody>
            </Table>
          </TabsContent>
        </Tabs>

        {/* Change Status Dialog */}
        <Dialog open={isChangeStatusDialogOpen} onOpenChange={setIsChangeStatusDialogOpen}>
          <DialogContent className="sm:max-w-[425px]">
            <DialogHeader>
              <DialogTitle>Thay đổi trạng thái thiết bị</DialogTitle>
              <DialogDescription>Cập nhật trạng thái của thiết bị này.</DialogDescription>
            </DialogHeader>
            <div className="grid gap-4 py-4">
              <div className="space-y-2">
                <Label htmlFor="status">Trạng thái mới.</Label>
                {device.type === "fan" ? (
                  <Select value={newStatus} onValueChange={setNewStatus}>
                    <SelectTrigger>
                      <SelectValue placeholder="Chọn mức độ  của quạt" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="0">Level 0 (Tắt)</SelectItem>
                      <SelectItem value="1">Level 1 (Thấp)</SelectItem>
                      <SelectItem value="2">Level 2 (Trung bình)</SelectItem>
                      <SelectItem value="3">Level 3 (Cao)</SelectItem>
                    </SelectContent>
                  </Select>
                ) : (
                  <Select value={newStatus} onValueChange={setNewStatus}>
                    <SelectTrigger>
                      <SelectValue placeholder="Chọn trạng thái" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="on">Bật</SelectItem>
                      <SelectItem value="off">Tắt</SelectItem>
                    </SelectContent>
                  </Select>
                )}
              </div>
              <div className="space-y-2">
                <Label htmlFor="person">Người thay đổi</Label>
                <Select
                  value={selectedPerson}
                  onValueChange={setSelectedPerson}
                >
                  <SelectTrigger>
                    <SelectValue placeholder="Chọn người" />
                  </SelectTrigger>
                  <SelectContent>
                    {persons.map((person) => (
                      <SelectItem key={person.id} value={person.id}>
                        {person.name}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
            </div>
            <DialogFooter>
              <Button
                variant="outline"
                onClick={() => setIsChangeStatusDialogOpen(false)}
                disabled={isSubmitting}
              >
                Cancel
              </Button>
              <Button
                className="bg-green-600 hover:bg-green-700"
                onClick={handleStatusChange}
                disabled={!selectedPerson || isSubmitting}
              >
                {isSubmitting ? (
                  <>
                    <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                    Đang cập nhật trang thái...
                  </>
                ) : (
                  "Cập nhật trạng thái"
                )}
              </Button>
            </DialogFooter>
          </DialogContent>
        </Dialog>
      </CardContent>
    </Card>
  )
}

