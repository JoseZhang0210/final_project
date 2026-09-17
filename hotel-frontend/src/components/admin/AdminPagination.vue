<template>
  <div v-if="!loading && totalCount > 0" class="pagination-area">
    <div class="pagination-info">
      第
      <strong>{{ currentPage }}</strong>
      頁 ／ 共
      <strong>{{ totalPages }}</strong>
      頁
    </div>

    <div class="pagination">
      <button
        type="button"
        class="page-button"
        :disabled="currentPage === 1"
        @click="$emit('page-change', 1)"
      >
        «
      </button>

      <button
        type="button"
        class="page-button"
        :disabled="currentPage === 1"
        @click="$emit('page-change', currentPage - 1)"
      >
        ‹
      </button>

      <button
        v-for="page in visiblePages"
        :key="page"
        type="button"
        class="page-button"
        :class="{ active: currentPage === page }"
        @click="$emit('page-change', page)"
      >
        {{ page }}
      </button>

      <button
        type="button"
        class="page-button"
        :disabled="currentPage === totalPages"
        @click="$emit('page-change', currentPage + 1)"
      >
        ›
      </button>

      <button
        type="button"
        class="page-button"
        :disabled="currentPage === totalPages"
        @click="$emit('page-change', totalPages)"
      >
        »
      </button>
    </div>
  </div>
</template>

<script setup>
defineProps({
  currentPage: {
    type: Number,
    required: true,
  },
  totalPages: {
    type: Number,
    required: true,
  },
  visiblePages: {
    type: Array,
    required: true,
  },
  loading: {
    type: Boolean,
    default: false,
  },
  totalCount: {
    type: Number,
    default: 0,
  },
});

defineEmits(["page-change"]);
</script>

