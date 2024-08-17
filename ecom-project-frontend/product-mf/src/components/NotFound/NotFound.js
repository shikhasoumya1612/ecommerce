import React from "react";
import notfound from "../../assets/not-found.png";
import styles from "./NotFound.module.css";

const NotFound = () => {
  return (
    <div className={`${styles["not-found"]}`}>
      <div className="d-flex justify-content-center">
        <img src={notfound} alt="Not Found" />
      </div>

      <p className="mt-5 text-center">
        “Your filters <span>caught our shoes offside</span>. Try a different
        formation!”
      </p>
    </div>
  );
};

export default NotFound;
