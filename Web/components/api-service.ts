const API_BASE_URL = "http://localhost:8088"

export type ApiError = {
  status: number
  message: string
}

export type Home = {
  id: string
  email: string
  address: string
  persons: Person[]
  devices: Device[]
}

export type Person = {
  id: string
  name: string
  homeIds: string[]
}

export type Device = {
  id: string
  name: string
  status: boolean
}

export async function apiCall<T>(
  endpoint: string,
  method: "GET" | "POST" | "PUT" | "DELETE" = "GET",
  data?: any
): Promise<T> {
  try {
    console.log(`Calling API: ${method} ${API_BASE_URL}${endpoint}`);

    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
      method,
      headers: {
        "Content-Type": "application/json",
      },
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
}

export const PersonApi = {
  getAll: () => apiCall<any[]>("/api/persons"),
  getById: (id: string) => apiCall<any>(`/api/persons/${id}`),
  create: (data: any) => apiCall<any>("/api/persons", "POST", data),
  update: (id: string, data: any) => apiCall<any>(`/api/persons/${id}`, "PUT", data),
  delete: (id: string) => apiCall<void>(`/api/persons/${id}`, "DELETE"),
}

export const DeviceApi = {
  getAll: () => apiCall<any[]>("/api/devices"),
  getById: (id: string) => apiCall<any>(`/api/devices/${id}`),
  create: (data: any) => apiCall<any>("/api/devices", "POST", data),
  update: (id: string, data: any) => apiCall<any>(`/api/devices/${id}`, "PUT", data),
  delete: (id: string) => apiCall<void>(`/api/devices/${id}`, "DELETE"),
  updateStatus: (id: string, status: string, personId: string) =>
    apiCall<any>(`/api/devices/${id}/status`, "PUT", { status, personId }),
}

export const ConnectionApi = {
  getPersonConnections: (personId: string) => apiCall<string[]>(`/api/persons/${personId}/connections`),
  saveConnections: (personId: string, homeIds: string[]) =>
    apiCall<any>(`/api/persons/${personId}/connections`, "PUT", {
      personId,
      connections: homeIds,
    }),
}

