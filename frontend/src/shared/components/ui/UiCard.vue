<template>
  <section class="card">
    <header v-if="title" class="header">
      <h2 class="title">{{ title }}</h2>
      <div class="actions">
        <slot name="actions" />
      </div>
    </header>
    <div class="body">
      <slot />
    </div>
  </section>
</template>

<script setup>
defineProps({
  title: { type: String, default: "" },
});
</script>

<style scoped>
.card {
  border: 1px solid var(--border);
  background: linear-gradient(
    180deg,
    rgba(255, 255, 255, 0.06),
    rgba(255, 255, 255, 0.03)
  );
  border-radius: var(--radius);
  box-shadow: var(--shadow);
  overflow: hidden;
}

.header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  border-bottom: 1px solid var(--border);
  background: rgba(0, 0, 0, 0.08);
}

.title {
  font-size: 15px;
  margin: 0;
  letter-spacing: 0.2px;
}

.actions {
  margin-left: auto;
}

.body {
  padding: 16px;
}
</style>

