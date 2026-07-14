const API_BASE_URL = (
  import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080"
).replace(/\/$/, "");

export type RegisterUserPayload = {
  username: string;
  email: string;
  password: string;
};

export type RegisteredUser = {
  id: string;
  username: string;
  email: string;
  createdAt: string;
};

export type ApiFieldError = {
  field: string;
  message: string;
};

type ApiErrorResponse = {
  timestamp: string;
  status: number;
  code: string;
  message: string;
  path: string;
  fieldErrors: ApiFieldError[];
};

export class RegistrationApiError extends Error {
  readonly code: string;
  readonly fieldErrors: ApiFieldError[];

  constructor(
    message: string,
    code: string,
    fieldErrors: ApiFieldError[]
  ) {
    super(message);
    this.name = "RegistrationApiError";
    this.code = code;
    this.fieldErrors = fieldErrors;
  }
}

export async function registerUser(
  payload: RegisterUserPayload
): Promise<RegisteredUser> {
  const response = await fetch(`${API_BASE_URL}/api/auth/register`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(payload),
  });

  const contentType = response.headers.get("content-type");

  if (!response.ok) {
    if (contentType?.includes("application/json")) {
      const errorResponse =
        await response.json() as ApiErrorResponse;

      throw new RegistrationApiError(
        errorResponse.message,
        errorResponse.code,
        errorResponse.fieldErrors ?? []
      );
    }

    throw new RegistrationApiError(
      `Registration failed. Status: ${response.status}`,
      "REGISTRATION_FAILED",
      []
    );
  }

  if (!contentType?.includes("application/json")) {
    throw new RegistrationApiError(
      "Backend returned a non-JSON response. Check the API URL or endpoint.",
      "INVALID_API_RESPONSE",
      []
    );
  }

  return response.json() as Promise<RegisteredUser>;
}