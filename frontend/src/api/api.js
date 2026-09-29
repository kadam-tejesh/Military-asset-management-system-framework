import axios from "axios";

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || "http://localhost:8080/api",
});

api.interceptors.request.use((cfg) => {
  const token = localStorage.getItem("token");
  if (token) cfg.headers.Authorization = `Bearer ${token}`;
  return cfg;
});

// expired or invalid token: clear session and go to login
api.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err.response?.status === 401 && !err.config.url.includes("/auth/login")) {
      localStorage.clear();
      window.location.href = "/login";
    }
    return Promise.reject(err);
  }
);

export const errMsg = (e) => e.response?.data?.message || "Something went wrong";

/** drop empty-string / null / undefined params so they are not sent */
export const clean = (obj) =>
  Object.fromEntries(Object.entries(obj).filter(([, v]) => v !== "" && v != null));

/** "" -> undefined, otherwise Number */
export const num = (v) => (v === "" || v == null ? undefined : Number(v));

export const fmtDate = (d) => d || "—";
export const fmtDateTime = (d) => (d ? new Date(d).toLocaleString() : "—");

export default api;