import { NextResponse } from "next/server"
import type { NextRequest } from "next/server"

// This function can be marked `async` if using `await` inside
export function middleware(request: NextRequest) {
  // Get the pathname of the request
  const path = request.nextUrl.pathname

  // Define public paths that don't require authentication
  const isPublicPath = path === "/login" || path === "/"

  // Get the access token from the cookies
  const accessToken = request.cookies.get("auth_access_token")?.value || ""

  // If the path requires authentication and there's no token, redirect to login
  if (!isPublicPath && !accessToken) {
    return NextResponse.redirect(new URL("/login", request.url))
  }

  // If the user is logged in and tries to access login page, redirect to dashboard
  if (isPublicPath && accessToken) {
    return NextResponse.redirect(new URL("/dashboard", request.url))
  }

  return NextResponse.next()
}

// See "Matching Paths" below to learn more
export const config = {
  matcher: [
    /*
     * Match all request paths except for the ones starting with:
     * - api (API routes)
     * - _next/static (static files)
     * - _next/image (image optimization files)
     * - favicon.ico (favicon file)
     */
    "/((?!api|_next/static|_next/image|favicon.ico).*)",
  ],
}

