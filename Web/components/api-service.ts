// This is a mock API service to simulate API calls and errors

// Simulate API delay
const delay = (ms: number) => new Promise((resolve) => setTimeout(resolve, ms))

// Modify the simulateRandomError function to reduce error rate for debugging
const simulateRandomError = (errorRate = 0.2) => {
  return Math.random() < errorRate
}

// Error types
export type ApiError = {
  status: number
  message: string
}

// Update the apiCall function to better handle the data validation
export async function apiCall<T>(
  endpoint: string,
  method: "GET" | "POST" | "PUT" | "DELETE" = "GET",
  data?: any,
): Promise<T> {
  try {
    // Simulate network delay
    await delay(800)

    // Validate data for specific endpoints
    if (endpoint.includes("/connections") && method === "PUT") {
      // Check if the data has the required format for connections
      if (!data || !data.personId || !data.connections || !Array.isArray(data.connections)) {
        throw {
          status: 400,
          message: "Bad request: Invalid data format for connections. Expected {personId, connections: string[]}",
        }
      }
    }

    // Simulate random errors (for demonstration) with reduced rate
    if (simulateRandomError(0.2)) {
      const errorTypes = [
        { status: 400, message: "Bad request: Invalid data provided" },
        { status: 401, message: "Unauthorized: Please login again" },
        { status: 403, message: "Forbidden: You don't have permission to perform this action" },
        { status: 404, message: "Not found: The requested resource doesn't exist" },
        { status: 500, message: "Server error: Something went wrong on our end" },
        { status: 503, message: "Service unavailable: Please try again later" },
      ]

      const randomError = errorTypes[Math.floor(Math.random() * errorTypes.length)]
      throw randomError
    }

    // Simulate successful response
    console.log(`API ${method} to ${endpoint}`, data)

    // For GET connections, return mock data
    if (endpoint.includes("/connections") && method === "GET") {
      const personId = endpoint.split("/")[2] // Extract personId from URL
      const mockConnections = {
        "1": ["1", "2"],
        "2": ["1"],
        "3": ["2"],
        "4": ["3"],
        "5": [],
      }
      return { data: mockConnections[personId as keyof typeof mockConnections] || [] } as unknown as T
    }

    return { success: true, data } as unknown as T
  } catch (error) {
    console.error("API Error:", error)
    throw error
  }
}

// Specific API functions for different entities
export const HomeApi = {
  getAll: () => apiCall<any[]>("/api/homes"),
  getById: (id: string) => apiCall<any>(`/api/homes/${id}`),
  create: (data: any) => apiCall<any>("/api/homes", "POST", data),
  update: (id: string, data: any) => apiCall<any>(`/api/homes/${id}`, "PUT", data),
  delete: (id: string) => apiCall<void>(`/api/homes/${id}`, "DELETE"),
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

// Connection API functions
export const ConnectionApi = {
  getPersonConnections: (personId: string) => apiCall<string[]>(`/api/persons/${personId}/connections`),
  saveConnections: (personId: string, homeIds: string[]) =>
    apiCall<any>(`/api/persons/${personId}/connections`, "PUT", {
      personId, // Make sure personId is included in the payload
      connections: homeIds, // Wrap homeIds in a 'connections' property
    }),
}

