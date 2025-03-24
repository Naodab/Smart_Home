"use client"

import { useState, useEffect } from "react"
import { Card, CardContent } from "@/components/ui/card"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Cpu, History, Power } from "lucide-react"
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
import { DeviceApi, type ApiError } from "@/components/api-service"
import { useToast } from "@/hooks/use-toast"
import { Loader2 } from "lucide-react"

// Mock data for device history
const mockDeviceHistory = {
  "1": [
    { id: "1", personId: "1", personName: "John Doe", newStatus: "Online", timestamp: "2023-05-15 14:30:22" },
    { id: "2", personId: "2", personName: "Jane Smith", newStatus: "Offline", timestamp: "2023-05-14 08:15:10" },
    { id: "3", personId: "2", personName: "Jane Smith", newStatus: "Online", timestamp: "2023-05-14 18:45:33" },
  ],
  "2": [
    { id: "4", personId: "1", personName: "John Doe", newStatus: "Offline", timestamp: "2023-05-13 12:10:05" },
    { id: "5", personId: "1", personName: "John Doe", newStatus: "Online", timestamp: "2023-05-13 17:22:18" },
  ],
  "3": [{ id: "6", personId: "2", personName: "Jane Smith", newStatus: "Offline", timestamp: "2023-05-16 22:10:05" }],
  "4": [{ id: "7", personId: "3", personName: "Bob Johnson", newStatus: "Online", timestamp: "2023-05-12 09:20:15" }],
  "5": [],
}

interface DeviceDetailsProps {
  device: {
    id: string
    name: string
    status: string
    homeId: string
    homeName: string
  }
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
    // In a real app, you would fetch this data from your API
    setDeviceHistory(mockDeviceHistory[device.id as keyof typeof mockDeviceHistory] || [])
    setNewStatus(device.status)
  }, [device.id, device.status])

  const handleStatusChange = async () => {
    if (!selectedPerson) return

    setIsSubmitting(true)
    try {
      await DeviceApi.updateStatus(device.id, newStatus, selectedPerson)

      // Call the parent component's onStatusChange
      onStatusChange(device.id, newStatus)

      // Add to history
      const newHistoryEntry = {
        id: `new-${Date.now()}`,
        personId: selectedPerson,
        personName: persons.find((p) => p.id === selectedPerson)?.name || "Unknown",
        newStatus,
        timestamp: new Date().toISOString().replace("T", " ").substring(0, 19),
      }

      setDeviceHistory([newHistoryEntry, ...deviceHistory])

      toast({
        title: "Success",
        description: `Device status changed to ${newStatus}`,
        variant: "success",
      })
    } catch (error) {
      const apiError = error as ApiError
      toast({
        title: "Error",
        description: apiError.message || "Failed to update device status. Please try again.",
        variant: "destructive",
      })
    } finally {
      setIsSubmitting(false)
      setIsChangeStatusDialogOpen(false)
    }
  }

  return (
    <Card>
      <CardContent className="pt-6">
        <div className="flex items-center gap-4 mb-6">
          <div className="p-2 bg-green-100 rounded-full">
            <Cpu className="h-6 w-6 text-green-600" />
          </div>
          <h3 className="text-xl font-semibold">{device.name}</h3>
        </div>

        <div className="grid grid-cols-3 gap-4 mb-6">
          <div className="space-y-1">
            <p className="text-sm text-muted-foreground">ID</p>
            <p className="font-medium">{device.id}</p>
          </div>
          <div className="space-y-1">
            <p className="text-sm text-muted-foreground">Status</p>
            <Badge
              variant={device.status === "Online" ? "default" : "secondary"}
              className={device.status === "Online" ? "bg-green-500" : "bg-gray-500"}
            >
              {device.status}
            </Badge>
          </div>
          <div className="space-y-1">
            <p className="text-sm text-muted-foreground">Home Email</p>
            <p className="font-medium">{device.homeName}</p>
          </div>
        </div>

        <div className="flex justify-end mb-6">
          <Button className="bg-green-600 hover:bg-green-700" onClick={() => setIsChangeStatusDialogOpen(true)}>
            <Power className="mr-2 h-4 w-4" />
            Change Status
          </Button>
        </div>

        <Tabs defaultValue="history" className="mt-6">
          <TabsList className="grid w-full grid-cols-1">
            <TabsTrigger value="history" className="flex items-center gap-2">
              <History className="h-4 w-4" />
              Status Change History
            </TabsTrigger>
          </TabsList>

          <TabsContent value="history" className="mt-4">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Person</TableHead>
                  <TableHead>New Status</TableHead>
                  <TableHead>Timestamp</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {deviceHistory.length > 0 ? (
                  deviceHistory.map((history) => (
                    <TableRow key={history.id}>
                      <TableCell>{history.personName}</TableCell>
                      <TableCell>
                        <Badge
                          variant={history.newStatus === "Online" ? "default" : "secondary"}
                          className={history.newStatus === "Online" ? "bg-green-500" : "bg-gray-500"}
                        >
                          {history.newStatus}
                        </Badge>
                      </TableCell>
                      <TableCell>{history.timestamp}</TableCell>
                    </TableRow>
                  ))
                ) : (
                  <TableRow>
                    <TableCell colSpan={3} className="text-center py-4 text-muted-foreground">
                      No status change history for this device
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
              <DialogTitle>Change Device Status</DialogTitle>
              <DialogDescription>Update the status of this device.</DialogDescription>
            </DialogHeader>
            <div className="grid gap-4 py-4">
              <div className="space-y-2">
                <Label htmlFor="status">New Status</Label>
                <Select value={newStatus} onValueChange={setNewStatus}>
                  <SelectTrigger>
                    <SelectValue placeholder="Select status" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="Online">Online</SelectItem>
                    <SelectItem value="Offline">Offline</SelectItem>
                  </SelectContent>
                </Select>
              </div>
              <div className="space-y-2">
                <Label htmlFor="person">Person Making Change</Label>
                <Select value={selectedPerson} onValueChange={setSelectedPerson}>
                  <SelectTrigger>
                    <SelectValue placeholder="Select person" />
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
              <Button variant="outline" onClick={() => setIsChangeStatusDialogOpen(false)} disabled={isSubmitting}>
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
                    Updating...
                  </>
                ) : (
                  "Update Status"
                )}
              </Button>
            </DialogFooter>
          </DialogContent>
        </Dialog>
      </CardContent>
    </Card>
  )
}

