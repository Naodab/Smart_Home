"use client"

import { Avatar, AvatarFallback } from "@/components/ui/avatar"
import { Button } from "@/components/ui/button"
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import { getDecodedToken, getUserEmail, removeAuthData } from "@/lib/auth"
import { Lock, LogOut, User } from "lucide-react"
import { useRouter } from "next/navigation"
import { useEffect, useState } from "react"
import { ChangePasswordDialog } from "./change-password-dialog"
import { AuthApi } from "./api-service"

export function UserNav() {
  const router = useRouter()
  const [userEmail, setUserEmail] = useState("")
  const [userName, setUserName] = useState("")
  const [isChangePasswordOpen, setIsChangePasswordOpen] = useState(false)
  
  useEffect(() => {
    const email = getUserEmail()
    if (email) {
      setUserEmail(email)
      const nameFromEmail = email.split("@")[0]
      setUserName(nameFromEmail.charAt(0).toUpperCase() + nameFromEmail.slice(1))
    }
    const decodedToken = getDecodedToken()
    if (decodedToken) {
      if (decodedToken.email) {
        setUserEmail(decodedToken.email)
      }
    }
  }, [])

  const handleLogout = async () => {
    await AuthApi.logout()
    removeAuthData()
    router.push("/login")
  }

  return (
    <>
      <DropdownMenu>
        <DropdownMenuTrigger asChild>
          <Button variant="ghost" className="relative h-8 w-8 rounded-full">
            <Avatar className="h-8 w-8">
              <AvatarFallback className="bg-green-200 text-green-700">A</AvatarFallback>
            </Avatar>
          </Button>
        </DropdownMenuTrigger>
        <DropdownMenuContent className="w-56" align="end" forceMount>
          <DropdownMenuLabel className="font-normal">
            <div className="flex flex-col space-y-1">
              <p className="text-sm font-medium leading-none">{userName.charAt(0).toUpperCase()}</p>
              <p className="text-xs leading-none text-muted-foreground">{userEmail}</p>
            </div>
          </DropdownMenuLabel>
          <DropdownMenuSeparator />
          <DropdownMenuItem>
            <User className="mr-2 h-4 w-4" />
            <span>Hồ sơ</span>
          </DropdownMenuItem>
          <DropdownMenuItem onClick={() => setIsChangePasswordOpen(true)}>
              <Lock className="mr-2 h-4 w-4" />
              <span>Đổi mật khẩu</span>
            </DropdownMenuItem>
          <DropdownMenuSeparator />
          <DropdownMenuItem onClick={handleLogout}>
            <LogOut className="mr-2 h-4 w-4" />
            <span>Đăng xuất</span>
          </DropdownMenuItem>
        </DropdownMenuContent>
      </DropdownMenu><ChangePasswordDialog 
          open={isChangePasswordOpen} onOpenChange={setIsChangePasswordOpen} />
    </>
  )
}

