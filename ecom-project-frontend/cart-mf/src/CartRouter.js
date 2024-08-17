import React, { useState } from "react";
import { Routes, Route, BrowserRouter, Navigate } from "react-router-dom";

import { ToastContainer } from "react-toastify";
import Cart from "./pages/Cart/Cart";
import Order from "./pages/Order/Order";
import ErrorComponent from "./components/ErrorComponent/ErrorComponent";

const CartRouter = () => {
  return (
    <div>
      <Routes>
        <Route path="/" element={<Cart />} />
        <Route path="/buy" element={<Order />} />
        <Route path="*" element={<ErrorComponent status={404} />} />
      </Routes>
    </div>
  );
};

export default CartRouter;
