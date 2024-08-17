import React from "react";
import ReactDOM from "react-dom";

import "./index.css";

import { Routes, Route, BrowserRouter, Navigate } from "react-router-dom";
import ProductRouter from "./ProductRouter";
import { ToastContainer } from "react-toastify";

const App = () => (
  <div>
    <ProductRouter />
  </div>
);

ReactDOM.render(
  <BrowserRouter>
    <ToastContainer autoClose={3000} theme="dark" />
    <App />
  </BrowserRouter>,
  document.getElementById("app")
);
