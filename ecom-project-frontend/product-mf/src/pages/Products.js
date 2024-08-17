import React, { useContext, useEffect, useState } from "react";
import ProductCard from "../components/ProductCard";
import "../styles/products.css";
import axios from "axios";
import Filters from "../components/Filters/Filters";
import filterGif from "../assets/filter.gif";
import filterIcon from "../assets/filter.png";
import "react-loading-skeleton/dist/skeleton.css";
import NotFound from "../components/NotFound/NotFound";
import { useLocation } from "react-router-dom";
import SearchBar from "../components/Searchbar/Searchbar";
import FilterContext from "../context/FilterContext";
import ErrorComponent from "../components/ErrorComponent/ErrorComponent";
import qs from "qs";

const Products = () => {
  const [products, setProducts] = useState([]);
  const [showFilters, setShowFilters] = useState(true);
  const [showGif, setShowGif] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const location = useLocation();

  const { filterState, setFilterState } = useContext(FilterContext);

  const fetchAllProducts = async () => {
    console.log(filterState);
    try {
      setLoading(true);
      setError(null);
      const response = await axios.get(
        "http://localhost:8060/products/products",
        {
          params: {
            categoryId: filterState.categoryId,
            minPrice: filterState.priceRange.min,
            maxPrice: filterState.priceRange.max,
            keyword: filterState.keyword,
            gender: filterState.gender,
          },
          paramsSerializer: (params) =>
            qs.stringify(params, { arrayFormat: "repeat" }),
        }
      );

      setProducts(response.data);
    } catch (error) {
      setError(error);
    } finally {
      setLoading(false);
    }
  };

  const toggleFilter = () => {
    setShowFilters((prevState) => !prevState);
    setShowGif(true);

    setTimeout(() => {
      setShowGif(false);
    }, 1000);
  };

  useEffect(() => {
    setFilterState((prevState) => ({
      ...prevState,
      gender: location.state?.gender || [],
      categoryId: location.state?.categoryId || "",
    }));
  }, [location]);

  useEffect(() => {
    fetchAllProducts();
  }, [filterState]);

  return (
    <>
      {error ? (
        <ErrorComponent status={error.response?.status} />
      ) : (
        <>
          <div className="container-fluid">
            <div className="d-flex flex-row justify-content-end gap-4 mb-5 search-filter">
              <div>
                <SearchBar />
              </div>

              <div className="pe-4">
                <button onClick={toggleFilter} className="filter-button">
                  {showFilters ? "Hide" : "Show"} Filters
                  {showGif ? (
                    <img
                      className="ms-2"
                      src={filterGif}
                      alt="Filtering"
                      width="25px"
                    />
                  ) : (
                    <img
                      className="ms-2"
                      src={filterIcon}
                      alt="icon"
                      width="25px"
                    />
                  )}
                </button>
              </div>
            </div>

            <div className="row">
              {showFilters && (
                <div className="col-3 slide-in-left">
                  <Filters
                    filterState={filterState}
                    setFilterState={setFilterState}
                  />
                </div>
              )}

              {loading ? (
                <div className="loader mx-auto mt-5"></div>
              ) : (
                <div className={`${showFilters ? "col-9" : "col-12"}`}>
                  {products.length > 0 || loading ? (
                    <div className="d-flex justify-content-start flex-wrap">
                      {products.map((product) => (
                        <div key={product.id} className="py-2 px-4">
                          <ProductCard
                            product={product}
                            showFilters={showFilters}
                          />
                        </div>
                      ))}
                    </div>
                  ) : (
                    <NotFound />
                  )}
                </div>
              )}
            </div>
          </div>
        </>
      )}
    </>
  );
};

export default Products;
