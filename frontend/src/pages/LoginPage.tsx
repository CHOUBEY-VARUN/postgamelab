import { useState } from "react";
import type { FormEvent } from "react";
import {
  Link,
  useLocation,
  useNavigate,
} from "react-router-dom";

import {
  AuthApiError,
  loginUser,
  saveAuthToken,
} from "../api/auth";

type LoginLocationState = {
  message?: string;
};

function LoginPage() {
  const location = useLocation();
  const navigate = useNavigate();
  const locationState = location.state as LoginLocationState | null;

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState("");
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const [showRegistrationMessage, setShowRegistrationMessage] = useState(
    Boolean(locationState?.message)
  );

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    if (isSubmitting) {
      return;
    }

    setError("");
    setFieldErrors({});
    setShowRegistrationMessage(false);
    setIsSubmitting(true);

    try {
      const response = await loginUser({
        email,
        password,
      });

      saveAuthToken(response.token);
      navigate("/dashboard", { replace: true });
    } catch (caughtError) {
      if (caughtError instanceof AuthApiError) {
        const nextFieldErrors: Record<string, string> = {};

        for (const fieldError of caughtError.fieldErrors) {
          if (!nextFieldErrors[fieldError.field]) {
            nextFieldErrors[fieldError.field] = fieldError.message;
          }
        }

        setFieldErrors(nextFieldErrors);
        setError(caughtError.message);
      } else {
        setError("Could not log in. Please try again.");
      }
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <main>
      <h1>Log in</h1>
      <p>Log in to access your PostGameLab dashboard.</p>

      {showRegistrationMessage && locationState?.message && (
        <p className="form-success" role="status">
          {locationState.message}
        </p>
      )}

      <form onSubmit={handleSubmit}>
        <div>
          <label htmlFor="email">Email address</label>
          <input
            id="email"
            name="email"
            type="email"
            autoComplete="email"
            value={email}
            onChange={(event) => setEmail(event.target.value)}
            required
            maxLength={255}
            aria-invalid={Boolean(fieldErrors.email)}
            aria-describedby={
              fieldErrors.email ? "email-error" : undefined
            }
          />
          {fieldErrors.email && (
            <p id="email-error" className="field-error">
              {fieldErrors.email}
            </p>
          )}
        </div>

        <div>
          <label htmlFor="password">Password</label>
          <input
            id="password"
            name="password"
            type="password"
            autoComplete="current-password"
            value={password}
            onChange={(event) => setPassword(event.target.value)}
            required
            minLength={8}
            maxLength={72}
            aria-invalid={Boolean(fieldErrors.password)}
            aria-describedby={
              fieldErrors.password ? "password-error" : undefined
            }
          />
          {fieldErrors.password && (
            <p id="password-error" className="field-error">
              {fieldErrors.password}
            </p>
          )}
        </div>

        {error && (
          <p className="form-error" role="alert">
            {error}
          </p>
        )}

        <button type="submit" disabled={isSubmitting}>
          {isSubmitting ? "Logging in..." : "Log in"}
        </button>
      </form>

      <p>
        Need an account? <Link to="/register">Create one</Link>
      </p>
    </main>
  );
}

export default LoginPage;