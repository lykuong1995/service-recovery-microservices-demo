import api from "../../shared/lib/apiClient";

export async function listMyOrders() {
  const res = await api.get("/orders/my");
  return res.data;
}

