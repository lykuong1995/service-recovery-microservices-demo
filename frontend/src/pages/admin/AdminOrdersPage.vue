<template>
  <div class="stack">
    <UiCard title="Admin · Orders (Recoverable)">
      <template #actions>
        <div class="row">
          <UiButton variant="secondary" :loading="loading" @click="load">
            Refresh
          </UiButton>
        </div>
      </template>

      <div class="stack">
        <UiNotice :message="notice.message" :tone="notice.tone" />

        <div class="row filters">
          <UiInput v-model="filters.search" placeholder="Search order # / user" />
          <select v-model="filters.status" class="select">
            <option value="">All statuses</option>
            <option value="PROCESSING">PROCESSING</option>
            <option value="FAILED_TEMP">FAILED_TEMP</option>
            <option value="FAILED_FINAL">FAILED_FINAL</option>
            <option value="COMPLETED">COMPLETED</option>
          </select>
        </div>

        <div class="kpis">
          <div class="kpi">
            <div class="kpiLabel">Total</div>
            <div class="kpiValue">{{ orders.length }}</div>
          </div>
          <div class="kpi">
            <div class="kpiLabel">Processing</div>
            <div class="kpiValue">{{ countByStatus("PROCESSING") }}</div>
          </div>
          <div class="kpi">
            <div class="kpiLabel">Failed (Temp)</div>
            <div class="kpiValue">{{ countByStatus("FAILED_TEMP") }}</div>
          </div>
        </div>

        <div v-if="!loading && filtered.length === 0" class="muted">
          No recoverable orders.
        </div>

        <table v-if="filtered.length > 0" class="table">
          <thead>
            <tr>
              <th>Order #</th>
              <th>User</th>
              <th>Status</th>
              <th class="right">Total</th>
              <th class="right">Retries</th>
              <th>Updated</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="o in filtered" :key="o.id">
              <td class="mono">{{ o.orderNumber }}</td>
              <td>{{ o.username }}</td>
              <td>
                <span :class="['badge', `badge--${o.status}`]">{{ o.status }}</span>
              </td>
              <td class="right">{{ formatCurrency(o.totalAmount) }}</td>
              <td class="right mono">{{ o.retryCount }} / {{ o.maxRetry }}</td>
              <td class="mono">{{ formatTime(o.updatedAt) }}</td>
              <td class="right">
                <UiButton
                  variant="primary"
                  :disabled="retryingId === o.id"
                  @click="retry(o.id)"
                >
                  Retry
                </UiButton>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </UiCard>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from "vue";
import UiButton from "../../shared/components/ui/UiButton.vue";
import UiCard from "../../shared/components/ui/UiCard.vue";
import UiInput from "../../shared/components/ui/UiInput.vue";
import UiNotice from "../../shared/components/ui/UiNotice.vue";
import { formatCurrency } from "../../shared/lib/formatters";
import { listRecoverableOrders, retryOrder } from "../../features/admin/adminOrderApi";
import { useAuth } from "../../features/auth/useAuth";

const orders = ref([]);
const loading = ref(false);
const retryingId = ref(null);
const notice = reactive({ message: "", tone: "info" });
const filters = reactive({ search: "", status: "" });
const { role } = useAuth();

function setNotice(message, tone = "info") {
  notice.message = message;
  notice.tone = tone;
}

function formatTime(value) {
  if (!value) return "—";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return String(value);
  return date.toLocaleString();
}

function countByStatus(status) {
  return orders.value.filter((o) => o.status === status).length;
}

const filtered = computed(() => {
  const q = filters.search.trim().toLowerCase();
  return orders.value
    .filter((o) => (filters.status ? o.status === filters.status : true))
    .filter((o) => {
      if (!q) return true;
      return (
        String(o.orderNumber || "").toLowerCase().includes(q) ||
        String(o.username || "").toLowerCase().includes(q)
      );
    });
});

async function load() {
  loading.value = true;
  setNotice("");
  try {
    orders.value = await listRecoverableOrders();
  } catch (err) {
    const status = err?.response?.status;
    if (status === 401) {
      setNotice(
        "Unauthorized (401). Your token was not accepted by the Order service. Make sure you are calling through the Gateway and you restarted the Order service.",
        "danger"
      );
    } else if (status === 403) {
      setNotice(
        `Forbidden (403). Your role is "${role.value ?? "unknown"}". Restart the Order service so /internal/** allows ADMIN (latest code change).`,
        "danger"
      );
    } else if (!status) {
      setNotice(
        "Network error. Check VITE_API_URL and that the Gateway is running.",
        "danger"
      );
    } else {
      setNotice(`Failed to load admin orders (HTTP ${status}).`, "danger");
    }
    orders.value = [];
  } finally {
    loading.value = false;
  }
}

async function retry(id) {
  retryingId.value = id;
  setNotice("");
  try {
    await retryOrder(id);
    setNotice("Retry triggered.", "success");
    await load();
  } catch (err) {
    setNotice("Retry failed.", "danger");
  } finally {
    retryingId.value = null;
  }
}

onMounted(load);
</script>

<style scoped>
.muted {
  color: var(--muted);
}

.filters {
  flex-wrap: wrap;
}

.select {
  padding: 10px 12px;
  border-radius: var(--radius-sm);
  border: 1px solid var(--border);
  background: rgba(0, 0, 0, 0.18);
  color: var(--text);
  outline: none;
}

.kpis {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.kpi {
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  padding: 10px 12px;
  background: rgba(0, 0, 0, 0.12);
}

.kpiLabel {
  font-size: 12px;
  color: var(--muted);
}

.kpiValue {
  margin-top: 4px;
  font-size: 18px;
  font-weight: 700;
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
  vertical-align: middle;
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
}

.mono {
  font-variant-numeric: tabular-nums;
}

.badge {
  display: inline-flex;
  align-items: center;
  padding: 4px 8px;
  border-radius: 999px;
  border: 1px solid var(--border);
  background: rgba(255, 255, 255, 0.06);
  font-size: 12px;
}

.badge--PROCESSING {
  border-color: rgba(0, 229, 255, 0.35);
  background: rgba(0, 229, 255, 0.1);
}

.badge--FAILED_TEMP {
  border-color: rgba(255, 77, 109, 0.35);
  background: rgba(255, 77, 109, 0.12);
}

.badge--COMPLETED {
  border-color: rgba(62, 255, 155, 0.35);
  background: rgba(62, 255, 155, 0.1);
}

@media (max-width: 900px) {
  .kpis {
    grid-template-columns: 1fr;
  }
  .table {
    display: block;
    overflow-x: auto;
    white-space: nowrap;
  }
}
</style>
