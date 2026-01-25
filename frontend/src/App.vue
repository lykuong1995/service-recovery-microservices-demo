<template>
  <div>
    <h1>Simple Order App</h1>

    <div v-if="!loggedIn">
      <h2>Login</h2>
      <input v-model="username" placeholder="Username" />
      <input v-model="password" type="password" placeholder="Password" />
      <button @click="login">Login</button>
    </div>

    <div v-else>
      <h2>Create Order</h2>
      <input v-model="amount" placeholder="Amount" />
      <button @click="createOrder">Submit</button>

      <h2>My Orders</h2>
      <button @click="fetchOrders">Refresh</button>
      <ul>
        <li v-for="order in orders" :key="order.id">
          {{ order.id }} - {{ order.amount }} - {{ order.status }}
        </li>
      </ul>

      <button @click="logout">Logout</button>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import api, { setToken } from "./api";

export default {
  data() {
    return {
      username: "",
      password: "",
      amount: "",
      orders: [],
      loggedIn: false,
      token: null,
    };
  },
  methods: {
    async login() {
      try {
        const response = await axios.post(
          "http://localhost:8081/auth/login",
          {
            username: this.username,
            password: this.password,
          }
        );

        this.token = response.data.accessToken;
        setToken(this.token);
        this.loggedIn = true;
        this.fetchOrders();
      } catch (err) {
        alert("Login failed");
      }
    },

    async fetchOrders() {
      const response = await api.get("/orders/my");
      this.orders = response.data;
    },

    async createOrder() {
      await api.post("/orders", {
        amount: this.amount,
      });
      this.amount = "";
      this.fetchOrders();
    },

    logout() {
      this.loggedIn = false;
      this.token = null;
      this.orders = [];
    },
  },
};
</script>