"use client"

import { Checkbox } from "@/components/ui/checkbox"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"

interface PersonHomeFormProps {
  allPersons: { id: string; name: string }[]
  selectedPersons: string[]
  setSelectedPersons: (ids: string[]) => void
}

export function PersonHomeForm({ allPersons, selectedPersons, setSelectedPersons }: PersonHomeFormProps) {
  const handleTogglePerson = (personId: string) => {
    if (selectedPersons.includes(personId)) {
      setSelectedPersons(selectedPersons.filter((id) => id !== personId))
    } else {
      setSelectedPersons([...selectedPersons, personId])
    }
  }

  return (
    <div className="max-h-[300px] overflow-auto">
      <Table>
        <TableHeader>
          <TableRow>
            <TableHead className="w-[50px]"></TableHead>
            <TableHead>ID</TableHead>
            <TableHead>Name</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {allPersons.length > 0 ? (
            allPersons.map((person) => (
              <TableRow key={person.id}>
                <TableCell>
                  <Checkbox
                    checked={selectedPersons.includes(person.id)}
                    onCheckedChange={() => handleTogglePerson(person.id)}
                  />
                </TableCell>
                <TableCell>{person.id}</TableCell>
                <TableCell>{person.name}</TableCell>
              </TableRow>
            ))
          ) : (
            <TableRow>
              <TableCell colSpan={3} className="text-center py-4 text-muted-foreground">
                No persons available to add
              </TableCell>
            </TableRow>
          )}
        </TableBody>
      </Table>
    </div>
  )
}

