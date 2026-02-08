<template>
  <header class="header">
    <div class="brand">
      <div class="logo" aria-hidden="true"></div>
      <div class="title">
        <div class="name">Order Portal</div>
        <div class="subtitle">Gateway-backed microservices</div>
      </div>
    </div>

    <nav class="nav" aria-label="Primary">
      <AppNavLink to="/products">Products</AppNavLink>
      <AppNavLink to="/cart">Cart</AppNavLink>
      <AppNavLink to="/orders">Orders</AppNavLink>
    </nav>

    <div class="actions">
      <UiButton variant="danger" @click="$emit('logout')">Logout</UiButton>
    </div>
  </header>
</template>

<script setup>
import AppNavLink from "./AppNavLink.vue";
import UiButton from "./ui/UiButton.vue";

defineEmits(["logout"]);
</script>

<style scoped>
.header {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px 16px;
  border: 1px solid var(--border);
  border-radius: var(--radius);
  background: linear-gradient(
    180deg,
    rgba(255, 255, 255, 0.08),
    rgba(255, 255, 255, 0.03)
  );
  box-shadow: var(--shadow);
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 260px;
}

.logo {
  width: 36px;
  height: 36px;
  border-radius: 12px;
  background: radial-gradient(circle at 30% 20%, #7c5cff, #00e5ff);
  box-shadow: 0 12px 30px rgba(124, 92, 255, 0.25);
}

.title .name {
  font-weight: 650;
  letter-spacing: 0.2px;
}

.title .subtitle {
  font-size: 12px;
  color: var(--muted);
}

.nav {
  display: flex;
  gap: 8px;
}

.actions {
  margin-left: auto;
}

@media (max-width: 820px) {
  .header {
    flex-wrap: wrap;
  }
  .actions {
    margin-left: 0;
  }
  .brand {
    min-width: unset;
    flex: 1;
  }
}
</style>

