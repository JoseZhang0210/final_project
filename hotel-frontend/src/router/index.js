import { createRouter, createWebHistory } from 'vue-router'

import HomeView from '../views/HomeView.vue'
import RestaurantMenuView from '../views/RestaurantMenuView.vue'
import RestaurantManageView from '../views/RestaurantManageView.vue'
import RestaurantTimeManageView from '../views/RestaurantTimeManageView.vue'
import ReservationManageView from '../views/ReservationManageView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),

  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/about',
      name: 'about',
      component: () => import('../views/AboutView.vue'),
    },
    {
      path: '/restaurant-menu',
      name: 'restaurant-menu',
      component: RestaurantMenuView,
    },
    {
      path: '/restaurants',
      name: 'restaurants',
      component: RestaurantManageView,
    },
    {
      path: '/restaurant-times',
      name: 'restaurant-times',
      component: RestaurantTimeManageView,
    },
    {
      path: '/reservation-manage',
      name: 'reservation-manage',
      component: ReservationManageView,
    },
  ],
})

export default router