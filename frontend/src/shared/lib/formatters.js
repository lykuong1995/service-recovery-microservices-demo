export function formatCurrency(value) {
  const numberValue =
    typeof value === "number" ? value : value == null ? 0 : Number(value);
  return new Intl.NumberFormat(undefined, {
    style: "currency",
    currency: "USD",
  }).format(Number.isFinite(numberValue) ? numberValue : 0);
}

