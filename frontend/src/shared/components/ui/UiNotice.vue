<template>
  <div v-if="message" :class="['notice', `notice--${tone}`]" role="status">
    {{ message }}
  </div>
</template>

<script setup>
defineProps({
  message: { type: String, default: "" },
  tone: { type: String, default: "info" }, // info | success | danger
});
</script>

<style scoped>
.notice {
  padding: 10px 12px;
  border-radius: var(--radius-sm);
  border: 1px solid var(--border);
  background: rgba(255, 255, 255, 0.06);
  color: var(--text);
}

.notice--success {
  border-color: rgba(62, 255, 155, 0.35);
  background: rgba(62, 255, 155, 0.1);
}

.notice--danger {
  border-color: rgba(255, 77, 109, 0.35);
  background: rgba(255, 77, 109, 0.12);
}
</style>

