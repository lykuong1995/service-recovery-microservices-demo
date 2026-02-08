<template>
  <div class="stack">
    <UiCard title="Cart">
      <template #actions>
        <UiButton :loading="loading" variant="secondary" @click="load">
          Refresh
        </UiButton>
      </template>

      <div class="stack">
        <UiNotice :message="notice.message" :tone="notice.tone" />

        <div v-if="!loading && items.length === 0" class="muted">
          Cart is empty.
        </div>

        <div v-for="item in items" :key="item.id" class="item">
          <div class="meta">
            <div class="name">{{ item.productName }}</div>
            <div class="sub">
              Qty {{ item.quantity }} · {{ formatCurrency(item.price) }} each
            </div>
          </div>
          <div class="lineTotal">
            {{ formatCurrency(Number(item.price) * item.quantity) }}
          </div>
          <UiButton
            variant="danger"
            :disabled="removingId === item.id"
            @click="remove(item.id)"
          >
            Remove
          </UiButton>
        </div>

        <div class="totalRow" v-if="items.length > 0">
          <div class="spacer" />
          <div class="total">
            Total: <span class="amount">{{ formatCurrency(total) }}</span>
          </div>
          <UiButton :loading="checkingOut" @click="checkout">
            Checkout
          </UiButton>
        </div>
      </div>
    </UiCard>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from "vue";
import UiButton from "../shared/components/ui/UiButton.vue";
import UiCard from "../shared/components/ui/UiCard.vue";
import UiNotice from "../shared/components/ui/UiNotice.vue";
import { formatCurrency } from "../shared/lib/formatters";
import { checkoutCart, getCart, removeCartItem } from "../features/cart/cartApi";
import { navigate } from "../shared/lib/hashRouter";

const items = ref([]);
const loading = ref(false);
const removingId = ref(null);
const checkingOut = ref(false);
const notice = reactive({ message: "", tone: "info" });

const total = computed(() =>
  items.value.reduce(
    (sum, item) => sum + Number(item.price) * Number(item.quantity),
    0
  )
);

function setNotice(message, tone = "info") {
  notice.message = message;
  notice.tone = tone;
}

async function load() {
  loading.value = true;
  setNotice("");
  try {
    const cart = await getCart();
    items.value = cart.items ?? [];
  } catch (err) {
    setNotice("Failed to load cart (check gateway route for /cart).", "danger");
    items.value = [];
  } finally {
    loading.value = false;
  }
}

async function remove(id) {
  removingId.value = id;
  setNotice("");
  try {
    const cart = await removeCartItem(id);
    items.value = cart.items ?? [];
  } catch (err) {
    setNotice("Failed to remove item.", "danger");
  } finally {
    removingId.value = null;
  }
}

async function checkout() {
  if (items.value.length === 0) return;
  checkingOut.value = true;
  setNotice("");
  try {
    await checkoutCart();
    setNotice("Checkout started. Redirecting to orders…", "success");
    await load();
    setTimeout(() => navigate("/orders"), 600);
  } catch (err) {
    setNotice("Checkout failed.", "danger");
  } finally {
    checkingOut.value = false;
  }
}

onMounted(load);
</script>

<style scoped>
.muted {
  color: var(--muted);
}

.item {
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

.sub {
  color: var(--muted);
  font-size: 13px;
  margin-top: 2px;
}

.lineTotal {
  font-variant-numeric: tabular-nums;
}

.totalRow {
  display: flex;
  align-items: center;
  gap: 12px;
  padding-top: 6px;
}

.total {
  color: var(--muted);
}

.amount {
  color: var(--text);
  font-weight: 650;
  margin-left: 6px;
}

@media (max-width: 720px) {
  .item {
    grid-template-columns: 1fr;
    align-items: stretch;
  }
  .totalRow {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>

