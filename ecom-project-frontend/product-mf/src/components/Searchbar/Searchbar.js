import React, { useState, useContext } from "react";
import { debounce } from "lodash";
import "./Searchbar.css";
import FilterContext from "../../context/FilterContext";

const SearchBar = () => {
  const { filterState, setFilterState } = useContext(FilterContext);
  const [inputValue, setInputValue] = useState(filterState.keyword);

  const handleInputChange = (event) => {
    const newValue = event.target.value;
    setInputValue(newValue);
  };

  const handleSearch = () => {
    setFilterState({ ...filterState, keyword: inputValue });
  };

  const clearSearch = () => {
    if (inputValue !== "") setInputValue("");

    if (filterState.keyword !== "")
      setFilterState({ ...filterState, keyword: "" });
  };

  return (
    <div className="container mt-1">
      <div className="row">
        <div className="col-12">
          <div className="input-group mb-3 search-bar">
            <div className="d-flex flex-row input-group-append">
              <span
                className="input-group-text bg-white border-left-0 search-icon"
                onClick={handleSearch}
              >
                <i className="bi bi-search"></i>
              </span>
            </div>
            <input
              type="text"
              className="form-control search-input"
              placeholder="Click icon to search"
              aria-label="Search"
              value={inputValue}
              onChange={handleInputChange}
            />
            {inputValue !== "" && (
              <span
                className="input-group-text bg-transparent border-left-0 clear-icon"
                onClick={clearSearch}
              >
                <i className="bi bi-x-lg"></i>
              </span>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default SearchBar;
