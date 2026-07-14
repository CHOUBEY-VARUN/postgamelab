import { useLocation } from "react-router-dom";

type LoginLocationState = {
  message?: string;
};

function LoginPage() {
  const location = useLocation();
  const state = location.state as LoginLocationState | null;

  return (
    <main>
      <h1>Login</h1>

      {state?.message && (
        <p className="form-success" role="status">
          {state.message}
        </p>
      )}

      <p>Login form will be added during the authentication milestone.</p>
    </main>
  );
}

export default LoginPage;