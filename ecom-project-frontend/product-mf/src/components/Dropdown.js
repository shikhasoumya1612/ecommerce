import React from "react";

const Dropdown = ({ filterValue, setFilterValue, values }) => {
  return (
    <div class="filter text-center">
      <button
        type="button"
        class="btn dropdown-toggle"
        data-toggle="dropdown"
        aria-haspopup="true"
        aria-expanded="false"
      >
        {filterValue || "All"}
      </button>

      <div class="dropdown-menu">
        <a class="dropdown-item" onClick={() => setFilterValue()}>
          All
        </a>
        {values.map((value, index) => {
          return (
            <a
              class="dropdown-item"
              onClick={() => setFilterValue(value)}
              key={index}
            >
              {value.name}
            </a>
          );
        })}
      </div>
    </div>
  );
};

export default Dropdown;
