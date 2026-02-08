import api from "../../shared/lib/apiClient";

export async function listProducts() {
  const res = await api.get("/products");
  return res.data;
}

