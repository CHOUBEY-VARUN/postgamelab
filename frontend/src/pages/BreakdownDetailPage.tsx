import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { getBreakdownById } from "../api/breakdowns";
import type { Breakdown } from "../api/breakdowns";

export default function BreakdownDetailPage() {
  const { id } = useParams<{ id: string }>();

  const [breakdown, setBreakdown] = useState<Breakdown | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    async function loadBreakdown() {
      if (!id) {
        setError("Missing breakdown ID.");
        setIsLoading(false);
        return;
      }

      try {
        setIsLoading(true);
        setError("");

        const data = await getBreakdownById(id);
        setBreakdown(data);
      } catch (err) {
        setError(err instanceof Error ? err.message : "Something went wrong.");
      } finally {
        setIsLoading(false);
      }
    }

    loadBreakdown();
  }, [id]);

  if (isLoading) {
    return (
      <main>
        <p>Loading breakdown...</p>
      </main>
    );
  }

  if (error || !breakdown) {
    return (
      <main>
        <h1>Breakdown not found</h1>
        <p>{error || "This breakdown could not be loaded."}</p>
        <Link to="/dashboard">Back to Dashboard</Link>
      </main>
    );
  }

  return (
    <main>
      <Link to="/dashboard">Back to Dashboard</Link>

      <section>
        <h1>{breakdown.title}</h1>

        <p>
          {breakdown.homeTeam} vs {breakdown.awayTeam}
        </p>

        <p>Game date: {breakdown.gameDate}</p>

        {breakdown.videoUrl && (
          <p>
            Video:{" "}
            <a href={breakdown.videoUrl} target="_blank" rel="noreferrer">
              Open video
            </a>
          </p>
        )}

        {breakdown.description && (
          <section>
            <h2>Description</h2>
            <p>{breakdown.description}</p>
          </section>
        )}

        <section>
          <h2>Metadata</h2>

          {breakdown.createdAt && (
            <p>Created: {new Date(breakdown.createdAt).toLocaleString()}</p>
          )}

          {breakdown.updatedAt && (
            <p>Updated: {new Date(breakdown.updatedAt).toLocaleString()}</p>
          )}
        </section>
      </section>
    </main>
  );
}
