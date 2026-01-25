import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8082",
});

export function setToken(token) {
  api.defaults.headers.common["Authorization"] = `Bearer ${token}`;
}

export default api;
