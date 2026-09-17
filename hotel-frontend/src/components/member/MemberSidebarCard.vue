<template>
  <div class="member-sidebar-section">
    <div class="avatar-box">
      <div class="avatar-large-circle">
        {{ userInitial }}
      </div>
    </div>
    <div class="sidebar-user-name">{{ displayName }}</div>
    <div class="sidebar-tier-tag">{{ tierText }}</div>
    <slot></slot>
  </div>
</template>

<script setup>
import { computed } from "vue";

const props = defineProps({
  name: {
    type: String,
    default: "",
  },
  tierText: {
    type: String,
    default: "星澄貴賓會員",
  },
});

const displayName = computed(() => {
  return props.name || "星澄貴賓";
});

const userInitial = computed(() => {
  if (props.name && props.name.trim().length > 0) {
    return props.name.trim().charAt(0);
  }
  return "客";
});
</script>

