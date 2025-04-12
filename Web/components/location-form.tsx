"use client"

import type React from "react"

import { useState, useEffect } from "react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Loader2 } from "lucide-react"
import { HomeApi, HomeToSelect, Location } from "./api-service"

interface LocationFormProps {
  initialData?: Location
  onSubmit: (data: any) => void
  onCancel: () => void
  isSubmitting?: boolean
}

export function LocationForm({ initialData, onSubmit, onCancel, isSubmitting = false }: LocationFormProps) {
  const [homes, setHomes] = useState<HomeToSelect[]>([])
  const [formData, setFormData] = useState({
    id: initialData?.id ?? "",
    name: initialData?.name ?? "",
    homeEmail: initialData?.home.email ?? "",
  })

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

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target
    setFormData({
      ...formData,
      [name]: name === "floor" ? Number(value) : value,
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
    const selectedHome = homes.find((home) => home.email === formData.homeEmail)
    onSubmit({
      ...formData,
      homeName: selectedHome?.email,
    })
  }

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div className="space-y-2">
        <Label htmlFor="name">Tên vị trí</Label>
        <Input id="name" name="name" value={formData.name} onChange={handleChange} required disabled={isSubmitting} />
      </div>
      <div className="space-y-2">
        <Label htmlFor="homeEmail">Thuộc về nhà</Label>
        <Select
          value={formData.homeEmail}
          onValueChange={(value) => handleSelectChange("homeEmail", value)}
          disabled={isSubmitting}
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
        <Button type="button" variant="outline" onClick={onCancel} disabled={isSubmitting}>
          Hủy
        </Button>
        <Button type="submit" className="bg-green-600 hover:bg-green-700" disabled={isSubmitting}>
          {isSubmitting ? (
            <>
              <Loader2 className="mr-2 h-4 w-4 animate-spin" />
              {initialData?.id ? "Đang cập nhật..." : "Đang tạo..."}
            </>
          ) : (
            <>{initialData?.id ? "Cập nhật" : "Tạo"} vị trí</>
          )}
        </Button>
      </div>
    </form>
  )
}
