"use client"

import { Card, CardContent } from "@/components/ui/card"
import { User, Home, History, Table, Badge } from "lucide-react"
import { HistoryInPerson, Person } from "./api-service"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@radix-ui/react-tabs"
import { TableBody, TableCell, TableHead, TableHeader, TableRow } from "./ui/table"
import { useEffect, useState } from "react"

interface PersonDetailsProps {
  person: Person
}

export function PersonDetails({ person }: PersonDetailsProps) {
  const [histories, setHistories] = useState<HistoryInPerson[]>([])

  useEffect(() => {
    setHistories(person.histories)
  }, [])

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
          <div className="flex items-center gap-2">
            <Home className="h-5 w-5 text-green-600" />
            <div>
              <p className="text-sm text-muted-foreground">Home</p>
              <p className="font-medium">{person.home.email}</p>
            </div>
          </div>
        </div>

        <Tabs defaultValue="history" className="mt-6">
          <TabsList className="grid w-full grid-cols-1">
            <TabsTrigger value="history" className="flex items-center gap-2">
              <History className="h-4 w-4" />
              Device History
            </TabsTrigger>
          </TabsList>
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
                {histories.length > 0 ? (
                  histories.map((history) => (
                    <TableRow key={history.id}>
                      <TableCell>{history.deviceName}</TableCell>
                      <TableCell>
                        <Badge
                          fontVariant={history.newStatus ? "default" : "secondary"}
                          className={history.newStatus ? "bg-green-500" : "bg-gray-500"}
                        >
                          {history.newStatus ? "Active" : "Inactive"}
                        </Badge>
                      </TableCell>
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

