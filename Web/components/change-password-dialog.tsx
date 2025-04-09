"use client"

import type React from "react"

import { useState } from "react"
import { Button } from "@/components/ui/button"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Loader2 } from "lucide-react"
import { useToast } from "@/hooks/use-toast"
import { Alert, AlertDescription } from "@/components/ui/alert"
import { AlertCircle } from "lucide-react"
import { getAuthToken } from "@/lib/auth"
import { AuthApi } from "./api-service"

interface ChangePasswordDialogProps {
  open: boolean
  onOpenChange: (open: boolean) => void
}

export function ChangePasswordDialog({ open, onOpenChange }: ChangePasswordDialogProps) {
  const { toast } = useToast()
  const [currentPassword, setCurrentPassword] = useState("")
  const [newPassword, setNewPassword] = useState("")
  const [confirmPassword, setConfirmPassword] = useState("")
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [error, setError] = useState("")

  const resetForm = () => {
    setCurrentPassword("")
    setNewPassword("")
    setConfirmPassword("")
    setError("")
  }

  const handleClose = () => {
    resetForm()
    onOpenChange(false)
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError("")

    // Validate passwords
    if (newPassword.length < 8) {
      setError("Mật khẩu mới phải có ít nhất 8 ký tự")
      return
    }

    if (newPassword !== confirmPassword) {
      setError("Mật khẩu mới không khớp")
      return
    }

    setIsSubmitting(true)

    try {
      const response = await AuthApi.changePassword(currentPassword, newPassword)
      console.log(currentPassword, newPassword)

      if (!response?.success) {
        const data = await response.json()
        throw new Error(data.message || "Không thể thay đổi mật khẩu")
      }

      toast({
        title: "Thành công",
        description: "Mật khẩu được thay đổi thành công",
        variant: "success",
      })

      handleClose()
    } catch (error) {
      setError(error instanceof Error ? error.message : "Đã xảy ra lỗi khi thay đổi mật khẩu")
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <Dialog open={open} onOpenChange={handleClose}>
      <DialogContent className="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle>Đổi mật khẩu</DialogTitle>
          <DialogDescription>Nhập mật khẩu hiện tại và  mật khẩu mới bên dưới.</DialogDescription>
        </DialogHeader>
        <form onSubmit={handleSubmit}>
          {error && (
            <Alert variant="destructive" className="mb-4">
              <AlertCircle className="h-4 w-4" />
              <AlertDescription>{error}</AlertDescription>
            </Alert>
          )}
          <div className="grid gap-4 py-4">
            <div className="grid gap-2">
              <Label htmlFor="current-password">Mật khẩu hiện tại</Label>
              <Input
                id="current-password"
                type="password"
                value={currentPassword}
                onChange={(e) => setCurrentPassword(e.target.value)}
                required
                autoComplete="current-password"
              />
            </div>
            <div className="grid gap-2">
              <Label htmlFor="new-password">Mật khẩu mới</Label>
              <Input
                id="new-password"
                type="password"
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
                required
                autoComplete="new-password"
              />
            </div>
            <div className="grid gap-2">
              <Label htmlFor="confirm-password">Xác nhận mật khẩu mới</Label>
              <Input
                id="confirm-password"
                type="password"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                required
                autoComplete="new-password"
              />
            </div>
          </div>
          <DialogFooter>
            <Button type="button" variant="outline" onClick={handleClose} disabled={isSubmitting}>
              Hủy
            </Button>
            <Button type="submit" className="bg-green-600 hover:bg-green-700" disabled={isSubmitting}>
              {isSubmitting ? (
                <>
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                  Đang thay đổi mật khẩu...
                </>
              ) : (
                "Thay đổi mật khẩu"
              )}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  )
}
