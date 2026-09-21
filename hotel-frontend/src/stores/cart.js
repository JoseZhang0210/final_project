import { computed, ref } from "vue";
import { defineStore } from "pinia";
import { cartApi } from "@/api/cartApi";

export const useCartStore = defineStore("cart", () => {
  const items = ref([]);
  const loading = ref(false);
  const totalQuantity = ref(0);
  const totalAmount = ref(0);
  const itemCount = computed(() => totalQuantity.value);

  function applyCart(cart) {
    items.value = Array.isArray(cart?.items) ? cart.items : [];
    totalQuantity.value = Number(cart?.totalQuantity ?? 0);
    totalAmount.value = Number(cart?.totalAmount ?? 0);
  }

  async function migrateLocalCart() {
    const raw = localStorage.getItem("cart");
    if (!raw) return;
    try {
      const parsed = JSON.parse(raw);
      const legacyItems = Array.isArray(parsed)
        ? parsed.map(({ productId, quantity }) => ({
            productId: Number(productId), quantity: Number(quantity),
          })).filter(item => item.productId > 0 && item.quantity > 0)
        : [];
      if (legacyItems.length) applyCart(await cartApi.merge(legacyItems));
      localStorage.removeItem("cart");
    } catch (error) {
      console.error("舊購物車移轉失敗：", error);
      throw error;
    }
  }

  async function load() {
    if (!localStorage.getItem("token")) { applyCart(null); return; }
    loading.value = true;
    try {
      await migrateLocalCart();
      applyCart(await cartApi.get());
    } finally { loading.value = false; }
  }

  async function addItem(productId, quantity = 1) {
    applyCart(await cartApi.add(Number(productId), Number(quantity)));
  }
  async function updateQuantity(productId, quantity) {
    applyCart(await cartApi.update(Number(productId), Number(quantity)));
  }
  async function removeItem(productId) { applyCart(await cartApi.remove(Number(productId))); }
  async function clear() { await cartApi.clear(); applyCart(null); }
  async function checkout(couponCode) {
    const order = await cartApi.checkout(couponCode);
    applyCart(null);
    localStorage.removeItem("cart");
    return order;
  }
  function resetLocalState() { applyCart(null); }

  return { items, loading, totalQuantity, totalAmount, itemCount, load, addItem,
    updateQuantity, removeItem, clear, checkout, resetLocalState };
});
