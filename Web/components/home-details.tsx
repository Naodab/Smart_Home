"use client"

import { useState, useEffect } from "react"
import { Card, CardContent } from "@/components/ui/card"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Home, Users, Cpu, Thermometer, Droplets } from "lucide-react"
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
  DeviceInHome,
  PersonInHome,
} from "./api-service"

interface HomeDetailsProps {
  home: {
    id: string
    email: string
    address: string
    persons: PersonInHome[]
    devices: DeviceInHome[]
    temperature: number
    humidity: number
  }
}

export function HomeDetails({ home }: HomeDetailsProps) {
  const [personsInHome, setPersonsInHome] = useState<any[]>([])
  const [devicesInHome, setDevicesInHome] = useState<any[]>([])

  useEffect(() => {
    setPersonsInHome(home.persons || [])
    setDevicesInHome(home.devices || [])
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
              <Cpu className="h-4 w-4" />
              Thiết bị
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
              <h4 className="text-lg font-semibold">Các thiết bị trong nhà này</h4>
            </div>
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Mã</TableHead>
                  <TableHead>Tên</TableHead>
                  <TableHead>Trạng thái</TableHead>
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
                          variant={device.status ? "default" : "secondary"}
                          className={device.status ? "bg-green-500" : "bg-gray-500"}
                        >
                          {device.status ? "Active" : "Inactive"}
                        </Badge>
                      </TableCell>
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

