import { createRouter, createWebHistory } from "vue-router";

import MainLayout from "../layouts/MainLayout.vue";
import AdminLayout from "../layouts/AdminLayout.vue";
import DashboardView from "../views/DashboardView.vue";
import HomeView from "../views/HomeView.vue";
import MemberManageView from "../views/MemberManageView.vue";
import EmployeeManageView from "../views/EmployeeManageView.vue";
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
//---------------------- 訂單後台管理 -----------------
import AdminOrdersView from "../views/AdminOrdersView.vue";
//---------------------------------------------------
import RentalView from "../views/RentalView.vue";
import VenueView from "../views/VenueView.vue";
//---------------------------------------------------
import RoomTypeManageView from "../views/RoomTypeManageView.vue";
import RoomManageView from "../views/RoomManageView.vue";
import RoomImageManageView from "../views/RoomImageManageView.vue";
import RoomTaskManageView from "../views/RoomTaskManageView.vue";
import RoomBookingManageView from "../views/RoomBookingManageView.vue";
import BookingPaymentManageView from "../views/BookingPaymentManageView.vue";

// ---------------購物車 / 結帳 / 付款-------------------
import CartView from "../views/CartView.vue";
import CheckoutView from "../views/CheckoutView.vue";
import PaymentView from "../views/PaymentView.vue";
// ---------------優惠券-------------------
import AdminCouponsView  from "@/views/AdminCouponsView.vue";

//--------------- 會員中心 -----------------
import MemberLayout from "../layouts/MemberLayout.vue";
import MemberProfileView from "../views/MemberProfileView.vue";
import MyOrdersView from "../views/MyOrdersView.vue";

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
          path: "employees",
          name: "admin-employees",
          component: EmployeeManageView,
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
          path: "orders",
          name: "admin-orders",
          component: AdminOrdersView,
        },
        {
          path: "coupons",
          name: "admin-coupons",
          component: AdminCouponsView,
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
          path: "room-status",
          name: "admin-room-status",
          component: RoomManageView,
        },
        {
          path: "room-types",
          name: "admin-room-types",
          component: RoomTypeManageView,
        },
        {
          path: "room-images",
          name: "admin-room-images",
          component: RoomImageManageView,
        },
        {
          path: "room-task",
          name: "admin-room-task",
          component: RoomTaskManageView,
        },
        {
          path: "room-booking",
          name: "admin-room-booking",
          component: RoomBookingManageView,
        },
        {
          path: "booking-payments",
          name: "admin-booking-payments",
          component: BookingPaymentManageView,
        },
        //===============================
        {
          path: "venues",
          name: "admin-venues",
          component: VenueView,
        },

        {
          path: "rental",
          name: "admin-rental",
          component: RentalView,
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
          path: "cart",
          name: "cart",
          component: CartView,
        },
        {
          path: "checkout",
          name: "checkout",
          component: CheckoutView,
        },
        {
          path: "payment/:orderId",
          name: "payment",
          component: PaymentView,
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
          component: () => import("../views/AboutView.vue"),
        },
        {
          path: "my-orders",
          name: "my-orders",
          component: MyOrdersView,
        },
        {
          path: "member",
          component: MemberLayout,
          children: [
            {
              path: "",
              name: "member-center",
              component: MemberProfileView,
            },
            {
              path: "profile",
              name: "member-profile",
              component: MemberProfileView,
            },
            {
              path: "orders",
              name: "member-orders",
              component: MyOrdersView,
            },
          ],
        },
      ],
    },
  ],
});

export default router;
