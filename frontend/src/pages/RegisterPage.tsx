import { useState } from "react";
import type { FormEvent } from "react";
import { Link, useNavigate } from "react-router-dom";

import {
  registerUser,
  RegistrationApiError,
} from "../api/auth";

function RegisterPage() {
  const navigate = useNavigate();

  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");

  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState("");
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    if (isSubmitting) {
      return;
    }

    setError("");
    setFieldErrors({});

    if (password !== confirmPassword) {
      setFieldErrors({
        confirmPassword: "Passwords do not match.",
      });
      return;
    }

    setIsSubmitting(true);

    try {
      await registerUser({
        username,
        email,
        password,
      });

      navigate("/login", {
        replace: true,
        state: {
          message: "Registration successful. You can now log in.",
        },
      });
    } catch (caughtError) {
      if (caughtError instanceof RegistrationApiError) {
        const nextFieldErrors: Record<string, string> = {};

        for (const fieldError of caughtError.fieldErrors) {
          if (!nextFieldErrors[fieldError.field]) {
            nextFieldErrors[fieldError.field] = fieldError.message;
          }
        }

        setFieldErrors(nextFieldErrors);
        setError(caughtError.message);
      } else {
        setError(
          "Could not create your account. Please try again."
        );
      }
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <main>
      <h1>Create your account</h1>
      <p>
        Register to start creating PostGameLab breakdowns.
      </p>

      <form onSubmit={handleSubmit}>
        <div>
          <label htmlFor="username">Username</label>
          <input
            id="username"
            name="username"
            type="text"
            autoComplete="username"
            value={username}
            onChange={(event) => setUsername(event.target.value)}
            required
            minLength={3}
            maxLength={40}
            pattern="[A-Za-z0-9_]+"
            aria-invalid={Boolean(fieldErrors.username)}
            aria-describedby={
              fieldErrors.username ? "username-error" : undefined
            }
          />
          {fieldErrors.username && (
            <p id="username-error" className="field-error">
              {fieldErrors.username}
            </p>
          )}
        </div>

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
            autoComplete="new-password"
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

        <div>
          <label htmlFor="confirmPassword">Confirm password</label>
          <input
            id="confirmPassword"
            name="confirmPassword"
            type="password"
            autoComplete="new-password"
            value={confirmPassword}
            onChange={(event) => setConfirmPassword(event.target.value)}
            required
            minLength={8}
            maxLength={72}
            aria-invalid={Boolean(fieldErrors.confirmPassword)}
            aria-describedby={
              fieldErrors.confirmPassword
                ? "confirm-password-error"
                : undefined
            }
          />
          {fieldErrors.confirmPassword && (
            <p id="confirm-password-error" className="field-error">
              {fieldErrors.confirmPassword}
            </p>
          )}
        </div>

        {error && (
          <p className="form-error" role="alert">
            {error}
          </p>
        )}

        <button type="submit" disabled={isSubmitting}>
          {isSubmitting ? "Creating account..." : "Create account"}
        </button>
      </form>

      <p>
        Already have an account? <Link to="/login">Log in</Link>
      </p>
    </main>
  );
}

export default RegisterPage;