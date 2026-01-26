import axios from "axios";

const api = axios.create({
  baseURL: import.meta.env.VITE_ORDER_URL,
});

api.interceptors.request.use(config => {
  const token = localStorage.getItem("accessToken");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  response => response,
  async error => {
    if (error.response && error.response.status === 401) {
      const refreshToken = localStorage.getItem("refreshToken");

      if (!refreshToken) {
        return Promise.reject(error);
      }

      try {
        const res = await axios.post(
          `${import.meta.env.VITE_AUTH_URL}/auth/refresh`,
          { refreshToken }
        );

        const newAccessToken = res.data.accessToken;
        localStorage.setItem("accessToken", newAccessToken);

        error.config.headers.Authorization = `Bearer ${newAccessToken}`;
        return axios(error.config);

      } catch (refreshError) {
        localStorage.clear();
        window.location.reload();
      }
    }

    return Promise.reject(error);
  }
);

export default api;