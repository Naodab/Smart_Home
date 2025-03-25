"use client"

import { useState, useEffect } from "react"
import { Card, CardContent } from "@/components/ui/card"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { User, Home } from "lucide-react"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { HomeInPerson, Person, HistoryInPerson } from "./api-service"

interface PersonDetailsProps {
  person: Person
}

export function PersonDetails({ person }: PersonDetailsProps) {
  const [homesForPerson, setHomesForPerson] = useState<HomeInPerson[]>([])
  const [deviceHistory, setDeviceHistory] = useState<HistoryInPerson[]>([])

  useEffect(() => {
    setHomesForPerson(person.homes)
    setDeviceHistory(person.histories)
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

