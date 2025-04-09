"use client"

import type React from "react"

import { useEffect, useState } from "react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue
} from "@/components/ui/select"
import { Device, HomeApi, HomeToSelect } from "./api-service"
import { Loader2 } from "lucide-react"

interface DeviceFormProps {
  initialData?: Device
  onSubmit: (data: any) => void
  onCancel: () => void
  isSubmitting?: boolean
}

export function DeviceForm({
  initialData,
  onSubmit,
  onCancel,
  isSubmitting=false
}: DeviceFormProps) {
  const [homes, setHomes] = useState<HomeToSelect[]>([])

  useEffect(() => {
    const fetchHomes = async () => {
      try {
        const homes = await HomeApi.getEmails()
        setHomes(homes)
      } catch (error) {
        console.error("Failed to fetch homes:", error)
      }
    }
    fetchHomes()
  }, [])

  const [formData, setFormData] = useState({
    id: initialData?.id ?? "",
    name: initialData?.name ?? "",
    homeEmail: initialData?.home?.email ?? "",
    type: initialData?.type ?? "light",
    status: initialData?.status ?? (initialData?.type === "quạt" ? "0" : "off"),
  })

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target
    setFormData({
      ...formData,
      [name]: value,
    })
  }

  const handleSelectChange = (field: string, value: any) => {
    console.log(field, ":", value)
    setFormData({
      ...formData,
      [field]: value,
    })
  }
  
  const handleTypeChange = (value: string) => {
    console.log(value)
    setFormData({
      ...formData,
      type: value
    })
    console.log(formData)
  }

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    const selectedHome = homes.find((home) => home.email === formData.homeEmail)
    const formattedData = {
      id: initialData?.id ?? "",
      name: formData.name,
      type: formData.type,
      home: {
        email: selectedHome?.email
      }
    }
    onSubmit(formattedData)
  }

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div className="space-y-2">
        <Label htmlFor="name">Tên thiết bị</Label>
        <Input id="name" name="name" value={formData.name} onChange={handleChange} required />
      </div>
      <div className="space-y-2">
        <Label htmlFor="type">Loại thiết bị</Label>
        <Select value={formData.type} onValueChange={handleTypeChange}>
          <SelectTrigger>
            <SelectValue placeholder="Select device type" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="door">Cửa</SelectItem>
            <SelectItem value="light">Đèn</SelectItem>
            <SelectItem value="curtain">Rèm</SelectItem>
            <SelectItem value="fan">Quạt</SelectItem>
          </SelectContent>
        </Select>
      </div>
      <div className="space-y-2">
        <Label htmlFor="homeEmail">Email nhà</Label>
        <Select
          value={formData.homeEmail}
          onValueChange={(value) => handleSelectChange("homeEmail", value)}
        >
          <SelectTrigger>
            <SelectValue placeholder="Chọn một nhà" />
          </SelectTrigger>
          <SelectContent>
            {homes.map((home) => (
              <SelectItem key={home.email} value={home.email}>
                {home.email}
              </SelectItem>
            ))}
          </SelectContent>
        </Select>
      </div>
      <div className="flex justify-end gap-2">
        <Button type="button" variant="outline" onClick={onCancel}>
          Hủy
        </Button>
        <Button type="submit" className="bg-green-600 hover:bg-green-700" disabled={isSubmitting}>
          {isSubmitting ? (
            <>
              <Loader2 className="mr-2 h-4 w-4 animate-spin" />
              {initialData?.id ? "Đang cập nhật..." : "Đang  tạo..."}
            </>
          ) : (
            <>{initialData?.id ? "Cập nhật" : "Tạo"} thiết bị</>
          )}
        </Button>
      </div>
    </form>
  )
}

