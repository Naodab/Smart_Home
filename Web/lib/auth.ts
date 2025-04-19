import Cookies from "js-cookie"

interface AuthData {
  email: string
  tokens: {
    access: string
    refresh: string
  }
}

const ACCESS_TOKEN_COOKIE = "auth_access_token"
const REFRESH_TOKEN_COOKIE = "auth_refresh_token"
const EMAIL_COOKIE = "auth_email"

export const saveAuthData = (authData: AuthData): void => {
  if (typeof window !== "undefined") {
    Cookies.set(ACCESS_TOKEN_COOKIE, authData.tokens.access, {
      expires: 1 / 24, // 1 hour
      secure: process.env.NODE_ENV === "production",
      sameSite: "strict",
    })

    Cookies.set(REFRESH_TOKEN_COOKIE, authData.tokens.refresh, {
      expires: 7, // 7 days
      secure: process.env.NODE_ENV === "production",
      sameSite: "strict",
    })

    Cookies.set(EMAIL_COOKIE, authData.email, {
      expires: 7, // 7 days
      secure: process.env.NODE_ENV === "production",
      sameSite: "strict",
    })

    localStorage.setItem("auth_email", authData.email)
  }
}

export const getAccessToken = (): string | null => {
  if (typeof window !== "undefined") {
    return Cookies.get(ACCESS_TOKEN_COOKIE) || null
  }
  return null
}

export const getRefreshToken = (): string | null => {
  if (typeof window !== "undefined") {
    return Cookies.get(REFRESH_TOKEN_COOKIE) || null
  }
  return null
}

export const getUserEmail = (): string | null => {
  if (typeof window !== "undefined") {
    return Cookies.get(EMAIL_COOKIE) || localStorage.getItem("auth_email") || null
  }
  return null
}

export const getAuthToken = async (): Promise<string | null> => {
  let token = getAccessToken()
  if (!token) {
    const refresh = await refreshAccessToken()
    if (refresh) {
      token = getAccessToken()
    }
  }
  return token
}

export const removeAuthData = (): void => {
  if (typeof window !== "undefined") {
    Cookies.remove(ACCESS_TOKEN_COOKIE)
    Cookies.remove(REFRESH_TOKEN_COOKIE)
    Cookies.remove(EMAIL_COOKIE)
    localStorage.removeItem("auth_email")
    localStorage.removeItem("auth_token")
  }
}

export const isAuthenticated = (): boolean => {
  const token = getAccessToken()
  return !!token
}

export const getDecodedToken = (): any | null => {
  const token = getAccessToken()
  if (!token) return null

  try {
    const base64Url = token.split(".")[1]
    const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/")
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split("")
        .map((c) => {
          return "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2)
        })
        .join(""),
    )

    return JSON.parse(jsonPayload)
  } catch (error) {
    console.error("Error decoding token:", error)
    return null
  }
}

export const isTokenExpired = (): boolean => {
  const decoded = getDecodedToken()
  if (!decoded) return true

  if (!decoded.exp) return false

  const currentTime = Math.floor(Date.now() / 1000)
  return decoded.exp < currentTime
}

export const refreshAccessToken = async (): Promise<boolean> => {
  const refreshToken = getRefreshToken()
  if (!refreshToken) return false

  try {
    const response = await fetch("/api/token/refresh", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ token: refreshToken }),
    })

    if (!response.ok) return false

    const data = await response.json()

    if (data.token?.access) {
      Cookies.set(ACCESS_TOKEN_COOKIE, data.token.access, {
        expires: 1 / 24, // 1 hour
        secure: process.env.NODE_ENV === "production",
        sameSite: "strict",
      })
      return true
    }

    return false
  } catch (error) {
    console.error("Error refreshing token:", error)
    return false
  }
}

