import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  server:{
    host: true, // 允許透過區域網路 IP 連線
    proxy:{
      "/api":{
        target:"http://localhost:8081",
        changeOrigin:true
      },
      // 開發環境中，商品與房型圖片由 Spring Boot 的 uploads 目錄提供
      "/uploads":{
        target:"http://localhost:8081",
        changeOrigin:true
      }
    }
  }
})

