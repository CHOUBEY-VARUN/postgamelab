import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { getPublicBreakdownBySlug } from "../api/breakdowns";
import type { Breakdown } from "../api/breakdowns";

function PublicBreakdownPage() {
  const { slug } = useParams<{ slug: string }>();

  const [breakdown, setBreakdown] = useState<Breakdown | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    async function loadBreakdown() {
      if (!slug) {
        setError("Missing breakdown slug.");
        setIsLoading(false);
        return;
      }

      try {
        const data = await getPublicBreakdownBySlug(slug);
        setBreakdown(data);
      } catch {
        setError("Could not load this breakdown.");
      } finally {
        setIsLoading(false);
      }
    }

    loadBreakdown();
  }, [slug]);

  if (isLoading) {
    return (
      <section>
        <p>Loading breakdown...</p>
      </section>
    );
  }

  if (error || !breakdown) {
    return (
      <section>
        <h1>Breakdown not found</h1>
        <p>{error}</p>
      </section>
    );
  }

  return (
    <section>
      <p>{breakdown.visibility}</p>

      <h1>{breakdown.title}</h1>

      <p>
        {breakdown.awayTeam} at {breakdown.homeTeam}
      </p>

      <p>Game date: {breakdown.gameDate}</p>

      {breakdown.videoUrl && (
        <p>
          Video:{" "}
          <a href={breakdown.videoUrl} target="_blank" rel="noreferrer">
            {breakdown.videoUrl}
          </a>
        </p>
      )}

      {breakdown.description && <p>{breakdown.description}</p>}

      <hr />

      <h2>Key Moments</h2>
      <p>Timestamped moments will be added here next.</p>
    </section>
  );
}

export default PublicBreakdownPage;