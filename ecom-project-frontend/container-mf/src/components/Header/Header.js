import React, { useState, Suspense } from "react";
import logo from "../../assets/logo.png";
import "./Header.css";
import { useNavigate } from "react-router-dom";
import isUserLoggedIn from "../../utils/isUserLoggedIn";
import { useContext } from "react";
import UserContext from "../../context/UserContext";
import axios from "axios";
import isAdmin from "../../utils/isAdmin";

const CartDropdown = React.lazy(() =>
  import("cart_mf/CartDropdown").catch(() => {
    return {
      default: () => (
        <div
          className="pt-1"
          data-toggle="tooltip"
          data-placement="bottom"
          title="Cart is down"
        >
          <i className="bi bi-cart cart-img-down h3 px-3" />
        </div>
      ),
    };
  })
);

const Header = () => {
  const navigate = useNavigate();
  const [dropdown, setDropdown] = useState(false);

  const { user, setUser } = useContext(UserContext);

  const fetchUserData = async () => {
    const userToken = isUserLoggedIn();

    if (!userToken) {
      navigate("/login");
    }

    const config = { headers: { Authorization: `Bearer ${userToken}` } };

    try {
      const response = await axios.get(
        "http://localhost:8080/users/my-data",
        config
      );

      setUser(response.data.user);
    } catch (error) {}
  };

  useState(() => {
    fetchUserData();
  }, []);

  return (
    <div className="container-fluid header">
      <div className="ps-5 row py-3 ">
        <a
          className={`col-lg-${isAdmin() === "true" ? 3 : 4} header-logo mt-2`}
        >
          <img
            src={logo}
            alt="logo"
            onClick={() => navigate("/")}
            role="button"
          />
        </a>
        {isAdmin() === "true" ? (
          <div className="col-lg-6 d-flex flex-content-start gap-4 header-links">
            <p onClick={() => navigate("/")}>Home</p>
            <p>
              <a onClick={() => navigate("/products")}>All Products</a>
            </p>
            <p>
              <a onClick={() => navigate("/user-management")}>Manage Users</a>
            </p>
            <p>
              <a onClick={() => navigate("/products/product-management")}>
                Manage Products
              </a>
            </p>
          </div>
        ) : (
          <div className="col-lg-5 d-flex flex-content-start gap-4 header-links">
            <p onClick={() => navigate("/")}>Home</p>
            <p>
              <a onClick={() => navigate("/products")}>All Products</a>
            </p>
            <p
              onClick={() =>
                navigate("/products", {
                  state: { gender: ["Men"] },
                })
              }
            >
              Men
            </p>
            <p
              onClick={() =>
                navigate("/products", {
                  state: { gender: ["Women"] },
                })
              }
            >
              Women
            </p>
          </div>
        )}
        <div className="col-lg-3 header-profile d-flex flex-row ">
          <Suspense fallback={<div className="loader"></div>}>
            <CartDropdown dropdown={dropdown} setDropdown={setDropdown} />
          </Suspense>

          {isUserLoggedIn() ? (
            <img
              src={user?.imgLink}
              alt="avatar"
              className="avatar-img"
              role="button"
              onClick={() => {
                navigate("/profile");
              }}
            />
          ) : (
            <div
              className="col-2 header-links my-auto"
              onClick={() => navigate("/login")}
            >
              <p>Login</p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default Header;
