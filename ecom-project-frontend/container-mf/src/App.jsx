import React, { Suspense } from "react";
import ReactDOM from "react-dom";

import "./index.css";
import Header from "./components/Header/Header.js";

import { Routes, Route, BrowserRouter, Navigate } from "react-router-dom";
import Dashboard from "./pages/Dashboard/Dashboard";
import Breadcrumbs from "./components/Breadcumbs/Breadcrumbs";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import { useEffect } from "react";
import Profile from "./pages/Profile/Profile.js";
import { useState } from "react";
import UserContext from "./context/UserContext.js";
import Login from "./pages/Login/Login.js";
import UserManagement from "./pages/UserManagement.js/UserManagement.js";
import ErrorComponent from "./components/ErrorComponent/ErrorComponent.js";
import ErrorBoundary from "./components/ErrorBoundary.js";
import Signup from "./pages/Signup/Signup.js";
import Footer from "./components/Footer/Footer.js";
import ScrollToTop from "./utils/ScrollToTop.js";
import { Helmet } from "react-helmet";

const ProductRouter = React.lazy(() =>
  import("product_mf/ProductRouter").catch(() => {
    return { default: () => <ErrorComponent /> };
  })
);

const CartRouter = React.lazy(() =>
  import("cart_mf/CartRouter").catch(() => {
    return { default: () => <ErrorComponent /> };
  })
);

const App = () => {
  const [user, setUser] = useState();

  const [isAdmin, setIsAdmin] = useState(false);

  useEffect(() => {
    setIsAdmin(localStorage.getItem("isAdmin") || "false");
  }, [user]);

  const ProtectedRoute = ({ element, isAdmin, ...props }) => {
    return isAdmin ? element : <Navigate to="/" />;
  };

  return (
    <BrowserRouter>
      <ToastContainer autoClose={3000} theme="dark" />
      <ScrollToTop />

      <UserContext.Provider value={{ user, setUser }}>
        <div className="pb-5">
          <Header />
        </div>

        <div className="app-content mb-5">
          <Breadcrumbs />
          <Routes>
            <Route
              path="/"
              element={
                <>
                  <Helmet>
                    <title>SB | Home</title>
                  </Helmet>
                  <Dashboard />
                </>
              }
            />
            <Route
              path="/user-management"
              element={
                <ProtectedRoute
                  element={
                    <>
                      <Helmet>
                        <title>SB | Manage</title>
                      </Helmet>
                      <UserManagement />
                    </>
                  }
                  isAdmin={isAdmin}
                />
              }
            />
            <Route
              path="/login"
              element={
                <>
                  <Helmet>
                    <title>SB | Login</title>
                  </Helmet>
                  <Login />
                </>
              }
            />
            <Route
              path="/register"
              element={
                <>
                  <Helmet>
                    <title>SB | Register</title>
                  </Helmet>
                  <Signup />
                </>
              }
            />

            <Route
              path="/profile"
              element={
                <>
                  <Helmet>
                    <title>SB | Profile</title>
                  </Helmet>
                  <Profile />
                </>
              }
            />

            <Route
              path="/products/*"
              element={
                <Suspense fallback={<div className="loader"></div>}>
                  <>
                    <Helmet>
                      <title>SB | Products</title>
                    </Helmet>
                    <ProductRouter />
                  </>
                </Suspense>
              }
            />
            <Route
              path="/cart/*"
              element={
                <Suspense fallback={<div className="loader"></div>}>
                  <>
                    <Helmet>
                      <title>SB | Cart</title>
                    </Helmet>
                    <CartRouter />
                  </>
                </Suspense>
              }
            />

            <Route path="*" element={<ErrorComponent status={404} />} />
          </Routes>
        </div>

        <div className="footer">
          <Footer />
        </div>
      </UserContext.Provider>
    </BrowserRouter>
  );
};

ReactDOM.render(<App />, document.getElementById("app"));
