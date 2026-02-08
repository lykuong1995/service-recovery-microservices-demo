import api from "../../shared/lib/apiClient";

export async function listRecoverableOrders() {
  const res = await api.get("/internal/recoverable");
  return res.data;
}

export async function retryOrder(id) {
  const res = await api.post(`/internal/retry/${id}`);
  return res.data;
}

