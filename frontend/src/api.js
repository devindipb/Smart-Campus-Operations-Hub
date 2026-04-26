const API_BASE_URL = "http://localhost:8080";

function buildHeaders(username, password) {
  return {
    "Content-Type": "application/json",
    Authorization: `Basic ${btoa(`${username}:${password}`)}`
  } ;
}

export async function fetchResources(filters, credentials) {
  const params = new URLSearchParams();

  Object.entries(filters).forEach(([key, value]) => {
    if (value !== "" && value !== null && value !== undefined) {
      params.append(key, value);
    }
  });

  const response = await fetch(`${API_BASE_URL}/api/resources?${params.toString()}`, {
    headers: buildHeaders(credentials.username, credentials.password)
  });

  return handleResponse(response);
}

export async function createResource(payload, credentials) {
  const response = await fetch(`${API_BASE_URL}/api/resources`, {
    method: "POST",
    headers: buildHeaders(credentials.username, credentials.password),
    body: JSON.stringify(payload)
  });

  return handleResponse(response);
}

export async function updateResource(id, payload, credentials) {
  const response = await fetch(`${API_BASE_URL}/api/resources/${id}`, {
    method: "PUT",
    headers: buildHeaders(credentials.username, credentials.password),
    body: JSON.stringify(payload)
  });

  return handleResponse(response);
}

export async function deleteResource(id, credentials) {
  const response = await fetch(`${API_BASE_URL}/api/resources/${id}`, {
    method: "DELETE",
    headers: buildHeaders(credentials.username, credentials.password)
  });

  if (!response.ok) {
    await handleResponse(response);
  }
}

export async function fetchCurrentUser(credentials) {
  const response = await fetch(`${API_BASE_URL}/api/auth/me`, {
    headers: buildHeaders(credentials.username, credentials.password)
  });

  return handleResponse(response);
}

async function handleResponse(response) {
  if (response.ok) {
    if (response.status === 204) {
      return null;
    }
    return response.json();
  }

  if (response.status === 401) {
    throw new Error("Invalid username or password");
  }

  if (response.status === 403) {
    throw new Error("You do not have permission to perform this action");
  }

  const error = await response.json().catch(() => ({
    message: "Request failed"
  }));
  throw new Error(error.message || "Request failed");
}
