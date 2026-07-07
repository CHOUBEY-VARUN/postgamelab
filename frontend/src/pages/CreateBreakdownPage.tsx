import { useState } from "react";
import type { FormEvent } from "react";
import { useNavigate } from "react-router-dom";
import { createBreakdown } from "../api/breakdowns";

function CreateBreakdownPage() {
  const navigate = useNavigate();

  const [title, setTitle] = useState("");
  const [homeTeam, setHomeTeam] = useState("");
  const [awayTeam, setAwayTeam] = useState("");
  const [gameDate, setGameDate] = useState("");
  const [videoUrl, setVideoUrl] = useState("");
  const [description, setDescription] = useState("");

  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState("");

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError("");
    setIsSubmitting(true);

    try {
      const breakdown = await createBreakdown({
        title,
        homeTeam,
        awayTeam,
        gameDate,
        videoUrl,
        description,
      });

      navigate(`/breakdowns/${breakdown.slug}`);
    } catch {
      setError("Could not create breakdown. Please check your details and try again.");
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <section>
      <h1>Create Breakdown</h1>
      <p>
        Start a new post-game breakdown by adding the game details and source
        video.
      </p>

      <form onSubmit={handleSubmit}>
        <div>
          <label htmlFor="title">Title</label>
          <input
            id="title"
            type="text"
            value={title}
            onChange={(event) => setTitle(event.target.value)}
            placeholder="Lakers vs Warriors Game 7 Breakdown"
            required
            maxLength={160}
          />
        </div>

        <div>
          <label htmlFor="homeTeam">Home Team</label>
          <input
            id="homeTeam"
            type="text"
            value={homeTeam}
            onChange={(event) => setHomeTeam(event.target.value)}
            placeholder="Lakers"
            required
            maxLength={80}
          />
        </div>

        <div>
          <label htmlFor="awayTeam">Away Team</label>
          <input
            id="awayTeam"
            type="text"
            value={awayTeam}
            onChange={(event) => setAwayTeam(event.target.value)}
            placeholder="Warriors"
            required
            maxLength={80}
          />
        </div>

        <div>
          <label htmlFor="gameDate">Game Date</label>
          <input
            id="gameDate"
            type="date"
            value={gameDate}
            onChange={(event) => setGameDate(event.target.value)}
            required
          />
        </div>

        <div>
          <label htmlFor="videoUrl">Video URL</label>
          <input
            id="videoUrl"
            type="url"
            value={videoUrl}
            onChange={(event) => setVideoUrl(event.target.value)}
            placeholder="https://www.youtube.com/watch?v=..."
          />
        </div>

        <div>
          <label htmlFor="description">Description</label>
          <textarea
            id="description"
            value={description}
            onChange={(event) => setDescription(event.target.value)}
            placeholder="What makes this game worth breaking down?"
            rows={5}
          />
        </div>

        {error && <p>{error}</p>}

        <button type="submit" disabled={isSubmitting}>
          {isSubmitting ? "Creating..." : "Create Breakdown"}
        </button>
      </form>
    </section>
  );
}

export default CreateBreakdownPage;