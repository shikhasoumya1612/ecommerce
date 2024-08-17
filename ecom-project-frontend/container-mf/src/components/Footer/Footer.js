import React from "react";
import "./Footer.css";
import { useNavigate } from "react-router-dom";

const Footer = () => {
  const navigate = useNavigate();

  return (
    <footer className="bg-light text-dark py-4 footer">
      <div className="container">
        <div className="row">
          <div className="col-md-5 my-3">
            <div className="head mb-3">About us</div>
            <div className="links pe-5">
              <div className="pe-3">
                Welcome to a world where style meets comfort. Our curated
                collection of footwear spans from trendy sneakers to elegant
                heels, ensuring that you find the perfect pair for every
                occasion.
              </div>
            </div>
          </div>
          <div className="col-md-1 my-3"></div>
          <div className="col-md-3 my-3">
            <div className="head mb-3">Sitemap</div>

            <div className="links">
              <li className="list-unstyled">
                <p
                  role="button"
                  className="text-dark my-1"
                  onClick={() => navigate("/")}
                >
                  Home
                </p>
                <p
                  role="button"
                  className="text-dark my-1"
                  onClick={() => navigate("/products")}
                >
                  Products
                </p>
                <p
                  role="button"
                  className="text-dark my-1"
                  onClick={() =>
                    navigate("/products", { state: { gender: ["Men"] } })
                  }
                >
                  Men
                </p>
                <p
                  role="button"
                  className="text-dark my-1"
                  onClick={() =>
                    navigate("/products", { state: { gender: ["Women"] } })
                  }
                >
                  Women
                </p>
              </li>
            </div>
          </div>
          <div className="col-md-3 my-3">
            <div className="head mb-3">Visit us here</div>

            <div className="links">
              <li className="list-unstyled">
                <p className="text-dark my-2" onClick={() => navigate("/")}>
                  <i role="button" className="bi bi-instagram h6"></i>
                </p>
                <p
                  className="text-dark my-2"
                  onClick={() => navigate("/products")}
                >
                  <i role="button" className="bi bi-twitter h6"></i>
                </p>
                <p className="text-dark my-2">
                  <i role="button" className="bi bi-envelope h6"></i>
                </p>
              </li>
            </div>
          </div>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
