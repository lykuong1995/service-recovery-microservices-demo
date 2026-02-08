import { computed, ref } from "vue";
import { clearTokens, getAccessToken, tokenEpoch } from "../../shared/lib/tokens";
import { navigate } from "../../shared/lib/hashRouter";
import { decodeJwtPayload } from "../../shared/lib/jwt";

const authEpoch = ref(0);

export function useAuth() {
  const isAuthenticated = computed(() => {
    authEpoch.value;
    tokenEpoch.value;
    return Boolean(getAccessToken());
  });

  const role = computed(() => {
    authEpoch.value;
    tokenEpoch.value;
    const payload = decodeJwtPayload(getAccessToken());
    return payload?.role ?? null;
  });

  const isAdmin = computed(() => role.value === "ADMIN");

  function logout() {
    clearTokens();
    authEpoch.value += 1;
    navigate("/login");
  }

  function notifyAuthChanged() {
    authEpoch.value += 1;
  }

  return { isAuthenticated, role, isAdmin, logout, notifyAuthChanged };
}
