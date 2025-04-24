import { LoginForm } from "@/components/login-form"
import type { Metadata } from "next"

export const metadata: Metadata = {
  title: "Đăng nhập - SmartHome",
  description: "Đăng nhập vào hệ thống quản lý SmartHome",
}

export default function LoginPage() {
  return (
    <div className="flex min-h-screen items-center justify-center bg-gradient-to-r from-green-50 to-green-100">
      <div className="w-full max-w-md p-8 space-y-8 bg-white rounded-lg shadow-lg">
        <div className="text-center">
          <h1 className="text-3xl font-bold text-green-700">SmartHome</h1>
          <p className="mt-2 text-gray-600">Đăng nhập quản trị</p>
        </div>
        <LoginForm />
      </div>
    </div>
  )
}

