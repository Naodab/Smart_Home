"use client"

import type React from "react"

import { useState } from "react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Loader2 } from "lucide-react"
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue
} from "@/components/ui/select"
import { HomeToSelect } from "./api-service"

interface PersonFormProps {
  initialData?: {
    id?: string
    name: string
    home?: {
      id?: string
      email?: string
    }
  }
  onSubmit: (data: any) => void
  onCancel: () => void
  isSubmitting?: boolean
  homes: HomeToSelect[]
}

export function PersonForm({ initialData, onSubmit, onCancel, isSubmitting = false, homes }: PersonFormProps) {
  const [formData, setFormData] = useState({
    id: initialData?.id ?? "",
    name: initialData?.name ?? "",
    home: initialData?.home ?? {  
      email: "",
    },
  })

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target
    setFormData({
      ...formData,
      [name]: value,
    })
  }

  const handleHomeChange = (value: string) => {
    setFormData({
      ...formData,
      home: {
        email: value,  
      },
    })
  }

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    onSubmit(formData)
  }

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div className="space-y-2">
        <Label htmlFor="name">Full Name</Label>
        <Input id="name" name="name" value={formData.name} onChange={handleChange} required disabled={isSubmitting} />
      </div>
      <div className="space-y-2">
        <Label htmlFor="homeId">Home</Label>
        <Select value={formData.home.email} onValueChange={handleHomeChange} disabled={isSubmitting}>
          <SelectTrigger id="homeId">
            <SelectValue placeholder="Select a home" />
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
          Cancel
        </Button>
        <Button type="submit" className="bg-green-600 hover:bg-green-700" disabled={isSubmitting}>
        {isSubmitting ? (
            <>
              <Loader2 className="mr-2 h-4 w-4 animate-spin" />
              {initialData?.id ? "Updating..." : "Creating..."}
            </>
          ) : (
            <>{initialData?.id ? "Update" : "Create"} Person</>
          )}
        </Button>
      </div>
    </form>
  )
}

