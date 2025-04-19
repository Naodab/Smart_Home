"use client"

import { useState, useEffect } from "react"
import { Card, CardContent } from "@/components/ui/card"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { MapPin, Users, Cpu } from "lucide-react"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Badge } from "@/components/ui/badge"
import { DeviceInLocation, Location } from "./api-service"

interface LocationDetailsProps {
  location: Location
}

export function LocationDetails({ location }: LocationDetailsProps) {
  const [devicesInLocation, setDevicesInLocation] = useState<DeviceInLocation[]>([])

  useEffect(() => {
    setDevicesInLocation(location.devices || [])
  }, [location.id])

  return (
    <Card>
      <CardContent className="pt-6">
        <div className="flex items-center gap-4 mb-6">
          <div className="p-2 bg-green-100 rounded-full">
            <MapPin className="h-6 w-6 text-green-600" />
          </div>
          <h3 className="text-xl font-semibold">{location.name}</h3>
        </div>

        <div className="grid grid-cols-2 gap-4 mb-6">
          <div className="space-y-1">
            <p className="text-sm text-muted-foreground">ID</p>
            <p className="font-medium">{location.id}</p>
          </div>
          <div className="space-y-1">
            <p className="text-sm text-muted-foreground">Thuộc về nhà</p>
            <p className="font-medium">{location.home.email}</p>
          </div>
        </div>

        <Tabs defaultValue="devices" className="mt-6">
          <TabsList className="grid w-full grid-cols-2">
            <TabsTrigger value="devices" className="flex items-center gap-2">
              <Cpu className="h-4 w-4" />
              Thiết bị
            </TabsTrigger>
          </TabsList>

          <TabsContent value="devices" className="mt-4">
            <div className="flex justify-between items-center mb-4">
              <h4 className="text-lg font-semibold">Thiết bị trong vị trí này</h4>
            </div>
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>ID</TableHead>
                  <TableHead>Tên thiết bị</TableHead>
                  <TableHead>Loại</TableHead>
                  <TableHead>Trạng thái</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {devicesInLocation.length > 0 ? (
                  devicesInLocation.map((device) => (
                    <TableRow key={device.id}>
                      <TableCell>{device.id}</TableCell>
                      <TableCell>{device.name}</TableCell>
                      <TableCell>{device.type}</TableCell>
                      <TableCell>
                        {device.type === "fan" ? (
                          <Badge
                            variant={device.status === "0" ? "secondary" : "default"}
                            className={
                              device.status === "0"
                                ? "bg-gray-500"
                                : device.status === "1"
                                  ? "bg-green-300"
                                  : device.status === "2"
                                    ? "bg-green-500"
                                    : "bg-green-700"
                            }
                          >
                            Mức {device.status}
                          </Badge>
                        ) : (
                          <Badge
                            variant={(device.status === "on" || device.status === "close") ? "default" : "secondary"}
                            className={(device.status === "on" || device.status === "close") ? "bg-green-500" : "bg-gray-500"}
                          >
                            {device.type === 'light'
                              ? (device.status === "on" ? "Bật" : "Tắt") 
                              : (device.status === "open" ? "Mở" : "Đóng")}
                          </Badge>
                        )}
                      </TableCell>
                    </TableRow>
                  ))
                ) : (
                  <TableRow>
                    <TableCell colSpan={4} className="text-center py-4 text-muted-foreground">
                      Không có thiết bị nào trong vị trí này
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
