<template>
  <div class="app-container stack">
    <AppHeader v-if="isAuthenticated" @logout="logout" />

    <main class="stack">
      <component :is="pageComponent" />
    </main>
  </div>
</template>

<script setup>
import { computed, watchEffect } from "vue";
import AppHeader from "./shared/components/AppHeader.vue";
import { useAuth } from "./features/auth/useAuth";
import { useHashRouter, navigate } from "./shared/lib/hashRouter";
import LoginPage from "./pages/LoginPage.vue";
import ProductsPage from "./pages/ProductsPage.vue";
import CartPage from "./pages/CartPage.vue";
import OrdersPage from "./pages/OrdersPage.vue";
import NotFoundPage from "./pages/NotFoundPage.vue";

const { isAuthenticated, logout } = useAuth();
const { route } = useHashRouter();

const pageComponent = computed(() => {
  if (!isAuthenticated.value) return LoginPage;

  switch (route.value.path) {
    case "/login":
      return LoginPage;
    case "/products":
      return ProductsPage;
    case "/cart":
      return CartPage;
    case "/orders":
      return OrdersPage;
    case "/":
      return ProductsPage;
    default:
      return NotFoundPage;
  }
});

watchEffect(() => {
  if (!isAuthenticated.value && route.value.path !== "/login") {
    navigate("/login");
  }
  if (isAuthenticated.value && route.value.path === "/login") {
    navigate("/products");
  }
});
</script>
