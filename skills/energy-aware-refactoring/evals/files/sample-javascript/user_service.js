const fs = require("fs");

async function getUsersWithOrders(userIds) {
  const result = [];
  for (const id of userIds) {
    const user = await fetch(`https://api.example.com/users/${id}`);
    const userData = await user.json();
    const orders = await fetch(`https://api.example.com/users/${id}/orders`);
    const ordersData = await orders.json();
    result.push({ user: userData, orders: ordersData });
  }
  return result;
}

function loadConfig() {
  const raw = fs.readFileSync("/etc/app/config.json", "utf-8");
  const config = JSON.parse(raw);
  return config;
}

function deepClone(obj) {
  return JSON.parse(JSON.stringify(obj));
}

function logBatch(messages) {
  let output = "";
  for (const msg of messages) {
    output += `[${new Date().toISOString()}] ${msg}\n`;
  }
  fs.writeFileSync("/var/log/app.log", output);
}
