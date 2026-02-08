<template>
  <div class="stack">
    <UiCard title="Products">
      <template #actions>
        <UiButton :loading="loading" variant="secondary" @click="load">
          Refresh
        </UiButton>
      </template>

      <div class="stack">
        <UiNotice :message="notice.message" :tone="notice.tone" />

        <div v-if="!loading && products.length === 0" class="muted">
          No products found.
        </div>

        <div v-for="p in products" :key="p.id" class="product">
          <div class="meta">
            <div class="name">{{ p.name }}</div>
            <div class="desc">{{ p.description || "—" }}</div>
          </div>
          <div class="price">{{ formatCurrency(p.price) }}</div>
          <UiButton :disabled="addingId === p.id" @click="addToCart(p)">
            Add to cart
          </UiButton>
        </div>
      </div>
    </UiCard>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from "vue";
import UiButton from "../shared/components/ui/UiButton.vue";
import UiCard from "../shared/components/ui/UiCard.vue";
import UiNotice from "../shared/components/ui/UiNotice.vue";
import { formatCurrency } from "../shared/lib/formatters";
import { listProducts } from "../features/products/productApi";
import { addCartItem } from "../features/cart/cartApi";

const products = ref([]);
const loading = ref(false);
const addingId = ref(null);
const notice = reactive({ message: "", tone: "info" });

function setNotice(message, tone = "info") {
  notice.message = message;
  notice.tone = tone;
}

async function load() {
  loading.value = true;
  setNotice("");
  try {
    products.value = await listProducts();
  } catch (err) {
    setNotice("Failed to load products (check gateway route for /products).", "danger");
    products.value = [];
  } finally {
    loading.value = false;
  }
}

async function addToCart(product) {
  addingId.value = product.id;
  setNotice("");
  try {
    await addCartItem({
      productId: product.id,
      productName: product.name,
      price: product.price,
      quantity: 1,
    });
    setNotice("Added to cart.", "success");
  } catch (err) {
    setNotice("Failed to add to cart (check gateway route for /cart).", "danger");
  } finally {
    addingId.value = null;
  }
}

onMounted(load);
</script>

<style scoped>
.muted {
  color: var(--muted);
}

.product {
  display: grid;
  grid-template-columns: 1fr auto auto;
  gap: 12px;
  align-items: center;
  padding: 12px;
  border-radius: var(--radius-sm);
  border: 1px solid var(--border);
  background: rgba(0, 0, 0, 0.12);
}

.name {
  font-weight: 650;
}

.desc {
  color: var(--muted);
  font-size: 13px;
  margin-top: 2px;
}

.price {
  color: var(--text);
  font-variant-numeric: tabular-nums;
}

@media (max-width: 720px) {
  .product {
    grid-template-columns: 1fr;
    align-items: stretch;
  }
}
</style>

