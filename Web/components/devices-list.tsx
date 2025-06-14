"use client"

import type React from "react"

import { useEffect, useState } from "react"
import { Button } from "@/components/ui/button"
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle
} from "@/components/ui/card"
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow
} from "@/components/ui/table"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog"
import { Badge } from "@/components/ui/badge"
import { Edit, Eye, Trash2, Search, X } from "lucide-react"
import { DeviceForm } from "./device-form"
import { DeviceDetails } from "./device-details"
import { Input } from "@/components/ui/input"
import {
  Pagination,
  PaginationContent,
  PaginationEllipsis,
  PaginationItem,
  PaginationLink,
  PaginationNext,
  PaginationPrevious,
} from "@/components/ui/pagination"
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue
} from "@/components/ui/select"
import { ApiError, Device, DeviceApi } from "./api-service"
import { useToast } from "@/hooks/use-toast"

export function DevicesList() {
  const { toast } = useToast()
  const [devices, setDevices] = useState<Device[]>([])
  const [selectedDevice, setSelectedDevice] = useState<Device | null>(null)
  const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false)
  const [isViewDialogOpen, setIsViewDialogOpen] = useState(false)
  const [isEditDialogOpen, setIsEditDialogOpen] = useState(false)
  const [searchTerm, setSearchTerm] = useState("")
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [typeFilter, setTypeFilter] = useState("all")
  const [statusFilter, setStatusFilter] = useState("all")

  useEffect(() => {
    const fetchDevices = async () => {
      try {
        const response = await DeviceApi.getAll()
        setDevices(response)
      } catch (error) {
        console.error("Error fetching devices:", error)
      }
    }
    fetchDevices()
  }, [])

  const [currentPage, setCurrentPage] = useState(1)
  const [itemsPerPage, setItemsPerPage] = useState(5)

  const handleDelete = async (id: string) => {
    setIsSubmitting(true)
    try {
      await DeviceApi.delete(id)
      setDevices(devices.filter((device) => device.id !== id))
      toast({
        title: "Thành công",
        description: "Xóa thiết bị thành công",
        variant: "success",
      })
    } catch (error) {
      const apiError = error as ApiError
      toast({
        title: "Lỗi",
        description: apiError.message || "Không thể xóa thiết bị. Vui lòng thử lại.",
        variant: "destructive",
      })
    } finally {
      setIsSubmitting(false)
      setIsDeleteDialogOpen(false)
    }
  }

  const handleEdit = async (updatedDevice: Device) => {
    console.log(updatedDevice)
    setIsSubmitting(true)
    try {
      await DeviceApi.update(updatedDevice.id, updatedDevice)
      setDevices(devices.map((device) => (device.id === updatedDevice.id ? updatedDevice : device)))
      toast({
        title: "Thành công",
        description: "Thiết bị đã được cập nhật thành công",
        variant: "success",
      })
    } catch (error) {
      const apiError = error as ApiError
      toast({
        title: "Lỗi",
        description: apiError.message || "Không thể cập nhật thiết bị. Vui lòng thử lại.",
        variant: "destructive",
      })
    } finally {
      setIsSubmitting(false)
      setIsEditDialogOpen(false)
    }
  }
  
  const handleStatusChange = (deviceId: string, newStatus: string) => {
    setDevices(devices.map((device) => (device.id === deviceId ? { ...device, status: newStatus } : device)))
  }

  const filteredDevices = devices.filter((device) => {
    const matchesSearch =
      device.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      device.location.home.email.toLowerCase().includes(searchTerm.toLowerCase())

    const matchesType = typeFilter === "all" || device.type === typeFilter

    return matchesSearch && matchesType
  })

  const totalItems = filteredDevices.length
  const totalPages = Math.ceil(totalItems / itemsPerPage)
  const indexOfLastItem = currentPage * itemsPerPage
  const indexOfFirstItem = indexOfLastItem - itemsPerPage
  const currentItems = filteredDevices.slice(indexOfFirstItem, indexOfLastItem)

  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchTerm(e.target.value)
    setCurrentPage(1)
  }

  const handleTypeFilterChange = (value: string) => {
    setTypeFilter(value)
    setCurrentPage(1)
  }
  
  const handleStatusFilterChange = (value: string) => {
    setStatusFilter(value)
    setCurrentPage(1)
  }

  const paginate = (pageNumber: number) => {
    if (pageNumber > 0 && pageNumber <= totalPages) {
      setCurrentPage(pageNumber)
    }
  }

  const pageNumbers: number[] = []
  for (let i = 1; i <= totalPages; i++) {
    pageNumbers.push(i)
  }

  const getVisiblePageNumbers = () => {
    if (totalPages <= 5) {
      return pageNumbers
    }

    if (currentPage <= 3) {
      return [1, 2, 3, 4, 5]
    }

    if (currentPage >= totalPages - 2) {
      return [totalPages - 4, totalPages - 3, totalPages - 2, totalPages - 1, totalPages]
    }

    return [currentPage - 2, currentPage - 1, currentPage, currentPage + 1, currentPage + 2]
  }

  const visiblePageNumbers = getVisiblePageNumbers()
  
  const getDeviceTypeDisplay = (type: string) => {
    return type.charAt(0).toUpperCase() + type.slice(1)
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>Tất cả thiết bị</CardTitle>
        <CardDescription>Danh sách tất cả thiết bị trong hệ thống SmartHome.</CardDescription>
      </CardHeader>
      <div className="px-6 mb-4">
        <div className="flex flex-col md:flex-row gap-4">
          <div className="relative flex-1">
            <Input
              placeholder="Tìm kiếm thiết bị theo tên hoặc email nhà..."
              value={searchTerm}
              onChange={handleSearchChange}
              className="pl-10"
            />
            <div className="absolute left-3 top-1/2 -translate-y-1/2">
              <Search className="h-4 w-4 text-muted-foreground" />
            </div>
            {searchTerm && (
              <Button
                variant="ghost"
                size="sm"
                className="absolute right-1 top-1/2 -translate-y-1/2 h-7 w-7 p-0"
                onClick={() => {
                  setSearchTerm("")
                  setCurrentPage(1)
                }}
              >
                <X className="h-4 w-4" />
                <span className="sr-only">Xóa tìm kiếm</span>
              </Button>
            )}
          </div>
          <div className="w-full md:w-[200px]">
            <Select value={typeFilter} onValueChange={handleTypeFilterChange}>
              <SelectTrigger>
                <SelectValue placeholder="Lọc theo  loại" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">Tất cả</SelectItem>
                <SelectItem value="door">Cửa</SelectItem>
                <SelectItem value="light">Đèn</SelectItem>
                <SelectItem value="curtain">Rèm</SelectItem>
                <SelectItem value="fan">Quạt</SelectItem>
              </SelectContent>
            </Select>
          </div>
          <div className="w-full md:w-[150px]">
            <Select value={statusFilter} onValueChange={handleStatusFilterChange}>
              <SelectTrigger>
                <SelectValue placeholder="Lọc theo trạng thái" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">Tất cả</SelectItem>
                <SelectItem value="on">Bật</SelectItem>
                <SelectItem value="off">Tắt</SelectItem>
                <SelectItem value="open">Đèn - Mở</SelectItem>
                <SelectItem value="close">Đèn - Đóng</SelectItem>
                <SelectItem value="0">Quạt - Tắt</SelectItem>
                <SelectItem value="1">Quạt - Thấp</SelectItem>
                <SelectItem value="2">Quạt - Trung bình</SelectItem>
                <SelectItem value="3">Quạt - Cao</SelectItem>
              </SelectContent>
            </Select>
          </div>
        </div>
      </div>
      <CardContent>
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Mã</TableHead>
              <TableHead>Tên</TableHead>
              <TableHead>Loại</TableHead>
              <TableHead>Trạng thái</TableHead>
              <TableHead>Email nhà</TableHead>
              <TableHead>Vị trí</TableHead>
              <TableHead className="text-right">Hành động</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {currentItems.length > 0 ? (
              currentItems.map((device) => (
                <TableRow key={device.id}>
                  <TableCell>{device.id}</TableCell>
                  <TableCell className="font-medium">{device.name}</TableCell>
                  <TableCell>{getDeviceTypeDisplay(device.type)}</TableCell>
                  <TableCell>
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
                        Mức {device.status}
                      </Badge>
                    ) : (
                      <Badge
                        variant={(device.status === "on" || device.status === "open") ? "default" : "secondary"}
                        className={(device.status === "on" || device.status === "open") ? "bg-green-500" : "bg-gray-500"}
                      >
                        {device.type === 'light' 
                          ? (device.status === "on" ? "Bật" : "Tắt") 
                          : (device.status === "open" ? "Mở" : "Đóng")}
                      </Badge>
                    )}
                  </TableCell>
                  <TableCell>{device.location?.home.email}</TableCell>
                  <TableCell>{device.location?.name || "Chưa gán"}</TableCell>
                  <TableCell className="text-right">
                    <div className="flex justify-end gap-2">
                      <Button
                        variant="outline"
                        size="icon"
                        onClick={() => {
                          setSelectedDevice(device)
                          setIsViewDialogOpen(true)
                        }}
                      >
                        <Eye className="h-4 w-4 text-green-600" />
                      </Button>
                      <Button
                        variant="outline"
                        size="icon"
                        onClick={() => {
                          setSelectedDevice(device)
                          setIsEditDialogOpen(true)
                        }}
                      >
                        <Edit className="h-4 w-4 text-blue-600" />
                      </Button>
                      <Button
                        variant="outline"
                        size="icon"
                        onClick={() => {
                          setSelectedDevice(device)
                          setIsDeleteDialogOpen(true)
                        }}
                      >
                        <Trash2 className="h-4 w-4 text-red-600" />
                      </Button>
                    </div>
                  </TableCell>
                </TableRow>
              ))
            ) : (
              <TableRow>
                <TableCell colSpan={5} className="text-center py-4 text-muted-foreground">
                  {searchTerm || typeFilter !== "all" || statusFilter !== "all" 
                    ? "Không có thiết bị phù hợp với tìm kiếm" 
                    : "Không có thiết bị nào trong danh sách"}
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>

        {/* Pagination Controls */}
        {totalItems > 0 && (
          <div className="mt-6 flex items-center justify-between">
            <div className="flex items-center gap-2">
              <span className="text-sm text-muted-foreground">Số thiết bị mỗi trang:</span>
              <Select
                value={itemsPerPage.toString()}
                onValueChange={(value) => {
                  setItemsPerPage(Number(value))
                  setCurrentPage(1)
                }}
              >
                <SelectTrigger className="h-8 w-[70px]">
                  <SelectValue placeholder="5" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="5">5</SelectItem>
                  <SelectItem value="10">10</SelectItem>
                  <SelectItem value="15">15</SelectItem>
                  <SelectItem value="20">20</SelectItem>
                </SelectContent>
              </Select>
              <span className="text-sm text-muted-foreground">
                Hiển thị {indexOfFirstItem + 1}-{Math.min(indexOfLastItem, totalItems)} của {totalItems}
              </span>
            </div>

            <Pagination>
              <PaginationContent>
                <PaginationItem>
                  <PaginationPrevious
                    onClick={() => paginate(currentPage - 1)}
                    className={currentPage === 1 ? "pointer-events-none opacity-50" : ""}
                  />
                </PaginationItem>

                {totalPages > 5 && currentPage > 3 && (
                  <>
                    <PaginationItem>
                      <PaginationLink onClick={() => paginate(1)}>1</PaginationLink>
                    </PaginationItem>
                    <PaginationItem>
                      <PaginationEllipsis />
                    </PaginationItem>
                  </>
                )}

                {visiblePageNumbers.map((number) => (
                  <PaginationItem key={number}>
                    <PaginationLink isActive={currentPage === number} onClick={() => paginate(number)}>
                      {number}
                    </PaginationLink>
                  </PaginationItem>
                ))}

                {totalPages > 5 && currentPage < totalPages - 2 && (
                  <>
                    <PaginationItem>
                      <PaginationEllipsis />
                    </PaginationItem>
                    <PaginationItem>
                      <PaginationLink onClick={() => paginate(totalPages)}>{totalPages}</PaginationLink>
                    </PaginationItem>
                  </>
                )}

                <PaginationItem>
                  <PaginationNext
                    onClick={() => paginate(currentPage + 1)}
                    className={currentPage === totalPages ? "pointer-events-none opacity-50" : ""}
                  />
                </PaginationItem>
              </PaginationContent>
            </Pagination>
          </div>
        )}
      </CardContent>

      {/* View Device Dialog */}
      <Dialog open={isViewDialogOpen} onOpenChange={setIsViewDialogOpen}>
        <DialogContent className="sm:max-w-[725px]">
          <DialogHeader>
            <DialogTitle>Chi tiết thiết bị</DialogTitle>
            <DialogDescription>Thông tin chi tiết của thiết bị này.</DialogDescription>
          </DialogHeader>
          {selectedDevice && <DeviceDetails device={selectedDevice} onStatusChange={handleStatusChange} />}
        </DialogContent>
      </Dialog>

      {/* Edit Device Dialog */}
      <Dialog open={isEditDialogOpen} onOpenChange={setIsEditDialogOpen}>
        <DialogContent className="sm:max-w-[525px]">
          <DialogHeader>
            <DialogTitle>Chỉnh sửa thiết bị</DialogTitle>
            <DialogDescription>Cập nhật thông tin của thiết bị.</DialogDescription>
          </DialogHeader>
          {selectedDevice && (
            <DeviceForm
              initialData={selectedDevice}
              onSubmit={handleEdit}
              onCancel={() => setIsEditDialogOpen(false)}
              isSubmitting={isSubmitting}
            />
          )}
        </DialogContent>
      </Dialog>

      {/* Delete Confirmation Dialog */}
      <Dialog open={isDeleteDialogOpen} onOpenChange={setIsDeleteDialogOpen}>
        <DialogContent className="sm:max-w-[425px]">
          <DialogHeader>
            <DialogTitle>Xác nhận xóa</DialogTitle>
            <DialogDescription>
              Bạn chắc chắn muốn xóa thiết bị này? Hành động này sẽ không được hoàn lại.
            </DialogDescription>
          </DialogHeader>
          <DialogFooter>
            <Button variant="outline" onClick={() => setIsDeleteDialogOpen(false)}>
              Hủy
            </Button>
            <Button variant="destructive" onClick={() => selectedDevice && handleDelete(selectedDevice.id)}>
              Xóa
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </Card>
  )
}

