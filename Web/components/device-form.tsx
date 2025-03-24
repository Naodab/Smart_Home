"use client"

import type React from "react"

import { useState } from "react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"

// Mock data for homes
const homes = [
  { id: "1", name: "main.residence@example.com" },
  { id: "2", name: "beach.house@example.com" },
  { id: "3", name: "mountain.cabin@example.com" },
]

interface DeviceFormProps {
  initialData?: {
    id?: string
    name: string
    status: string
    homeId: string
    homeName?: string
  }
  onSubmit: (data: any) => void
  onCancel: () => void
}

export function DeviceForm({ initialData, onSubmit, onCancel }: DeviceFormProps) {
  const [formData, setFormData] = useState({
    id: initialData?.id || "",
    name: initialData?.name || "",
    status: initialData?.status || "Online",
    homeId: initialData?.homeId || "",
  })

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target
    setFormData({
      ...formData,
      [name]: value,
    })
  }

  const handleSelectChange = (field: string, value: string) => {
    setFormData({
      ...formData,
      [field]: value,
    })
  }

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    const selectedHome = homes.find((home) => home.id === formData.homeId)
    onSubmit({
      ...formData,
      homeName: selectedHome?.name,
    })
  }

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div className="space-y-2">
        <Label htmlFor="name">Device Name</Label>
        <Input id="name" name="name" value={formData.name} onChange={handleChange} required />
      </div>
      <div className="space-y-2">
        <Label htmlFor="status">Status</Label>
        <Select value={formData.status} onValueChange={(value) => handleSelectChange("status", value)}>
          <SelectTrigger>
            <SelectValue placeholder="Select status" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="Online">Online</SelectItem>
            <SelectItem value="Offline">Offline</SelectItem>
          </SelectContent>
        </Select>
      </div>
      <div className="space-y-2">
        <Label htmlFor="homeId">Home Email</Label>
        <Select value={formData.homeId} onValueChange={(value) => handleSelectChange("homeId", value)}>
          <SelectTrigger>
            <SelectValue placeholder="Select a home" />
          </SelectTrigger>
          <SelectContent>
            {homes.map((home) => (
              <SelectItem key={home.id} value={home.id}>
                {home.name}
              </SelectItem>
            ))}
          </SelectContent>
        </Select>
      </div>
      <div className="flex justify-end gap-2">
        <Button type="button" variant="outline" onClick={onCancel}>
          Cancel
        </Button>
        <Button type="submit" className="bg-green-600 hover:bg-green-700">
          {initialData?.id ? "Update" : "Create"} Device
        </Button>
      </div>
    </form>
  )
}

