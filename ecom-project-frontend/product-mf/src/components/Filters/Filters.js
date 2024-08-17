import React, { useState, useEffect } from "react";
import axios from "axios";
import styles from "./Filters.module.css";

const Filters = ({ filterState, setFilterState }) => {
  const [categories, setCategories] = useState([]);
  const fetchAllCategories = async () => {
    try {
      const response = await axios.get(
        "http://localhost:8060/products/categories"
      );

      setCategories(response.data);
    } catch (error) {}
  };

  useEffect(() => {
    fetchAllCategories();
  }, []);

  return (
    <div className={`ms-3 mt-2 container-fluid ${styles.filters}`}>
      {/* Categories */}
      <div>
        <div>
          <p
            className={`${styles["filter-categories"]} ${
              filterState.categoryId === "" ? styles.selected : ""
            }`}
            onClick={() => {
              setFilterState({ ...filterState, categoryId: "" });
            }}
          >
            All Categories
          </p>
        </div>
        {categories.map((category) => (
          <div
            key={category.id}
            onClick={() => {
              setFilterState({ ...filterState, categoryId: category.id });
            }}
          >
            <p
              className={`${styles["filter-categories"]} ${
                filterState.categoryId === category.id ? styles.selected : ""
              }`}
            >
              {category.name}
            </p>
          </div>
        ))}
      </div>

      {/* Gender */}
      <div className="mt-5">
        <div>
          <p className={`${styles["filter-genders"]}`}>Gender</p>
        </div>
        <div>
          {["Men", "Women"].map((gender, index) => (
            <div className="my-2 form-check" key={gender}>
              <input
                className={`form-check-input`}
                type="checkbox"
                value={gender}
                checked={filterState.gender.includes(gender)}
                id="flexCheckChecked"
                onChange={(e) => {
                  if (e.target.checked) {
                    setFilterState({
                      ...filterState,
                      gender: [...filterState.gender, e.target.value],
                    });
                  } else {
                    setFilterState({
                      ...filterState,
                      gender: filterState.gender.filter(
                        (val) => val !== e.target.value
                      ),
                    });
                  }
                }}
              />
              <label className={`${styles["filter-gender"]} form-check-label`}>
                {gender}
              </label>
            </div>
          ))}
        </div>
      </div>

      {/* Price range */}
      <div className="mt-5">
        <div>
          <p className={`${styles["filter-genders"]}`}>Price Range</p>
        </div>

        <div>
          <input
            className={`form-check-input`}
            type="radio"
            name="priceRange"
            value={{}}
            onChange={() => {
              setFilterState({ ...filterState, priceRange: {} });
            }}
            defaultChecked
          />
          <label className={`ms-2 ${styles["filter-gender"]} form-check-label`}>
            All Prices
          </label>
          {[
            { min: 2501, max: 7500 },
            { min: 7501, max: 12999 },
            { min: 13000 },
          ].map((range, index) => (
            <div className="my-2 form-check " key={range.min}>
              <input
                className={`form-check-input`}
                type="radio"
                name="priceRange"
                value={range}
                onChange={() => {
                  setFilterState({ ...filterState, priceRange: range });
                }}
              />
              <label className={`${styles["filter-gender"]} form-check-label`}>
                {!range.max
                  ? " > " + "₹ " + range.min.toLocaleString("en-IN")
                  : "₹ " +
                    range.min.toLocaleString("en-IN") +
                    " - " +
                    "₹ " +
                    range.max.toLocaleString("en-IN")}
              </label>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default Filters;
