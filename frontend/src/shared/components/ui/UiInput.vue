<template>
  <label class="field">
    <span v-if="label" class="label">{{ label }}</span>
    <input
      class="input"
      :type="type"
      :placeholder="placeholder"
      :value="modelValue"
      :autocomplete="autocomplete"
      @input="$emit('update:modelValue', $event.target.value)"
    />
  </label>
</template>

<script setup>
defineProps({
  label: { type: String, default: "" },
  type: { type: String, default: "text" },
  placeholder: { type: String, default: "" },
  modelValue: { type: [String, Number], default: "" },
  autocomplete: { type: String, default: "off" },
});

defineEmits(["update:modelValue"]);
</script>

<style scoped>
.field {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.label {
  font-size: 13px;
  color: var(--muted);
}

.input {
  padding: 10px 12px;
  border-radius: var(--radius-sm);
  border: 1px solid var(--border);
  background: rgba(0, 0, 0, 0.18);
  color: var(--text);
  outline: none;
}

.input:focus {
  border-color: rgba(124, 92, 255, 0.65);
  box-shadow: 0 0 0 4px rgba(124, 92, 255, 0.18);
}
</style>

