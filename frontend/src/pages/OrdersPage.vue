<template>
  <div class="stack">
    <UiCard title="My Orders">
      <template #actions>
        <UiButton :loading="loading" variant="secondary" @click="load">
          Refresh
        </UiButton>
      </template>

      <div class="stack">
        <UiNotice :message="notice.message" :tone="notice.tone" />

        <div v-if="!loading && orders.length === 0" class="muted">
          No orders yet.
        </div>

        <table v-if="orders.length > 0" class="table">
          <thead>
            <tr>
              <th>Order #</th>
              <th>Status</th>
              <th class="right">Total</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="o in orders" :key="o.id">
              <td>{{ o.orderNumber }}</td>
              <td>{{ o.status }}</td>
              <td class="right">{{ formatCurrency(o.totalAmount) }}</td>
            </tr>
          </tbody>
        </table>
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
import { listMyOrders } from "../features/orders/orderApi";

const orders = ref([]);
const loading = ref(false);
const notice = reactive({ message: "", tone: "info" });

function setNotice(message, tone = "info") {
  notice.message = message;
  notice.tone = tone;
}

async function load() {
  loading.value = true;
  setNotice("");
  try {
    orders.value = await listMyOrders();
  } catch (err) {
    setNotice("Failed to load orders.", "danger");
    orders.value = [];
  } finally {
    loading.value = false;
  }
}

onMounted(load);
</script>

<style scoped>
.muted {
  color: var(--muted);
}

.table {
  width: 100%;
  border-collapse: collapse;
  overflow: hidden;
  border-radius: var(--radius-sm);
  border: 1px solid var(--border);
}

th,
td {
  padding: 10px 12px;
  border-bottom: 1px solid var(--border);
}

th {
  text-align: left;
  font-size: 13px;
  color: var(--muted);
  background: rgba(0, 0, 0, 0.16);
}

tr:last-child td {
  border-bottom: none;
}

.right {
  text-align: right;
  font-variant-numeric: tabular-nums;
}
</style>

