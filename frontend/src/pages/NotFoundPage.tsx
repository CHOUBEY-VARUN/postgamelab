import { Link } from 'react-router-dom';

function NotFoundPage() {
  return (
    <main>
      <h1>Page not found</h1>
      <p>This page does not exist or may have been moved.</p>
      <Link to="/">Go home</Link>
    </main>
  );
}

export default NotFoundPage;