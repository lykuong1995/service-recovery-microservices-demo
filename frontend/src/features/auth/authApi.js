import api from "../../shared/lib/apiClient";
import { setAccessToken, setRefreshToken } from "../../shared/lib/tokens";

export async function register(username, password) {
  await api.post("/auth/register", { username, password });
}

export async function login(username, password) {
  const response = await api.post("/auth/login", { username, password });
  const { accessToken, refreshToken } = response.data;
  setAccessToken(accessToken);
  setRefreshToken(refreshToken);
  return response.data;
}

