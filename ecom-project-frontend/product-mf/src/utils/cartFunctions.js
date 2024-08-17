import axios from "axios";
import { toast } from "react-toastify";
const isUserLoggedIn = require("./isUserLoggedIn");

export function anonymousAddToCart(productId, quantity, size) {
  const cart = localStorage.getItem("cart");
  if (!cart) {
    localStorage.setItem("cart", JSON.stringify([]));
    cartData.push({ productId, quantity, size: size });
  } else {
    const cartData = JSON.parse(cart);
    let found = false;
    cartData.forEach((cartItem) => {
      if (cartItem.productId === productId && cartItem.size === size) {
        cartItem.quantity += quantity;
        found = true;
      }
    });
    if (!found) {
      cartData.push({ productId, quantity, size });
    }
    localStorage.setItem("cart", JSON.stringify(cartData));
  }
  toast.success("Product added successfully");
}

export async function mergeCart() {
  const userToken = isUserLoggedIn();
  if (!userToken) {
    return;
  }

  const config = { headers: { Authorization: `Bearer ${userToken}` } };

  try {
    const cart = localStorage.getItem("cart");
    if (!cart) {
      return;
    }
    const cartData = JSON.parse(cart);

    for (const cartItem of cartData) {
      try {
        await axios.post(
          "http://localhost:8090/cart/addToCart",
          cartItem,
          config
        );
      } catch (error) {}
    }

    localStorage.removeItem("cart");
  } catch (error) {}
}
