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
import { Edit, Eye, Trash2, Search, X, Loader2 } from "lucide-react"
import { PersonForm } from "./person-form"
import { PersonDetails } from "./person-details"
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
import { ApiError, Home, HomeApi, HomeToSelect, PersonApi, type Person } from "./api-service"  
import { useToast } from "@/hooks/use-toast"



export function PersonsList() {
  const { toast } = useToast()
  const [persons, setPersons] = useState<Person[]>([])
  const [homes, setHomes] = useState<HomeToSelect[]>([])
  const [isLoading, setIsLoading] = useState(false)
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [selectedPerson, setSelectedPerson] = useState<Person | null>(null)
  const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false)
  const [isViewDialogOpen, setIsViewDialogOpen] = useState(false)
  const [isEditDialogOpen, setIsEditDialogOpen] = useState(false)
  const [searchTerm, setSearchTerm] = useState("")

  const [currentPage, setCurrentPage] = useState(1)
  const [itemsPerPage, setItemsPerPage] = useState(5)

  useEffect(() => {
    (async function fetchPersons() {
      try {
        setIsLoading(true)
        const data = await PersonApi.getAll();
        setPersons(data);
        setIsLoading(false)
      } catch (error) {
        console.error("Failed to fetch persons", error);
        setIsLoading(false)
      }
    })()
  }, []);

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

  const handleDelete = async (id: string) => {
    setIsSubmitting(true)
    try {
      await PersonApi.delete(id)
      setPersons(persons.filter((person) => person.id !== id))
      toast({
        title: "Thành công",
        description: "Người dùng được xóa thành công",
        variant: "success",
      })
    } catch (error) {
      const apiError = error as ApiError
      toast({
        title: "Lỗi",
        description: apiError.message || "Không thể xóa người dùng. Vui lòng thử lại.",
        variant: "destructive",
      })
    } finally {
      setIsSubmitting(false)
      setIsDeleteDialogOpen(false)
    }
  }

  const handleEdit = async (updatedPerson: Person) => {
    setIsSubmitting(true)
    try {
      const selectedHome = homes.find((home) => home.id === updatedPerson.home.id)
      updatedPerson.home.email = selectedHome ? selectedHome.email : ""
      await PersonApi.update(updatedPerson.id, updatedPerson)
      setPersons(persons.map((person) => (person.id === updatedPerson.id ? updatedPerson : person)))
      toast({
        title: "Thành công",
        description: "Người dùng được cập nhật thành công",
        variant: "success",
      })
    } catch (error) {
      const apiError = error as ApiError
      toast({
        title: "Lỗi",
        description: apiError.message || "Không thể cập nhật người dùng. Vui lòng thử lại.",
        variant: "destructive",
      })
    } finally {
      setIsSubmitting(false)
      setIsEditDialogOpen(false)
    }
  }

  const filteredPersons = persons.filter(
    (person) =>
      person.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      person.home.email.toLowerCase().includes(searchTerm.toLowerCase())
  )

  const totalItems = filteredPersons.length
  const totalPages = Math.ceil(totalItems / itemsPerPage)
  const indexOfLastItem = currentPage * itemsPerPage
  const indexOfFirstItem = indexOfLastItem - itemsPerPage
  const currentItems = filteredPersons.slice(indexOfFirstItem, indexOfLastItem)

  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchTerm(e.target.value)
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

  return (
    <Card>
      <CardHeader>
        <CardTitle>Tất cả người dùng</CardTitle>
        <CardDescription>Danh sách của tất cả người dùng trong hệ thống SmartHome.</CardDescription>
      </CardHeader>
      <div className="px-6 mb-4">
        <div className="relative">
          <Input
            placeholder="Tìm kiếm người dùng theo tên hoặc email nhà..."
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
      </div>
      <CardContent>
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Mã</TableHead>
              <TableHead>Tên</TableHead>
              <TableHead>Email nhà</TableHead>
              <TableHead className="text-right">Hành động</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {currentItems.length > 0 ? (
              currentItems.map((person) => (
                <TableRow key={person.id}>
                  <TableCell>{person.id}</TableCell>
                  <TableCell className="font-medium">{person.name}</TableCell>
                  <TableCell>{person.home.email}</TableCell>
                  <TableCell className="text-right">
                    <div className="flex justify-end gap-2">
                      <Button
                        variant="outline"
                        size="icon"
                        onClick={() => {
                          setSelectedPerson(person)
                          setIsViewDialogOpen(true)
                        }}
                      >
                        <Eye className="h-4 w-4 text-green-600" />
                      </Button>
                      <Button
                        variant="outline"
                        size="icon"
                        onClick={() => {
                          setSelectedPerson(person)
                          setIsEditDialogOpen(true)
                        }}
                      >
                        <Edit className="h-4 w-4 text-blue-600" />
                      </Button>
                      <Button
                        variant="outline"
                        size="icon"
                        onClick={() => {
                          setSelectedPerson(person)
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
                <TableCell colSpan={4} className="text-center py-4 t  ext-muted-foreground">
                  {searchTerm ? "Không tìm thấy người dùng" : "Không có người dùng nào"}
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>

        {/* Pagination Controls */}
        {totalItems > 0 && (
          <div className="mt-6 flex items-center justify-between">
            <div className="flex items-center gap-2">
              <span className="text-sm text-muted-foreground">Người dùng mỗi trang:</span>
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

      {/* View Person Dialog */}
      <Dialog open={isViewDialogOpen} onOpenChange={setIsViewDialogOpen}>
        <DialogContent className="sm:max-w-[725px]">
          <DialogHeader>
            <DialogTitle>Thông tin người dùng</DialogTitle>
            <DialogDescription>Thông tin chi tiết về người dùng này.</DialogDescription>
          </DialogHeader>
          {selectedPerson && <PersonDetails person={selectedPerson} />}
        </DialogContent>
      </Dialog>

      {/* Edit Person Dialog */}
      <Dialog open={isEditDialogOpen} onOpenChange={setIsEditDialogOpen}>
        <DialogContent className="sm:max-w-[525px]">
          <DialogHeader>
            <DialogTitle>Chỉnh sửa người dùng</DialogTitle>
            <DialogDescription>Thay đổi thông tin người dùng.</DialogDescription>
          </DialogHeader>
          {selectedPerson && (
            <PersonForm
              initialData={selectedPerson}
              onSubmit={handleEdit}
              onCancel={() => setIsEditDialogOpen(false)}
              isSubmitting={isSubmitting}
              homes={homes}
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
              Bạn có chắc muốn xóa người dùng này? Hành động này không thể hoàn lại.
            </DialogDescription>
          </DialogHeader>
          <DialogFooter>
            <Button variant="outline" onClick={() => setIsDeleteDialogOpen(false)} disabled={isSubmitting}>
              Hủy
            </Button>
            <Button
              variant="destructive"
              onClick={() => selectedPerson && handleDelete(selectedPerson.id)}
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

