import React, { useState } from "react";
import { Routes, Route, BrowserRouter, Navigate } from "react-router-dom";
import Products from "./pages/Products";
import PDP from "./pages/PDP";
import { ToastContainer } from "react-toastify";
import FilterContext from "./context/FilterContext";
import ProductManagement from "./pages/ProductManagement";
import ErrorComponent from "./components/ErrorComponent/ErrorComponent";

const ProductRouter = () => {
  const [filterState, setFilterState] = useState({
    categoryId: "",
    gender: [],
    priceRange: {},
    keyword: "",
  });

  const ProtectedRoute = ({ element, isAdmin, ...props }) => {
    return isAdmin ? element : <Navigate to="/" />;
  };

  return (
    <FilterContext.Provider value={{ filterState, setFilterState }}>
      <Routes>
        <Route path="/" element={<Products />} />
        <Route path="/:id" element={<PDP />} />
        <Route
          path="/product-management"
          element={
            <ProtectedRoute
              element={<ProductManagement />}
              isAdmin={localStorage.getItem("isAdmin") === "true"}
            />
          }
        />
        <Route path="*" element={<ErrorComponent status={404} />} />
      </Routes>
    </FilterContext.Provider>
  );
};

export default ProductRouter;
