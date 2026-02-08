import api from "../../shared/lib/apiClient";

export async function getCart() {
  const res = await api.get("/cart");
  return res.data;
}

export async function addCartItem(item) {
  const res = await api.post("/cart/items", item);
  return res.data;
}

export async function removeCartItem(id) {
  const res = await api.delete(`/cart/items/${id}`);
  return res.data;
}

export async function checkoutCart() {
  const res = await api.post("/cart/checkout");
  return res.data;
}

