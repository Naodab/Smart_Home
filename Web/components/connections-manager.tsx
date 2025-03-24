"use client"

import { useState, useEffect } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Label } from "@/components/ui/label"
import { Button } from "@/components/ui/button"
import { Checkbox } from "@/components/ui/checkbox"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { useToast } from "@/hooks/use-toast"
import { Loader2, Save } from "lucide-react"
import { ConnectionApi, type ApiError } from "@/components/api-service"

// Mock data for persons
const allPersons = [
  { id: "1", name: "John Doe" },
  { id: "2", name: "Jane Smith" },
  { id: "3", name: "Bob Johnson" },
  { id: "4", name: "Alice Williams" },
  { id: "5", name: "Charlie Brown" },
]

// Mock data for homes
const allHomes = [
  { id: "1", email: "main.residence@example.com", address: "123 Main St, Anytown, USA" },
  { id: "2", email: "beach.house@example.com", address: "456 Ocean Ave, Beachtown, USA" },
  { id: "3", email: "mountain.cabin@example.com", address: "789 Mountain Rd, Highlands, USA" },
  { id: "4", email: "downtown.apt@example.com", address: "101 City Center, Metropolis, USA" },
  { id: "5", email: "lakeside.cottage@example.com", address: "202 Lake View, Waterfront, USA" },
]

// Mock data for person-home connections
const initialConnections = {
  "1": ["1", "2"], // John Doe is connected to homes 1 and 2
  "2": ["1"], // Jane Smith is connected to home 1
  "3": ["2"], // Bob Johnson is connected to home 2
  "4": ["3"], // Alice Williams is connected to home 3
  "5": [], // Charlie Brown has no connections
}

// Mock API function to save connections
// const saveConnectionsToAPI = async (personId: string, homeIds: string[]) => {
//   // Simulate API call with random success/failure
//   await new Promise(resolve => setTimeout(resolve, 1000));

//   // Simulate random error (30% chance)
//   if (Math.random() < 0.3) {
//     throw {
//       status: 500,
//       message: "Server error: Failed to save connections. Please try again."
//     };
//   }

//   return { success: true, personId, homeIds };
// };

export function ConnectionsManager() {
  const { toast } = useToast()
  const [selectedPersonId, setSelectedPersonId] = useState<string>("")
  const [connectedHomeIds, setConnectedHomeIds] = useState<string[]>([])
  const [originalConnectedHomeIds, setOriginalConnectedHomeIds] = useState<string[]>([])
  const [isLoading, setIsLoading] = useState<boolean>(false)
  const [isSaving, setIsSaving] = useState<boolean>(false)
  const [hasChanges, setHasChanges] = useState<boolean>(false)

  // Load person's connected homes when a person is selected
  useEffect(() => {
    if (selectedPersonId) {
      setIsLoading(true)

      // Use the API service
      ConnectionApi.getPersonConnections(selectedPersonId)
        .then((response) => {
          // Extract the connections array from the response
          // The API might return { data: string[] } instead of string[] directly
          const connections =
            response.data || response || initialConnections[selectedPersonId as keyof typeof initialConnections] || []
          setConnectedHomeIds(Array.isArray(connections) ? connections : [])
          setOriginalConnectedHomeIds(Array.isArray(connections) ? [...connections] : [])
          setHasChanges(false)
        })
        .catch((error) => {
          console.error("Failed to load connections:", error)
          const apiError = error as ApiError
          toast({
            title: "Error",
            description: apiError.message || "Failed to load connections. Please try again.",
            variant: "destructive",
          })

          // Fallback to mock data on error
          const fallbackConnections = initialConnections[selectedPersonId as keyof typeof initialConnections] || []
          setConnectedHomeIds(fallbackConnections)
          setOriginalConnectedHomeIds([...fallbackConnections])
        })
        .finally(() => {
          setIsLoading(false)
        })
    } else {
      setConnectedHomeIds([])
      setOriginalConnectedHomeIds([])
      setHasChanges(false)
    }
  }, [selectedPersonId, toast])

  // Check if there are unsaved changes
  useEffect(() => {
    if (!selectedPersonId) return

    // Check if arrays are different
    const hasUnsavedChanges =
      connectedHomeIds.length !== originalConnectedHomeIds.length ||
      connectedHomeIds.some((id) => !originalConnectedHomeIds.includes(id)) ||
      originalConnectedHomeIds.some((id) => !connectedHomeIds.includes(id))

    setHasChanges(hasUnsavedChanges)
  }, [connectedHomeIds, originalConnectedHomeIds, selectedPersonId])

  const handleHomeToggle = (homeId: string) => {
    setConnectedHomeIds((prev) => {
      if (prev.includes(homeId)) {
        return prev.filter((id) => id !== homeId)
      } else {
        return [...prev, homeId]
      }
    })
  }

  const handleSaveConnections = async () => {
    if (!selectedPersonId || !hasChanges) return

    setIsSaving(true)
    try {
      const response = await ConnectionApi.saveConnections(selectedPersonId, connectedHomeIds)

      // Check if the response indicates success
      if (response && (response.success || response.data)) {
        // Update original connections to match current state
        setOriginalConnectedHomeIds([...connectedHomeIds])
        setHasChanges(false)

        toast({
          title: "Success",
          description: "Person-home connections saved successfully",
          variant: "success",
        })
      } else {
        throw new Error("Unexpected response format")
      }
    } catch (error) {
      console.error("Failed to save connections:", error)
      const apiError = error as ApiError
      toast({
        title: "Error",
        description: apiError.message || "Failed to save connections. Please try again.",
        variant: "destructive",
      })
    } finally {
      setIsSaving(false)
    }
  }

  const selectedPerson = allPersons.find((person) => person.id === selectedPersonId)

  return (
    <Card>
      <CardHeader>
        <CardTitle>Manage Person-Home Connections</CardTitle>
        <CardDescription>Select a person and manage which homes they are connected to</CardDescription>
      </CardHeader>
      <CardContent className="space-y-6">
        <div className="space-y-2">
          <Label htmlFor="person-select">Select Person</Label>
          <Select value={selectedPersonId} onValueChange={setSelectedPersonId} disabled={isSaving}>
            <SelectTrigger id="person-select" className="w-full md:w-[300px]">
              <SelectValue placeholder="Select a person" />
            </SelectTrigger>
            <SelectContent>
              {allPersons.map((person) => (
                <SelectItem key={person.id} value={person.id}>
                  {person.name}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>

        {selectedPersonId && (
          <>
            <div className="flex items-center justify-between">
              <h3 className="text-lg font-medium">{selectedPerson?.name}'s Connected Homes</h3>
              <Button
                onClick={handleSaveConnections}
                disabled={!hasChanges || isSaving}
                className="bg-green-600 hover:bg-green-700"
              >
                {isSaving ? (
                  <>
                    <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                    Saving...
                  </>
                ) : (
                  <>
                    <Save className="mr-2 h-4 w-4" />
                    Save Changes
                  </>
                )}
              </Button>
            </div>

            {isLoading ? (
              <div className="flex justify-center py-8">
                <Loader2 className="h-8 w-8 animate-spin text-green-600" />
              </div>
            ) : (
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead className="w-[50px]"></TableHead>
                    <TableHead>Email</TableHead>
                    <TableHead>Address</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {allHomes.map((home) => (
                    <TableRow key={home.id}>
                      <TableCell>
                        <Checkbox
                          checked={connectedHomeIds.includes(home.id)}
                          onCheckedChange={() => handleHomeToggle(home.id)}
                          disabled={isSaving}
                        />
                      </TableCell>
                      <TableCell className="font-medium">{home.email}</TableCell>
                      <TableCell>{home.address}</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            )}

            <div className="text-sm text-muted-foreground">
              {hasChanges ? <p className="text-amber-600">You have unsaved changes</p> : <p>No changes to save</p>}
            </div>
          </>
        )}

        {!selectedPersonId && (
          <div className="py-8 text-center text-muted-foreground">
            Please select a person to manage their home connections
          </div>
        )}
      </CardContent>
    </Card>
  )
}

