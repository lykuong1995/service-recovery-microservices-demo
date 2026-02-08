import { computed, ref } from "vue";

function parseHash() {
  const raw = window.location.hash || "#/products";
  const hash = raw.startsWith("#") ? raw.slice(1) : raw;
  const [pathPart] = hash.split("?");
  const path = pathPart.startsWith("/") ? pathPart : `/${pathPart}`;
  return { path };
}

const routeRef = ref({ path: "/products" });

function syncFromLocation() {
  routeRef.value = parseHash();
}

export function navigate(path) {
  const normalized = path.startsWith("/") ? path : `/${path}`;
  window.location.hash = `#${normalized}`;
}

export function useHashRouter() {
  const route = computed(() => routeRef.value);
  return { route, navigate };
}

if (typeof window !== "undefined") {
  syncFromLocation();
  window.addEventListener("hashchange", syncFromLocation);
}
