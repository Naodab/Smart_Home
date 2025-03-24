"use client"

import { useState, useEffect } from "react"
import { Card, CardContent } from "@/components/ui/card"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { User, Home } from "lucide-react"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"

// Mock data for homes associated with person
const mockHomesForPerson = {
  "1": [{ id: "1", email: "main.residence@example.com", address: "123 Main St, Anytown, USA" }],
  "2": [{ id: "1", email: "main.residence@example.com", address: "123 Main St, Anytown, USA" }],
  "3": [{ id: "2", email: "beach.house@example.com", address: "456 Ocean Ave, Beachtown, USA" }],
  "4": [{ id: "3", email: "mountain.cabin@example.com", address: "789 Mountain Rd, Highlands, USA" }],
  "5": [],
}

// Mock data for device history
const mockDeviceHistory = {
  "1": [
    {
      id: "1",
      deviceId: "1",
      deviceName: "Living Room Thermostat",
      newStatus: "Online",
      timestamp: "2023-05-15 14:30:22",
    },
    {
      id: "2",
      deviceId: "2",
      deviceName: "Kitchen Smart Light",
      newStatus: "Offline",
      timestamp: "2023-05-14 08:15:10",
    },
    {
      id: "3",
      deviceId: "2",
      deviceName: "Kitchen Smart Light",
      newStatus: "Online",
      timestamp: "2023-05-14 18:45:33",
    },
  ],
  "2": [
    { id: "4", deviceId: "3", deviceName: "Front Door Lock", newStatus: "Offline", timestamp: "2023-05-16 22:10:05" },
  ],
  "3": [
    { id: "5", deviceId: "4", deviceName: "Beach House Camera", newStatus: "Online", timestamp: "2023-05-12 09:20:15" },
  ],
  "4": [],
  "5": [],
}

interface PersonDetailsProps {
  person: {
    id: string
    name: string
  }
}

export function PersonDetails({ person }: PersonDetailsProps) {
  const [homesForPerson, setHomesForPerson] = useState<any[]>([])
  const [deviceHistory, setDeviceHistory] = useState<any[]>([])

  useEffect(() => {
    // In a real app, you would fetch this data from your API
    setHomesForPerson(mockHomesForPerson[person.id as keyof typeof mockHomesForPerson] || [])
    setDeviceHistory(mockDeviceHistory[person.id as keyof typeof mockDeviceHistory] || [])
  }, [person.id])

  return (
    <Card>
      <CardContent className="pt-6">
        <div className="flex items-center gap-4 mb-6">
          <div className="p-2 bg-green-100 rounded-full">
            <User className="h-6 w-6 text-green-600" />
          </div>
          <h3 className="text-xl font-semibold">{person.name}</h3>
        </div>

        <div className="mb-6">
          <div className="space-y-1">
            <p className="text-sm text-muted-foreground">ID</p>
            <p className="font-medium">{person.id}</p>
          </div>
        </div>

        <Tabs defaultValue="homes" className="mt-6">
          <TabsList className="grid w-full grid-cols-2">
            <TabsTrigger value="homes" className="flex items-center gap-2">
              <Home className="h-4 w-4" />
              Homes
            </TabsTrigger>
            <TabsTrigger value="history" className="flex items-center gap-2">
              <User className="h-4 w-4" />
              Device History
            </TabsTrigger>
          </TabsList>

          <TabsContent value="homes" className="mt-4">
            <div className="flex justify-between items-center mb-4">
              <h4 className="text-lg font-semibold">Homes Associated with this Person</h4>
            </div>
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>ID</TableHead>
                  <TableHead>Email</TableHead>
                  <TableHead>Address</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {homesForPerson.length > 0 ? (
                  homesForPerson.map((home) => (
                    <TableRow key={home.id}>
                      <TableCell>{home.id}</TableCell>
                      <TableCell>{home.email}</TableCell>
                      <TableCell>{home.address}</TableCell>
                    </TableRow>
                  ))
                ) : (
                  <TableRow>
                    <TableCell colSpan={3} className="text-center py-4 text-muted-foreground">
                      No homes associated with this person
                    </TableCell>
                  </TableRow>
                )}
              </TableBody>
            </Table>
          </TabsContent>

          <TabsContent value="history" className="mt-4">
            <div className="flex justify-between items-center mb-4">
              <h4 className="text-lg font-semibold">Device Status Change History</h4>
            </div>
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Device</TableHead>
                  <TableHead>New Status</TableHead>
                  <TableHead>Timestamp</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {deviceHistory.length > 0 ? (
                  deviceHistory.map((history) => (
                    <TableRow key={history.id}>
                      <TableCell>{history.deviceName}</TableCell>
                      <TableCell>{history.newStatus}</TableCell>
                      <TableCell>{history.timestamp}</TableCell>
                    </TableRow>
                  ))
                ) : (
                  <TableRow>
                    <TableCell colSpan={3} className="text-center py-4 text-muted-foreground">
                      No device history for this person
                    </TableCell>
                  </TableRow>
                )}
              </TableBody>
            </Table>
          </TabsContent>
        </Tabs>
      </CardContent>
    </Card>
  )
}

