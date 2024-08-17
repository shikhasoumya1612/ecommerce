import React from "react";
import ReactDOM from "react-dom";

import "./index.css";
import CartRouter from "./CartRouter";
import { BrowserRouter } from "react-router-dom";
import CartDropdown from "./components/CartDropdown/CartDropdown";
import { ToastContainer } from "react-toastify";
import ScrollToTop from "./utils/ScrollToTop";
const App = () => (
  <BrowserRouter>
    <ScrollToTop />
    <ToastContainer autoClose={3000} theme="dark" />
    <CartDropdown />
    <CartRouter />
  </BrowserRouter>
);
ReactDOM.render(<App />, document.getElementById("app"));
