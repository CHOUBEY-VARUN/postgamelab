import { useParams } from 'react-router-dom';

function PublicBreakdownPage() {
  const { slug } = useParams();

  return (
    <main>
      <h1>Public Breakdown</h1>
      <p>Breakdown slug: {slug}</p>
    </main>
  );
}

export default PublicBreakdownPage;