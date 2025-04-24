"use client"

import type React from "react"

import { useEffect, useState } from "react"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog"
import { Edit, Eye, Trash2, Search, X } from "lucide-react"
import { LocationForm } from "./location-form"
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
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Location, LocationApi, type ApiError } from "@/components/api-service"
import { useToast } from "@/hooks/use-toast"
import { Loader2 } from "lucide-react"
import { LocationDetails } from "./location-details"

export function LocationsList() {
  const { toast } = useToast()
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [locations, setLocations] = useState<Location[]>([])
  const [selectedLocation, setSelectedLocation] = useState<any>(null)
  const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false)
  const [isViewDialogOpen, setIsViewDialogOpen] = useState(false)
  const [isEditDialogOpen, setIsEditDialogOpen] = useState(false)
  const [searchTerm, setSearchTerm] = useState("")
  const [typeFilter, setTypeFilter] = useState("all")
  const [homeFilter, setHomeFilter] = useState("all")

  const [currentPage, setCurrentPage] = useState(1)
  const [itemsPerPage, setItemsPerPage] = useState(5)

  useEffect(() => {
    const fetchLocations = async () => {
      try {
        const response = await LocationApi.getAll()
        setLocations(response)
      } catch (error) {
        const apiError = error as ApiError
        toast({
            title: "Lỗi",
            description: apiError.message || "Không thể tải vị trí. Vui lòng thử lại.",
            variant: "destructive",
        })
      }
    }
    fetchLocations()
  }, [])

  const handleDelete = async (id: string) => {
    setIsSubmitting(true)
    try {
      await LocationApi.delete(id)
      setLocations(locations.filter((location) => location.id !== id))
      toast({
        title: "Thành công",
        description: "Vị trí đã được xóa thành công",
        variant: "success",
      })
    } catch (error) {
      const apiError = error as ApiError
      toast({
        title: "Lỗi",
        description: apiError.message || "Không thể xóa vị trí. Vui lòng thử lại.",
        variant: "destructive",
      })
    } finally {
      setIsSubmitting(false)
      setIsDeleteDialogOpen(false)
    }
  }

  const handleEdit = async (updatedLocation: any) => {
    setIsSubmitting(true)
    try {
      await LocationApi.update(updatedLocation.id, updatedLocation)
      setLocations(locations.map((location) => (location.id === updatedLocation.id ? updatedLocation : location)))
      toast({
        title: "Thành công",
        description: "Vị trí đã được cập nhật thành công",
        variant: "success",
      })
    } catch (error) {
      const apiError = error as ApiError
      toast({
        title: "Lỗi",
        description: apiError.message || "Không thể cập nhật vị trí. Vui lòng thử lại.",
        variant: "destructive",
      })
    } finally {
      setIsSubmitting(false)
      setIsEditDialogOpen(false)
    }
  }

  const uniqueHomes = Array.from(new Set(locations.map((location) => location.home.email))).map((homeEmail) => {
    const location = locations.find((r) => r.home.email === homeEmail)
    return { id: homeEmail, name: location?.home?.email || "" }
  })

  const filteredLocations = locations.filter((location) => {
    const matchesSearch =
      location.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      location.home.email.toLowerCase().includes(searchTerm.toLowerCase())

    const matchesHome = homeFilter === "all" || location.home.email === homeFilter

    return matchesSearch && matchesHome
  })

  const totalItems = filteredLocations.length
  const totalPages = Math.ceil(totalItems / itemsPerPage)
  const indexOfLastItem = currentPage * itemsPerPage
  const indexOfFirstItem = indexOfLastItem - itemsPerPage
  const currentItems = filteredLocations.slice(indexOfFirstItem, indexOfLastItem)

  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchTerm(e.target.value)
    setCurrentPage(1)
  }

  const paginate = (pageNumber: number) => {
    if (pageNumber > 0 && pageNumber <= totalPages) {
      setCurrentPage(pageNumber)
    }
  }

  const pageNumbers : number[] = []
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

  return (
    <Card>
      <CardHeader>
        <CardTitle>Tất cả vị trí</CardTitle>
        <CardDescription>Danh sách tất cả các vị trí trong hệ thống SmartHome.</CardDescription>
      </CardHeader>
      <div className="px-6 mb-4">
        <div className="flex flex-col md:flex-row gap-4">
          <div className="relative flex-1">
            <Input
              placeholder="Tìm kiếm vị trí theo tên hoặc email nhà..."
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
          <div className="w-full md:w-[180px]">
            <Select
              value={homeFilter}
              onValueChange={(value) => {
                setHomeFilter(value)
                setCurrentPage(1)
              }}
            >
              <SelectTrigger>
                <SelectValue placeholder="Lọc theo nhà" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">Tất cả nhà</SelectItem>
                {uniqueHomes.map((home) => (
                  <SelectItem key={home.id} value={home.id}>
                    {home.name}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>
        </div>
      </div>
      <CardContent>
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>ID</TableHead>
              <TableHead>Tên vị trí</TableHead>
              <TableHead>Email nhà</TableHead>
              <TableHead className="text-right">Thao tác</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {currentItems.length > 0 ? (
              currentItems.map((location) => (
                <TableRow key={location.id}>
                  <TableCell>{location.id}</TableCell>
                  <TableCell className="font-medium">{location.name}</TableCell>
                  <TableCell>{location.home.email}</TableCell>
                  <TableCell className="text-right">
                    <div className="flex justify-end gap-2">
                      <Button
                        variant="outline"
                        size="icon"
                        onClick={() => {
                          setSelectedLocation(location)
                          setIsViewDialogOpen(true)
                        }}
                      >
                        <Eye className="h-4 w-4 text-green-600" />
                      </Button>
                      <Button
                        variant="outline"
                        size="icon"
                        onClick={() => {
                          setSelectedLocation(location)
                          setIsEditDialogOpen(true)
                        }}
                      >
                        <Edit className="h-4 w-4 text-blue-600" />
                      </Button>
                      <Button
                        variant="outline"
                        size="icon"
                        onClick={() => {
                          setSelectedLocation(location)
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
                <TableCell colSpan={6} className="text-center py-4 text-muted-foreground">
                  {searchTerm || typeFilter !== "all" || homeFilter !== "all"
                    ? "Không tìm thấy vị trí phù hợp với tìm kiếm của bạn"
                    : "Không có vị trí nào"}
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>

        {/* Pagination Controls */}
        {totalItems > 0 && (
          <div className="mt-6 flex items-center justify-between">
            <div className="flex items-center gap-2">
              <span className="text-sm text-muted-foreground">Số mục mỗi trang:</span>
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

      {/* View Location Dialog */}
      <Dialog open={isViewDialogOpen} onOpenChange={setIsViewDialogOpen}>
        <DialogContent className="sm:max-w-[725px]">
          <DialogHeader>
            <DialogTitle>Thông tin vị trí</DialogTitle>
            <DialogDescription>Thông tin chi tiết về vị trí này.</DialogDescription>
          </DialogHeader>
          {selectedLocation && <LocationDetails location={selectedLocation} />}
        </DialogContent>
      </Dialog>

      {/* Edit Location Dialog */}
      <Dialog open={isEditDialogOpen} onOpenChange={setIsEditDialogOpen}>
        <DialogContent className="sm:max-w-[525px]">
          <DialogHeader>
            <DialogTitle>Chỉnh sửa vị trí</DialogTitle>
            <DialogDescription>Thay đổi thông tin vị trí.</DialogDescription>
          </DialogHeader>
          {selectedLocation && (
            <LocationForm
              initialData={selectedLocation}
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
              Bạn có chắc chắn muốn xóa vị trí này? Hành động này không thể hoàn tác.
            </DialogDescription>
          </DialogHeader>
          <DialogFooter>
            <Button variant="outline" onClick={() => setIsDeleteDialogOpen(false)} disabled={isSubmitting}>
              Hủy
            </Button>
            <Button
              variant="destructive"
              onClick={() => selectedLocation && handleDelete(selectedLocation.id)}
              disabled={isSubmitting}
            >
              {isSubmitting ? (
                <>
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                  Đang xóa...
                </>
              ) : (
                "Xóa"
              )}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </Card>
  )
}
