import { useParams } from "react-router-dom";

function BreakdownDetailPage() {
  const { id } = useParams<{ id: string }>();

  return (
    <main>
      <h1>Breakdown Detail</h1>
      <p>Breakdown ID: {id}</p>
    </main>
  );
}

export default BreakdownDetailPage;