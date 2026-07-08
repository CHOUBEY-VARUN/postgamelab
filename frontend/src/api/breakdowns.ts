const API_BASE_URL = (
  import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080"
).replace(/\/$/, "");
const AUTH_TOKEN_STORAGE_KEY = "postgamelab_token";

function getApiUrl(path: string) {
  return `${API_BASE_URL}${path}`;
}

function getAuthHeaders() {
  const token = localStorage.getItem(AUTH_TOKEN_STORAGE_KEY);
  const headers = new Headers();

  if (token) {
    headers.set("Authorization", `Bearer ${token}`);
  }

  return headers;
}

async function readJsonResponse<T>(
  response: Response,
  errorMessage: string
): Promise<T> {
  const contentType = response.headers.get("content-type");

  if (!response.ok) {
    throw new Error(`${errorMessage} Status: ${response.status}`);
  }

  if (!contentType?.includes("application/json")) {
    throw new Error(
      "Backend returned a non-JSON response. Check the API URL or endpoint."
    );
  }

  return response.json();
}

export async function getMyBreakdowns(): Promise<Breakdown[]> {
  const response = await fetch(getApiUrl("/api/breakdowns"), {
    headers: getAuthHeaders(),
  });

  return readJsonResponse<Breakdown[]>(response, "Failed to load breakdowns.");
}

export async function getBreakdownById(id: string): Promise<Breakdown> {
  const response = await fetch(getApiUrl(`/api/breakdowns/${id}`), {
    headers: getAuthHeaders(),
  });

  return readJsonResponse<Breakdown>(response, "Failed to load breakdown.");
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

  const response = await fetch(getApiUrl("/api/breakdowns"), {
    method: "POST",
    headers,
    body: JSON.stringify(payload),
  });

  return readJsonResponse<Breakdown>(response, "Failed to create breakdown.");
}

export async function getPublicBreakdownBySlug(
  slug: string
): Promise<Breakdown> {
  const response = await fetch(getApiUrl(`/api/breakdowns/public/${slug}`));

  return readJsonResponse<Breakdown>(response, "Failed to load breakdown.");
}
