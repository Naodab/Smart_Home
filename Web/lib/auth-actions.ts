"use server"

import { cookies } from "next/headers"
import { redirect } from "next/navigation"
import API_BASE_URL from "./api-config"

type AuthResult = {
  success: boolean
  error?: string
  data?: any
}

export async function loginUser(data: {
  email: string
  password: string
}): Promise<AuthResult> {

  try {
    const response = await fetch(`${API_BASE_URL}/api/admins/login/`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(data),
    });

    if (!response.ok) {
      const errorData = await response.json();
      return {
        success: false,
        error: errorData.error || "Invalid email or password",
      };
    }
    
    const result = await response.json();
    return { success: true, data: result }
  } catch (error) {
    return {
      success: false,
      error: "An unexpected error occurred",
    }
  }
}

export async function logoutUser() {
  const cookieStore = await cookies()
  cookieStore.delete("session")
  cookieStore.delete("access_token")
  cookieStore.delete("refresh_token")
  redirect("/login")
}