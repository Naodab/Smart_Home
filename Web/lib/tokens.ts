"use server"

import { cookies } from "next/headers";
import API_BASE_URL from "./api-config";

const refreshAccessToken = async () => {
    const cookieStore = await cookies();
    const refreshToken = cookieStore.get("refresh_token");

    if (!refreshToken) {
        console.error("Refresh token is missing. Please log in again.");
        return null;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/api/token/refresh/`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({ refresh: refreshToken }),
        });

        if (!response.ok) {
            console.error("Failed to refresh access token.");
            return null;
        }

        const result = await response.json();
        cookieStore.set("access_token", result.access, {
            httpOnly: true,
            secure: process.env.NODE_ENV === "production",
            maxAge: 60 * 60,
            path: "/",
        });

        return result.access;
    } catch (error) {
        console.error("Error refreshing token:", error);
        return null;
    }
};

export default async function getAccessToken() {
    const cookieStore = await cookies()
    let accessToken = cookieStore.get("access_token");

    if (!accessToken) {
        accessToken = await refreshAccessToken();
        if (!accessToken) {
            console.error("User must log in again.")
            return null;
        }
    }

    return accessToken.value
};