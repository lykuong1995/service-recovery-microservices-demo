<template>
  <div class="page">
    <div class="hero">
      <h1 class="h1">Customer Order Portal</h1>
      <p class="sub">
        Login to browse products, manage your cart, and place orders through the
        gateway.
      </p>
    </div>

    <UiCard :title="modeTitle">
      <div class="stack">
        <UiNotice :message="notice.message" :tone="notice.tone" />

        <UiInput
          label="Username"
          v-model="form.username"
          autocomplete="username"
          placeholder="Enter username"
        />
        <UiInput
          label="Password"
          v-model="form.password"
          type="password"
          autocomplete="current-password"
          placeholder="Enter password"
        />

        <div class="row">
          <UiButton :loading="loading" @click="submit">{{ modeTitle }}</UiButton>
          <UiButton variant="secondary" :disabled="loading" @click="toggleMode">
            {{ toggleText }}
          </UiButton>
          <div class="spacer" />
        </div>
      </div>
    </UiCard>
  </div>
</template>

<script setup>
import { computed, reactive, ref } from "vue";
import UiButton from "../shared/components/ui/UiButton.vue";
import UiCard from "../shared/components/ui/UiCard.vue";
import UiInput from "../shared/components/ui/UiInput.vue";
import UiNotice from "../shared/components/ui/UiNotice.vue";
import { login, register } from "../features/auth/authApi";
import { navigate } from "../shared/lib/hashRouter";
import { useAuth } from "../features/auth/useAuth";

const { notifyAuthChanged } = useAuth();

const isRegister = ref(false);
const loading = ref(false);
const form = reactive({ username: "", password: "" });
const notice = reactive({ message: "", tone: "info" });

const modeTitle = computed(() => (isRegister.value ? "Register" : "Login"));
const toggleText = computed(() =>
  isRegister.value ? "Switch to Login" : "Switch to Register"
);

function toggleMode() {
  isRegister.value = !isRegister.value;
  form.username = "";
  form.password = "";
  notice.message = "";
}

function setNotice(message, tone = "info") {
  notice.message = message;
  notice.tone = tone;
}

async function submit() {
  if (!form.username || !form.password) {
    setNotice("Username and password are required.", "danger");
    return;
  }

  loading.value = true;
  setNotice("");

  try {
    if (isRegister.value) {
      await register(form.username, form.password);
      setNotice("Registration successful. Please login.", "success");
      isRegister.value = false;
      form.password = "";
      return;
    }

    await login(form.username, form.password);
    notifyAuthChanged();
    navigate("/products");
  } catch (err) {
    const status = err?.response?.status;
    if (!status) {
      setNotice(
        "Network error. Check VITE_API_URL and that the Gateway is running.",
        "danger"
      );
    } else if (status === 401) {
      setNotice("Invalid username/password (401).", "danger");
    } else {
      setNotice(`Login failed (HTTP ${status}).`, "danger");
    }
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.page {
  max-width: 560px;
  margin: 0 auto;
  padding-top: 18px;
}

.hero {
  margin-bottom: 14px;
}

.h1 {
  margin: 0;
  font-size: 28px;
  letter-spacing: 0.2px;
}

.sub {
  margin: 8px 0 0 0;
  color: var(--muted);
}
</style>
