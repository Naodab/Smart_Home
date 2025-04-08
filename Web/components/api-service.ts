"use client"

import { getAuthToken } from "@/lib/auth"
import { toast } from "./ui/use-toast"
import router from "next/router"

const API_BASE_URL = "http://localhost:8088"

export type ApiError = {
  status: number
  message: string
}

export type Home = {
  id: string
  email: string
  address: string
  temperature: number
  humidity: number
  persons: PersonInHome[]
  devices: DeviceInHome[]
}

export type PersonInHome = {
  id: string
  name: string
  homeIds: string[]
}

export type DeviceInHome = {
  id: string
  name: string
  status: boolean
  homeId: string
}

export type HomeInPerson = {
  id: string
  email: string
  address: string
  temperature: number
  humidity: number
}

export type Person = {
  id: string
  name: string
  home: HomeInPerson
  histories: HistoryInPerson[]
}

export type HistoryInPerson = {
  id: string
  deviceId: string
  deviceName: string
  newStatus: string
  timestamp: string
}

export type HistoryInDevice = {
  id: string
  personId: string
  personName: string
  newStatus: string
  timestamp: string
}

export type HomeInDevice = {
  id: string
  email: string
  address: string
}

export type PersonToSelect = {
  id: string
  name: string
}

export type HomeToSelect = {
  id: string
  email: string
}

export type Device = {
  id: string
  name: string
  status: string
  type: string
  home: HomeInDevice
  histories: HistoryInDevice[]
}

export async function apiCall<T>(
  endpoint: string,
  method: "GET" | "POST" | "PUT" | "DELETE" = "GET",
  data?: any
): Promise<T> {
  try {
    console.log(`Calling API: ${method} ${API_BASE_URL}${endpoint}`);
    const token = await getAuthToken()

    const headers: Record<string, string> = {
      "Content-Type": "application/json",
    }
    if  (token) {
      headers["Authorization"] = `Bearer ${token}`
    } else {
      toast({
        variant: "destructive",
        title: "Unauthorized",
        description: "Please log in again.",
      })
      return Promise.reject({ status: 401, message: "Unauthorized" } as ApiError);
    }

    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
      method,
      headers: headers,
      body: data ? JSON.stringify(data) : undefined,
    });

    if (!response.ok) {
      const errorData = await response.json();
      throw { status: response.status, message: errorData.message || "Unknown error" } as ApiError;
    }

    return (method === "DELETE" ? null : await response.json()) as T;
  } catch (error: any) {
    console.error("API error:", error);
    throw { status: 500, message: "Lỗi kết nối đến server. Vui lòng thử lại." } as ApiError;
  }
}

export const HomeApi = {
  getAll: () => apiCall<Home[]>("/api/homes/"),
  getById: (id: string) => apiCall<Home>(`/api/homes/${id}/`),
  create: (data: Home) => apiCall<Home>("/api/homes/", "POST", data),
  update: (id: string, data: Home) => apiCall<Home>(`/api/homes/${id}/`, "PUT", data),
  delete: (id: string) => apiCall<void>(`/api/homes/${id}/`, "DELETE"),
  getEmails: () => apiCall<HomeToSelect[]>("/api/homes/emails/"),
}

export const PersonApi = {
  getAll: () => apiCall<Person[]>("/api/people/"),
  getById: (id: string) => apiCall<Person>(`/api/people/${id}/`),
  create: (data: Person) => apiCall<Person>("/api/people/", "POST", data),
  update: (id: string, data: Person) => apiCall<Person>(`/api/people/${id}/`, "PUT", data),
  delete: (id: string) => apiCall<void>(`/api/people/${id}/`, "DELETE"),
  getPeopleToAdd: () => apiCall<PersonToSelect[]>("/api/people/select/"),
}

export const DeviceApi = {
  getAll: () => apiCall<Device[]>("/api/devices/"),
  getById: (id: string) => apiCall<Device>(`/api/devices/${id}/`),
  create: (data: any) => apiCall<any>("/api/devices/", "POST", data),
  update: (id: string, data: Device) => apiCall<Device>(`/api/devices/${id}/`, "PUT", data),
  delete: (id: string) => apiCall<void>(`/api/devices/${id}/`, "DELETE"),
  updateStatus: (id: string, status: string, personId: string) =>
    apiCall<Device>(`/api/devices/${id}/status`, "PUT", { status, personId }),
}

export const HomePersonApi = {
  updateHomePersons: (data: { home_id: string; person_ids: string[] }) =>
    apiCall<any>(`/api/homes/${data.home_id}/persons/`, "PUT", data),
  deleteHomePersons: (data: { home_id: string; person_ids: string[] }) =>
    apiCall<any>(`/api/homes/${data.home_id}/persons/`, "DELETE", data),
}

export const AuthApi = {
  logout: () => apiCall<void>("/api/logout/", "POST"),
  changePassword: (oldPass: string, newPass: string) =>
    apiCall<any>("/api/change-password/", "POST", { oldPass, newPass }),
}