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
  const response = await fetch("/api/breakdowns", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
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
  const response = await fetch(`/api/breakdowns/public/${slug}`);

  if (!response.ok) {
    throw new Error("Failed to load breakdown");
  }

  return response.json();
}