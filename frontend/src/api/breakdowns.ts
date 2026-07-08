const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "";
const AUTH_TOKEN_STORAGE_KEY = "postgamelab_token";

function getAuthHeaders() {
  const token = localStorage.getItem(AUTH_TOKEN_STORAGE_KEY);
  const headers = new Headers();

  if (token) {
    headers.set("Authorization", `Bearer ${token}`);
  }

  return headers;
}

export async function getMyBreakdowns(): Promise<Breakdown[]> {
  const response = await fetch(`${API_BASE_URL}/api/breakdowns`, {
    headers: getAuthHeaders(),
  });

  if (!response.ok) {
    throw new Error("Failed to load breakdowns");
  }

  return response.json();
}

export type BreakdownVisibility = "PRIVATE" | "PUBLIC";

export type Breakdown = {
  id: string;
  title: string;
  slug: string;
  homeTeam: string;
  awayTeam: string;
  gameDate: string;
  videoUrl: string | null;
  description: string | null;
  visibility: BreakdownVisibility;
  createdAt: string;
  updatedAt: string;
};

export type CreateBreakdownPayload = {
  title: string;
  homeTeam: string;
  awayTeam: string;
  gameDate: string;
  videoUrl?: string;
  description?: string;
};

export async function createBreakdown(
  payload: CreateBreakdownPayload
): Promise<Breakdown> {
  const headers = getAuthHeaders();
  headers.set("Content-Type", "application/json");

  const response = await fetch(`${API_BASE_URL}/api/breakdowns`, {
    method: "POST",
    headers,
    body: JSON.stringify(payload),
  });

  if (!response.ok) {
    throw new Error("Failed to create breakdown");
  }

  return response.json();
}

export async function getPublicBreakdownBySlug(
  slug: string
): Promise<Breakdown> {
  const response = await fetch(`${API_BASE_URL}/api/breakdowns/public/${slug}`);

  if (!response.ok) {
    throw new Error("Failed to load breakdown");
  }

  return response.json();
}
