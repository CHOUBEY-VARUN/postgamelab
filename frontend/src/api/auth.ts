const API_BASE_URL = (
  import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080"
).replace(/\/$/, "");

const AUTH_TOKEN_STORAGE_KEY = "postgamelab_token";

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

export type LoginUserPayload = {
  email: string;
  password: string;
};

export type AuthenticatedUser = {
  id: string;
  username: string;
  email: string;
};

export type LoginUserResponse = {
  token: string;
  user: AuthenticatedUser;
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

export class AuthApiError extends Error {
  readonly code: string;
  readonly fieldErrors: ApiFieldError[];

  constructor(
    message: string,
    code: string,
    fieldErrors: ApiFieldError[]
  ) {
    super(message);
    this.name = "AuthApiError";
    this.code = code;
    this.fieldErrors = fieldErrors;
  }
}

async function createApiError(
  response: Response,
  fallbackMessage: string,
  fallbackCode: string
): Promise<AuthApiError> {
  const contentType = response.headers.get("content-type");

  if (contentType?.includes("application/json")) {
    try {
      const errorResponse =
        await response.json() as Partial<ApiErrorResponse>;

      return new AuthApiError(
        typeof errorResponse.message === "string"
          ? errorResponse.message
          : fallbackMessage,
        typeof errorResponse.code === "string"
          ? errorResponse.code
          : fallbackCode,
        Array.isArray(errorResponse.fieldErrors)
          ? errorResponse.fieldErrors
          : []
      );
    } catch {
      return new AuthApiError(
        `${fallbackMessage} Status: ${response.status}`,
        fallbackCode,
        []
      );
    }
  }

  return new AuthApiError(
    `${fallbackMessage} Status: ${response.status}`,
    fallbackCode,
    []
  );
}

async function readJsonResponse<T>(
  response: Response,
  fallbackMessage: string,
  fallbackCode: string
): Promise<T> {
  if (!response.ok) {
    throw await createApiError(
      response,
      fallbackMessage,
      fallbackCode
    );
  }

  const contentType = response.headers.get("content-type");

  if (!contentType?.includes("application/json")) {
    throw new AuthApiError(
      "Backend returned a non-JSON response. Check the API URL or endpoint.",
      "INVALID_API_RESPONSE",
      []
    );
  }

  return response.json() as Promise<T>;
}

export function saveAuthToken(token: string): void {
  localStorage.setItem(AUTH_TOKEN_STORAGE_KEY, token);
}

export function getStoredAuthToken(): string | null {
  return localStorage.getItem(AUTH_TOKEN_STORAGE_KEY);
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

  return readJsonResponse<RegisteredUser>(
    response,
    "Registration failed.",
    "REGISTRATION_FAILED"
  );
}

export async function loginUser(
  payload: LoginUserPayload
): Promise<LoginUserResponse> {
  const response = await fetch(`${API_BASE_URL}/api/auth/login`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(payload),
  });

  return readJsonResponse<LoginUserResponse>(
    response,
    "Login failed.",
    "LOGIN_FAILED"
  );
}
