"use client"

import { useState, useEffect } from "react"
import { Card, CardContent } from "@/components/ui/card"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Home, Users, Cpu, Thermometer, Droplets, DoorOpen } from "lucide-react"
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow
} from "@/components/ui/table"
import { Badge } from "@/components/ui/badge"
import { 
  LocationInHome,
  PersonInHome,
} from "./api-service"

interface HomeDetailsProps {
  home: {
    id: string
    email: string
    address: string
    persons: PersonInHome[]
    locations: LocationInHome[]
    temperature: number
    humidity: number
  }
}

export function HomeDetails({ home }: HomeDetailsProps) {
  const [personsInHome, setPersonsInHome] = useState<any[]>([])
  const [locationsInHome, setLocationsInHome] = useState<any[]>([])

  useEffect(() => {
    setPersonsInHome(home.persons || [])
    setLocationsInHome(home.locations || [])
  }, [home.id])

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
            <p className="text-sm text-muted-foreground">Mã</p>
            <p className="font-medium">{home.id}</p>
          </div>
          <div className="space-y-1">
            <p className="text-sm text-muted-foreground">Địa chỉ</p>
            <p className="font-medium">{home.address}</p>
          </div>
        </div>

        <div className="grid grid-cols-2 gap-4 mb-6">
          <div className="flex items-center gap-2">
            <Thermometer className="h-5 w-5 text-orange-500" />
            <div>
              <p className="text-sm text-muted-foreground">Nhiệt độ</p>
              <p className="font-medium">{home.temperature}°C</p>
            </div>
          </div>
          <div className="flex items-center gap-2">
            <Droplets className="h-5 w-5 text-blue-500" />
            <div>
              <p className="text-sm text-muted-foreground">Độ ẩm</p>
              <p className="font-medium">{home.humidity}%</p>
            </div>
          </div>
        </div>


        <Tabs defaultValue="persons" className="mt-6">
          <TabsList className="grid w-full grid-cols-2">
            <TabsTrigger value="persons" className="flex items-center gap-2">
              <Users className="h-4 w-4" />
              Người dùng
            </TabsTrigger>
            <TabsTrigger value="devices" className="flex items-center gap-2">
              <DoorOpen className="h-4 w-4" />
              Vị trí
            </TabsTrigger>
          </TabsList>

          <TabsContent value="persons" className="mt-4">
            <div className="flex justify-between items-center mb-4">
              <h4 className="text-lg font-semibold">Người dùng trong nhà này</h4>
            </div>
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Mã</TableHead>
                  <TableHead>Tên</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {personsInHome.length > 0 ? (
                  personsInHome.map((person) => (
                    <TableRow key={person.id}>
                      <TableCell>{person.id}</TableCell>
                      <TableCell>{person.name}</TableCell>
                    </TableRow>
                  ))
                ) : (
                  <TableRow>
                    <TableCell colSpan={3} className="text-center py-4 text-muted-foreground">
                      Không có người dùng nào trong nhà này
                    </TableCell>
                  </TableRow>
                )}
              </TableBody>
            </Table>
          </TabsContent>

          <TabsContent value="devices" className="mt-4">
            <div className="flex justify-between items-center mb-4">
              <h4 className="text-lg font-semibold">Các vị trí trong nhà này</h4>
            </div>
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Mã</TableHead>
                  <TableHead>Tên</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {locationsInHome.length > 0 ? (
                  locationsInHome.map((location) => (
                    <TableRow key={location.id}>
                      <TableCell>{location.id}</TableCell>
                      <TableCell>{location.name}</TableCell>
                      </TableRow>
                  ))
                ) : (
                  <TableRow>
                    <TableCell colSpan={3} className="text-center py-4 text-muted-foreground">
                      Không có thiết bị nào trong nhà này
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

