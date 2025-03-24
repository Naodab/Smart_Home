"use client"

import type React from "react"

import { useState } from "react"
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
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"

// Mock data for devices - let's add more items to demonstrate pagination
const initialDevices = [
  {
    id: "1",
    name: "Living Room Thermostat",
    status: "Online",
    homeId: "1",
    homeName: "main.residence@example.com",
  },
  {
    id: "2",
    name: "Kitchen Smart Light",
    status: "Online",
    homeId: "1",
    homeName: "main.residence@example.com",
  },
  {
    id: "3",
    name: "Front Door Lock",
    status: "Offline",
    homeId: "1",
    homeName: "main.residence@example.com",
  },
  {
    id: "4",
    name: "Beach House Camera",
    status: "Online",
    homeId: "2",
    homeName: "beach.house@example.com",
  },
  {
    id: "5",
    name: "Cabin Heater",
    status: "Online",
    homeId: "3",
    homeName: "mountain.cabin@example.com",
  },
  {
    id: "6",
    name: "Bedroom Smart Light",
    status: "Online",
    homeId: "1",
    homeName: "main.residence@example.com",
  },
  {
    id: "7",
    name: "Garage Door Opener",
    status: "Offline",
    homeId: "1",
    homeName: "main.residence@example.com",
  },
  {
    id: "8",
    name: "Backyard Motion Sensor",
    status: "Online",
    homeId: "1",
    homeName: "main.residence@example.com",
  },
  {
    id: "9",
    name: "Pool Temperature Monitor",
    status: "Online",
    homeId: "2",
    homeName: "beach.house@example.com",
  },
  {
    id: "10",
    name: "Patio Smart Light",
    status: "Offline",
    homeId: "2",
    homeName: "beach.house@example.com",
  },
  {
    id: "11",
    name: "Fireplace Controller",
    status: "Online",
    homeId: "3",
    homeName: "mountain.cabin@example.com",
  },
  {
    id: "12",
    name: "Water Leak Detector",
    status: "Online",
    homeId: "3",
    homeName: "mountain.cabin@example.com",
  },
]

export function DevicesList() {
  const [devices, setDevices] = useState(initialDevices)
  const [selectedDevice, setSelectedDevice] = useState<any>(null)
  const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false)
  const [isViewDialogOpen, setIsViewDialogOpen] = useState(false)
  const [isEditDialogOpen, setIsEditDialogOpen] = useState(false)
  const [searchTerm, setSearchTerm] = useState("")

  // Pagination state
  const [currentPage, setCurrentPage] = useState(1)
  const [itemsPerPage, setItemsPerPage] = useState(5)

  const handleDelete = (id: string) => {
    setDevices(devices.filter((device) => device.id !== id))
    setIsDeleteDialogOpen(false)
  }

  const handleEdit = (updatedDevice: any) => {
    setDevices(devices.map((device) => (device.id === updatedDevice.id ? updatedDevice : device)))
    setIsEditDialogOpen(false)
  }

  const handleStatusChange = (deviceId: string, newStatus: string) => {
    setDevices(devices.map((device) => (device.id === deviceId ? { ...device, status: newStatus } : device)))
  }

  // Filter devices based on search term
  const filteredDevices = devices.filter(
    (device) =>
      device.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      device.homeName.toLowerCase().includes(searchTerm.toLowerCase()),
  )

  // Calculate pagination
  const totalItems = filteredDevices.length
  const totalPages = Math.ceil(totalItems / itemsPerPage)
  const indexOfLastItem = currentPage * itemsPerPage
  const indexOfFirstItem = indexOfLastItem - itemsPerPage
  const currentItems = filteredDevices.slice(indexOfFirstItem, indexOfLastItem)

  // Reset to first page when search term changes
  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchTerm(e.target.value)
    setCurrentPage(1)
  }

  // Handle page change
  const paginate = (pageNumber: number) => {
    if (pageNumber > 0 && pageNumber <= totalPages) {
      setCurrentPage(pageNumber)
    }
  }

  // Generate page numbers for pagination
  const pageNumbers = []
  for (let i = 1; i <= totalPages; i++) {
    pageNumbers.push(i)
  }

  // Determine which page numbers to show
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
        <CardTitle>All Devices</CardTitle>
        <CardDescription>A list of all devices in your SmartHome system.</CardDescription>
      </CardHeader>
      <div className="px-6 mb-4">
        <div className="relative">
          <Input
            placeholder="Search devices by name or home email..."
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
              <span className="sr-only">Clear search</span>
            </Button>
          )}
        </div>
      </div>
      <CardContent>
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>ID</TableHead>
              <TableHead>Name</TableHead>
              <TableHead>Status</TableHead>
              <TableHead>Home Email</TableHead>
              <TableHead className="text-right">Actions</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {currentItems.length > 0 ? (
              currentItems.map((device) => (
                <TableRow key={device.id}>
                  <TableCell>{device.id}</TableCell>
                  <TableCell className="font-medium">{device.name}</TableCell>
                  <TableCell>
                    <Badge
                      variant={device.status === "Online" ? "default" : "secondary"}
                      className={device.status === "Online" ? "bg-green-500" : "bg-gray-500"}
                    >
                      {device.status}
                    </Badge>
                  </TableCell>
                  <TableCell>{device.homeName}</TableCell>
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
                  {searchTerm ? "No devices found matching your search" : "No devices available"}
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>

        {/* Pagination Controls */}
        {totalItems > 0 && (
          <div className="mt-6 flex items-center justify-between">
            <div className="flex items-center gap-2">
              <span className="text-sm text-muted-foreground">Items per page:</span>
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
                Showing {indexOfFirstItem + 1}-{Math.min(indexOfLastItem, totalItems)} of {totalItems}
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
            <DialogTitle>Device Details</DialogTitle>
            <DialogDescription>Detailed information about this device.</DialogDescription>
          </DialogHeader>
          {selectedDevice && <DeviceDetails device={selectedDevice} onStatusChange={handleStatusChange} />}
        </DialogContent>
      </Dialog>

      {/* Edit Device Dialog */}
      <Dialog open={isEditDialogOpen} onOpenChange={setIsEditDialogOpen}>
        <DialogContent className="sm:max-w-[525px]">
          <DialogHeader>
            <DialogTitle>Edit Device</DialogTitle>
            <DialogDescription>Make changes to the device details.</DialogDescription>
          </DialogHeader>
          {selectedDevice && (
            <DeviceForm
              initialData={selectedDevice}
              onSubmit={handleEdit}
              onCancel={() => setIsEditDialogOpen(false)}
            />
          )}
        </DialogContent>
      </Dialog>

      {/* Delete Confirmation Dialog */}
      <Dialog open={isDeleteDialogOpen} onOpenChange={setIsDeleteDialogOpen}>
        <DialogContent className="sm:max-w-[425px]">
          <DialogHeader>
            <DialogTitle>Confirm Deletion</DialogTitle>
            <DialogDescription>
              Are you sure you want to delete this device? This action cannot be undone.
            </DialogDescription>
          </DialogHeader>
          <DialogFooter>
            <Button variant="outline" onClick={() => setIsDeleteDialogOpen(false)}>
              Cancel
            </Button>
            <Button variant="destructive" onClick={() => selectedDevice && handleDelete(selectedDevice.id)}>
              Delete
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </Card>
  )
}

