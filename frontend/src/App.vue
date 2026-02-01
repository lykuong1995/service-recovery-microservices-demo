<template>
  <div class="container">
    <h1>Customer Order Portal</h1>

    <!-- LOGIN -->
    <div v-if="!loggedIn">
      <h2>{{ isRegister ? "Register" : "Login" }}</h2>

      <input v-model="username" placeholder="Username" />
      <input v-model="password" type="password" placeholder="Password" />

      <button v-if="!isRegister" @click="login">Login</button>
      <button v-else @click="register">Register</button>

      <p style="margin-top:10px;">
        <span v-if="!isRegister">
          Don't have an account?
          <a href="#" @click.prevent="toggleMode">Register</a>
        </span>
        <span v-else>
          Already have an account?
          <a href="#" @click.prevent="toggleMode">Login</a>
        </span>
      </p>
    </div>

    <!-- MAIN APP -->
    <div v-else>
      <button class="logout" @click="logout">Logout</button>

      <!-- PRODUCT LIST -->
      <div class="card">
        <h2>Products</h2>
        <div v-for="product in products" :key="product.id" class="product">
          <span>{{ product.name }} - ${{ product.price }}</span>
          <button @click="addToCart(product)">Add to Cart</button>
        </div>
      </div>

      <!-- CART -->
      <div class="card">
        <h2>Cart</h2>

        <div v-if="cart.length === 0">Cart is empty.</div>

        <div v-for="item in cart" :key="item.id" class="cart-item">
          <span>{{ item.productName }}</span>
          <div>
            <button @click="decrease(item)">-</button>
            {{ item.quantity }}
            <button @click="increase(item)">+</button>
          </div>
          <span>${{ (item.price * item.quantity).toFixed(2) }}</span>
          <button @click="removeFromCart(item)">Remove</button>
        </div>

        <h3>Total: ${{ cartTotal }}</h3>

        <button :disabled="cart.length === 0" @click="submitOrder">
          Submit Order
        </button>
      </div>

      <!-- ORDER HISTORY -->
      <div class="card">
        <h2>My Orders</h2>
        <button @click="fetchOrders">Refresh</button>

        <table v-if="orders.length > 0">
          <thead>
            <tr>
              <th>Order #</th>
              <th>Status</th>
              <th>Total</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="order in orders" :key="order.id">
              <td>{{ order.orderNumber }}</td>
              <td>{{ order.status }}</td>
              <td>${{ order.totalAmount }}</td>
            </tr>
          </tbody>
        </table>

        <div v-else>No orders yet.</div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import api from "./api";

export default {
  data() {
    return {
      username: "",
      password: "",
      loggedIn: false,
      token: null,
      isRegister: false,
      orders: [],
      cart: [],
      products: [
        { id: 1, name: "Laptop", price: 1000 },
        { id: 2, name: "Keyboard", price: 80 },
        { id: 3, name: "Mouse", price: 40 },
        { id: 4, name: "Monitor", price: 300 },
      ],
    };
  },

  computed: {
    cartTotal() {
      return this.cart
        .reduce((sum, item) => sum + item.price * item.quantity, 0)
        .toFixed(2);
    },
  },

  methods: {
    toggleMode() {
      this.isRegister = !this.isRegister;
      this.username = "";
      this.password = "";
    },

    async register() {
      try {
        await axios.post(
          `${import.meta.env.VITE_AUTH_URL}/auth/register`,
          {
            username: this.username,
            password: this.password,
          }
        );

        alert("Registration successful. Please login.");
        this.isRegister = false;
        this.username = "";
        this.password = "";

      } catch (err) {
        alert("Registration failed");
      }
    },
    async login() {
      try {
        const response = await axios.post(
          `${import.meta.env.VITE_API_URL}/auth/login`,
          {
            username: this.username,
            password: this.password,
          }
        );

        const accessToken = response.data.accessToken;
        const refreshToken = response.data.refreshToken;

        localStorage.setItem("accessToken", accessToken);
        localStorage.setItem("refreshToken", refreshToken);

        this.loggedIn = true;

        await this.fetchOrders();

      } catch (err) {
        console.error(err);
        alert("Login failed");
      }
    },

    logout() {
      this.loggedIn = false;
      this.token = null;
      this.cart = [];
      this.orders = [];
    },

    addToCart(product) {
      const existing = this.cart.find((i) => i.id === product.id);

      if (existing) {
        existing.quantity++;
      } else {
        this.cart.push({
          id: product.id,
          productName: product.name,
          price: product.price,
          quantity: 1,
        });
      }
    },

    increase(item) {
      item.quantity++;
    },

    decrease(item) {
      if (item.quantity > 1) {
        item.quantity--;
      }
    },

    removeFromCart(item) {
      this.cart = this.cart.filter((i) => i.id !== item.id);
    },

    async submitOrder() {
      await api.post("/orders", {
        items: this.cart.map((i) => ({
          productName: i.productName,
          quantity: i.quantity,
          price: i.price,
        })),
      });

      this.cart = [];
      this.fetchOrders();
    },

    async fetchOrders() {
      const response = await api.get("/orders/my");
      this.orders = response.data;
    },
  },
};
</script>

<style>
.container {
  max-width: 800px;
  margin: auto;
  font-family: Arial;
}

.card {
  border: 1px solid #ccc;
  padding: 16px;
  margin-bottom: 20px;
  border-radius: 6px;
}

.product,
.cart-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}

button {
  margin-left: 5px;
}

.logout {
  float: right;
}
</style>