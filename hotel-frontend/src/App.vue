<script setup>
import { RouterView } from 'vue-router'
</script>

<template>
  <RouterView />
</template>


<style>
* {
  box-sizing: border-box;
}

html,
body {
  margin: 0;
  min-width: 320px;
  min-height: 100vh;
}

body {
  display: block;
  font-family: Arial, "Microsoft JhengHei", sans-serif;
}

#app {
  display: block;
  width: 100%;
  min-height: 100vh;
  max-width: none;
  margin: 0;
  padding: 0;
}
</style>
