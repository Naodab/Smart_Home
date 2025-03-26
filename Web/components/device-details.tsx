"use client"

import { useState, useEffect } from "react"
import { Card, CardContent } from "@/components/ui/card"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Cpu, History } from "lucide-react"
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
  onStatusChange: (deviceId: string, newStatus: boolean) => void
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
      await DeviceApi.updateStatus(device.id, newStatus ? "Activate" : "Inactivate", selectedPerson)

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
              variant={device.status ? "default" : "secondary"}
              className={device.status ? "bg-green-500" : "bg-gray-500"}
            >
              {device.status ? "Activate" : "Inactivate"}
            </Badge>
          </div>
          <div className="space-y-1">
            <p className="text-sm text-muted-foreground">Home Email</p>
            <p className="font-medium">{device.home.email}</p>
          </div>
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
                          variant={history.newStatus ? "default" : "secondary"}
                          className={history.newStatus ? "bg-green-500" : "bg-gray-500"}
                        >
                          {history.newStatus ? "Activate" : "Inactivate"}
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
                <Select value={newStatus ? "Activate" : "Inactivate"} onValueChange={(value) => setNewStatus(value === "Activate")}>
                  <SelectTrigger>
                    <SelectValue placeholder="Select status" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="Activate">Activate</SelectItem>
                    <SelectItem value="Inactivate">Inactivate</SelectItem>
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
            </DialogFooter>
          </DialogContent>
        </Dialog>
      </CardContent>
    </Card>
  )
}

