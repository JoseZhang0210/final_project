<template lang="">
   <h1>找不到畫面</h1>
   <button @click="router.push('/admin/')">回首頁</button>
</template>
<script setup>

import { useRouter } from 'vue-router';
const router = useRouter();
</script>
<style></style>