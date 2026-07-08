import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { getMyBreakdowns } from "../api/breakdowns";
import type { Breakdown } from "../api/breakdowns";

function DashboardPage() {
  const [breakdowns, setBreakdowns] = useState<Breakdown[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    async function loadBreakdowns() {
      try {
        const data = await getMyBreakdowns();
        setBreakdowns(data);
      } catch {
        setError("Could not load your breakdowns. Please try again.");
      } finally {
        setIsLoading(false);
      }
    }

    loadBreakdowns();
  }, []);

  if (isLoading) {
    return <main><p>Loading your breakdowns...</p></main>;
  }

  return (
    <main>
      <h1>Dashboard</h1>
      <Link to="/breakdowns/new">Create new breakdown</Link>

      {error && <p>{error}</p>}

      {!error && breakdowns.length === 0 && (
        <p>You have not created any breakdowns yet.</p>
      )}

      {!error && breakdowns.map((breakdown) => (
        <article key={breakdown.id}>
          <Link to={`/breakdowns/${breakdown.id}`}>
            <h2>{breakdown.title}</h2>
            <p>{breakdown.awayTeam} at {breakdown.homeTeam}</p>
            <p>Game date: {breakdown.gameDate}</p>
            {breakdown.createdAt && <p>Created: {breakdown.createdAt}</p>}
          </Link>
        </article>
      ))}
    </main>
  );
}

export default DashboardPage;