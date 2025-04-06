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
        <CardTitle>Device Status Change History</CardTitle>
        <CardDescription>A record of all device status changes in your SmartHome system.</CardDescription>
      </CardHeader>
      <CardContent>
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
          <div className="space-y-2">
            <Label htmlFor="deviceName">Device Name</Label>
            <Input
              id="deviceName"
              placeholder="Filter by device name"
              value={filters.deviceName}
              onChange={(e) => handleFilterChange("deviceName", e.target.value)}
            />
          </div>
          <div className="space-y-2">
            <Label htmlFor="personName">Person Name</Label>
            <Input
              id="personName"
              placeholder="Filter by person name"
              value={filters.personName}
              onChange={(e) => handleFilterChange("personName", e.target.value)}
            />
          </div>
          <div className="space-y-2">
            <Label htmlFor="homeName">Home Email</Label>
            <Input
              id="homeName"
              placeholder="Filter by home email"
              value={filters.homeName}
              onChange={(e) => handleFilterChange("homeName", e.target.value)}
            />
          </div>
          <div className="space-y-2">
            <Label htmlFor="status">Status</Label>
            <Select value={filters.status} onValueChange={(value) => handleFilterChange("status", value)}>
              <SelectTrigger>
                <SelectValue placeholder="All statuses" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">All statuses</SelectItem>
                <SelectItem value="Online">Online</SelectItem>
                <SelectItem value="Offline">Offline</SelectItem>
              </SelectContent>
            </Select>
          </div>
        </div>

        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Device</TableHead>
              <TableHead>Person</TableHead>
              <TableHead>Home Email</TableHead>
              <TableHead>New Status</TableHead>
              <TableHead>Timestamp</TableHead>
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
                  No history records found matching your filters
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </CardContent>
    </Card>
  )
}

