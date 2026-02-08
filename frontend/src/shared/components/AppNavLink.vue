<template>
  <a :href="href" :class="['link', active ? 'link--active' : '']">
    <slot />
  </a>
</template>

<script setup>
import { computed } from "vue";
import { useHashRouter } from "../lib/hashRouter";

const props = defineProps({
  to: { type: String, required: true },
});

const { route } = useHashRouter();

const href = computed(() => `#${props.to.startsWith("/") ? props.to : `/${props.to}`}`);
const active = computed(() => route.value.path === (props.to.startsWith("/") ? props.to : `/${props.to}`));
</script>

<style scoped>
.link {
  display: inline-flex;
  align-items: center;
  padding: 8px 10px;
  border-radius: 10px;
  color: var(--muted);
  border: 1px solid transparent;
}

.link:hover {
  color: var(--text);
  background: rgba(255, 255, 255, 0.06);
  border-color: rgba(255, 255, 255, 0.08);
}

.link--active {
  color: var(--text);
  background: rgba(124, 92, 255, 0.14);
  border-color: rgba(124, 92, 255, 0.35);
}
</style>

