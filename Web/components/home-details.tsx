"use client"

import { useState, useEffect } from "react"
import { Card, CardContent } from "@/components/ui/card"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Home, Users, Cpu } from "lucide-react"
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
import { PersonHomeForm } from "./person-home-form"
import { Device, Person } from "./api-service"

// Mock data for all persons (for adding to home)
const allPersons = [
  { id: "1", name: "John Doe" },
  { id: "2", name: "Jane Smith" },
  { id: "3", name: "Bob Johnson" },
  { id: "4", name: "Alice Williams" },
  { id: "5", name: "Charlie Brown" },
]

interface HomeDetailsProps {
  home: {
    id: string
    email: string
    address: string
    persons: Person[]
    devices: Device[]
  }
}

export function HomeDetails({ home }: HomeDetailsProps) {
  const [personsInHome, setPersonsInHome] = useState<any[]>([])
  const [devicesInHome, setDevicesInHome] = useState<any[]>([])
  const [isAddPersonDialogOpen, setIsAddPersonDialogOpen] = useState(false)
  const [selectedPersons, setSelectedPersons] = useState<string[]>([])

  useEffect(() => {
    setPersonsInHome(home.persons || [])
    setDevicesInHome(home.devices || [])
  }, [home.id])

  const handleAddPersons = () => {
    const newPersons = allPersons.filter((person) => selectedPersons.includes(person.id))
    setPersonsInHome([...personsInHome, ...newPersons])
    setIsAddPersonDialogOpen(false)
    setSelectedPersons([])
  }

  const handleRemovePerson = (personId: string) => {
    setPersonsInHome(personsInHome.filter((person) => person.id !== personId))
  }

  return (
    <Card>
      <CardContent className="pt-6">
        <div className="flex items-center gap-4 mb-6">
          <div className="p-2 bg-green-100 rounded-full">
            <Home className="h-6 w-6 text-green-600" />
          </div>
          <h3 className="text-xl font-semibold">{home.email}</h3>
        </div>

        <div className="grid grid-cols-2 gap-4 mb-6">
          <div className="space-y-1">
            <p className="text-sm text-muted-foreground">ID</p>
            <p className="font-medium">{home.id}</p>
          </div>
          <div className="space-y-1">
            <p className="text-sm text-muted-foreground">Address</p>
            <p className="font-medium">{home.address}</p>
          </div>
        </div>

        <Tabs defaultValue="persons" className="mt-6">
          <TabsList className="grid w-full grid-cols-2">
            <TabsTrigger value="persons" className="flex items-center gap-2">
              <Users className="h-4 w-4" />
              Persons
            </TabsTrigger>
            <TabsTrigger value="devices" className="flex items-center gap-2">
              <Cpu className="h-4 w-4" />
              Devices
            </TabsTrigger>
          </TabsList>

          <TabsContent value="persons" className="mt-4">
            <div className="flex justify-between items-center mb-4">
              <h4 className="text-lg font-semibold">Persons in this Home</h4>
              <Button
                size="sm"
                className="bg-green-600 hover:bg-green-700"
                onClick={() => setIsAddPersonDialogOpen(true)}
              >
                Add Person
              </Button>
            </div>
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>ID</TableHead>
                  <TableHead>Name</TableHead>
                  <TableHead className="text-right">Actions</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {personsInHome.length > 0 ? (
                  personsInHome.map((person) => (
                    <TableRow key={person.id}>
                      <TableCell>{person.id}</TableCell>
                      <TableCell>{person.name}</TableCell>
                      <TableCell className="text-right">
                        <Button variant="outline" size="sm" onClick={() => handleRemovePerson(person.id)}>
                          Remove
                        </Button>
                      </TableCell>
                    </TableRow>
                  ))
                ) : (
                  <TableRow>
                    <TableCell colSpan={3} className="text-center py-4 text-muted-foreground">
                      No persons associated with this home
                    </TableCell>
                  </TableRow>
                )}
              </TableBody>
            </Table>
          </TabsContent>

          <TabsContent value="devices" className="mt-4">
            <div className="flex justify-between items-center mb-4">
              <h4 className="text-lg font-semibold">Devices in this Home</h4>
            </div>
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>ID</TableHead>
                  <TableHead>Name</TableHead>
                  <TableHead>Status</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {devicesInHome.length > 0 ? (
                  devicesInHome.map((device) => (
                    <TableRow key={device.id}>
                      <TableCell>{device.id}</TableCell>
                      <TableCell>{device.name}</TableCell>
                      <TableCell>
                        <Badge
                          variant={device.status === "Online" ? "default" : "secondary"}
                          className={device.status === "Online" ? "bg-green-500" : "bg-gray-500"}
                        >
                          {device.status}
                        </Badge>
                      </TableCell>
                    </TableRow>
                  ))
                ) : (
                  <TableRow>
                    <TableCell colSpan={3} className="text-center py-4 text-muted-foreground">
                      No devices in this home
                    </TableCell>
                  </TableRow>
                )}
              </TableBody>
            </Table>
          </TabsContent>
        </Tabs>

        {/* Add Person to Home Dialog */}
        <Dialog open={isAddPersonDialogOpen} onOpenChange={setIsAddPersonDialogOpen}>
          <DialogContent className="sm:max-w-[525px]">
            <DialogHeader>
              <DialogTitle>Add Persons to Home</DialogTitle>
              <DialogDescription>Select persons to add to this home.</DialogDescription>
            </DialogHeader>
            <PersonHomeForm
              allPersons={allPersons.filter((p) => !personsInHome.some((hp) => hp.id === p.id))}
              selectedPersons={selectedPersons}
              setSelectedPersons={setSelectedPersons}
            />
            <DialogFooter>
              <Button variant="outline" onClick={() => setIsAddPersonDialogOpen(false)}>
                Cancel
              </Button>
              <Button className="bg-green-600 hover:bg-green-700" onClick={handleAddPersons}>
                Add Selected Persons
              </Button>
            </DialogFooter>
          </DialogContent>
        </Dialog>
      </CardContent>
    </Card>
  )
}

