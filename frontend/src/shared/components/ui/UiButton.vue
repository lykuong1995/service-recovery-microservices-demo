<template>
  <button :class="classes" :type="type" :disabled="disabled || loading">
    <span v-if="loading" class="spinner" aria-hidden="true"></span>
    <slot />
  </button>
</template>

<script setup>
import { computed } from "vue";

const props = defineProps({
  variant: { type: String, default: "primary" },
  type: { type: String, default: "button" },
  disabled: { type: Boolean, default: false },
  loading: { type: Boolean, default: false },
});

const classes = computed(() => [
  "btn",
  `btn--${props.variant}`,
  props.loading ? "btn--loading" : "",
]);
</script>

<style scoped>
.btn {
  position: relative;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  padding: 10px 14px;
  border-radius: var(--radius-sm);
  border: 1px solid var(--border);
  color: var(--text);
  background: rgba(255, 255, 255, 0.06);
  cursor: pointer;
  transition: transform 120ms ease, background 120ms ease, border-color 120ms ease;
  user-select: none;
}

.btn:hover:not(:disabled) {
  transform: translateY(-1px);
  border-color: rgba(255, 255, 255, 0.22);
  background: rgba(255, 255, 255, 0.09);
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn--primary {
  border-color: rgba(124, 92, 255, 0.45);
  background: linear-gradient(
    180deg,
    rgba(124, 92, 255, 0.28),
    rgba(124, 92, 255, 0.14)
  );
}

.btn--secondary {
  background: rgba(255, 255, 255, 0.06);
}

.btn--danger {
  border-color: rgba(255, 77, 109, 0.45);
  background: linear-gradient(
    180deg,
    rgba(255, 77, 109, 0.22),
    rgba(255, 77, 109, 0.12)
  );
}

.spinner {
  width: 14px;
  height: 14px;
  border-radius: 999px;
  border: 2px solid rgba(255, 255, 255, 0.45);
  border-top-color: transparent;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
</style>
