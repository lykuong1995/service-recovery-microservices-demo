import { computed, ref } from "vue";
import { clearTokens, getAccessToken, tokenEpoch } from "../../shared/lib/tokens";
import { navigate } from "../../shared/lib/hashRouter";

const authEpoch = ref(0);

export function useAuth() {
  const isAuthenticated = computed(() => {
    authEpoch.value;
    tokenEpoch.value;
    return Boolean(getAccessToken());
  });

  function logout() {
    clearTokens();
    authEpoch.value += 1;
    navigate("/login");
  }

  function notifyAuthChanged() {
    authEpoch.value += 1;
  }

  return { isAuthenticated, logout, notifyAuthChanged };
}
