import { Link } from 'react-router-dom';

function Navbar() {
  return (
    <header>
      <nav>
        <Link to="/">PostGameLab</Link>

        <div>
          <Link to="/login">Login</Link>
          <Link to="/register">Register</Link>
          <Link to="/dashboard">Dashboard</Link>
          <Link to="/breakdowns/new">Create Breakdown</Link>
        </div>
      </nav>
    </header>
  );
}

export default Navbar;