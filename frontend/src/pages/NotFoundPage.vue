<template>
  <UiCard title="Not Found">
    <div class="stack">
      <p class="muted">This page does not exist.</p>
      <UiButton @click="goHome">Go to Products</UiButton>
    </div>
  </UiCard>
</template>

<script setup>
import UiButton from "../shared/components/ui/UiButton.vue";
import UiCard from "../shared/components/ui/UiCard.vue";
import { navigate } from "../shared/lib/hashRouter";

function goHome() {
  navigate("/products");
}
</script>

<style scoped>
.muted {
  color: var(--muted);
  margin: 0;
}
</style>

