import { Route, Routes } from 'react-router-dom';

import Navbar from './components/Navbar';
import CreateBreakdownPage from './pages/CreateBreakdownPage';
import DashboardPage from './pages/DashboardPage';
import LandingPage from './pages/LandingPage';
import LoginPage from './pages/LoginPage';
import NotFoundPage from './pages/NotFoundPage';
import PublicBreakdownPage from './pages/PublicBreakdownPage';
import RegisterPage from './pages/RegisterPage';
import BreakdownDetailPage from './pages/BreakdownDetailPage';

function App() {
  return (
    <>
      <Navbar />

      <Routes>
        <Route path="/" element={<LandingPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/dashboard" element={<DashboardPage />} />
        <Route path="/breakdowns/new" element={<CreateBreakdownPage />} />
        <Route path="/breakdowns/:id" element={<BreakdownDetailPage />} />
        <Route path="/public/breakdowns/:slug" element={<PublicBreakdownPage />} />
        <Route path="*" element={<NotFoundPage />} />
      </Routes>
    </>
  );
}

export default App;