import { createRouter, createWebHistory } from "vue-router";

import MainLayout from "../layouts/MainLayout.vue";
import AdminLayout from "../layouts/AdminLayout.vue";
import DashboardView from "../views/DashboardView.vue";
import HomeView from "../views/HomeView.vue";
import MemberManageView from "../views/MemberManageView.vue";
//--------------------餐廳後台管理-----------------------------------
import RestaurantMenuView from "../views/RestaurantMenuView.vue";
import RestaurantManageView from "../views/RestaurantManageView.vue";
import RestaurantTimeManageView from "../views/RestaurantTimeManageView.vue";
import ReservationManageView from "../views/ReservationManageView.vue";
//--------------------登入登出註冊管理--------------------------------
import LoginView from "../views/LoginView.vue";
import LogoutView from "../views/LogoutView.vue";
import RegisterView from "../views/RegisterView.vue";
//---------------------- 商品後台管理-----------------
import ProductManageView from "../views/ProductManageView.vue";
import ProductEditView from "../views/ProductEditView.vue";
import ProductAddView from "../views/ProductAddView.vue";
import ProductShopView from "../views/ProductShopView.vue";
//---------------------------------------------------
import RentalView from "../views/RentalView.vue";
import VenueView from "../views/VenueView.vue";
//---------------------------------------------------
import RoomBookingOrderView from "@/views/RoomBookingOrderView.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),

  routes: [
    {
      path: "/admin",
      component: AdminLayout,
      children: [
        {
          path: "",
          name: "dashboard",
          component: DashboardView,
        },
        {
          path: "members",
          name: "admin-members",
          component: MemberManageView,
        },
        {
          path: "restaurants",
          name: "admin-restaurants",
          component: RestaurantManageView,
        },
        {
          path: "products",
          name: "admin-products",
          component: ProductManageView,
        },
        {
          path: "products/:id/edit",
          name: "admin-product-edit",
          component: ProductEditView,
        },
        {
          path: "products/add",
          name: "admin-product-add",
          component: ProductAddView,
        },
        {
          path: "restaurant-times",
          name: "admin-restaurant-times",
          component: RestaurantTimeManageView,
        },
        {
          path: "reservations",
          name: "admin-reservations",
          component: ReservationManageView,
        },
        // ==========訂房==========
        {
          path: "room-booking-order",
          name: "admin-roombookingorder",
          component: RoomBookingOrderView,
        },
        //===============================
         {
          path: "venues",
          name: "admin-venues",
          component: VenueView,
        },
      ],
    },

    {
      path: "/",
      component: MainLayout,
      children: [
        {
          path: "",
          name: "home",
          component: HomeView,
        },
        {
          path: "/login",
          name: "login",
          component: LoginView,
        },
        {
          path: "/logout",
          name: "logout",
          component: LogoutView,
        },
        {
          path: "/register",
          name: "register",
          component: RegisterView,
        },
        {
          path: "products",
          name: "product-shop",
          component: ProductShopView,
        },
        {
          path: "restaurant-menu",
          name: "restaurant-menu",
          component: RestaurantMenuView,
        },
        {
          path: "restaurants",
          name: "restaurants",
          component: RestaurantManageView,
        },
        {
          path: "restaurant-times",
          name: "restaurant-times",
          component: RestaurantTimeManageView,
        },
        {
          path: "reservation-manage",
          name: "reservation-manage",
          component: ReservationManageView,
        },
        {
          path: "rentals",
          name: "rentals",
          component: RentalView,
        },
        {
          path: "about",
          name: "about",
          component: () =>
            import("../views/AboutView.vue"),
        },
      ],
    },
  ],
});

export default router;