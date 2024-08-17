import "./CartDropdown.css";
import cart from "../../assets/cart.png";
import { useState, useEffect, useRef } from "react";
import isUserLoggedIn from "../../utils/isUserLoggedIn";
import axios from "axios";
import CartItem from "../CartItemDropdown/CartItemDropdown";
import { useNavigate } from "react-router-dom";

const CartDropdown = ({ dropdown, setDropdown }) => {
  const navigate = useNavigate();
  const [cartData, setCartData] = useState([]);

  const dropdownRef = useRef(null);

  const [error, setError] = useState(null);

  const [productLoadError, setProductLoadError] = useState(null);

  const fetchCartData = async () => {
    setError(null);

    const userToken = isUserLoggedIn();
    if (!userToken) {
      const cart = localStorage.getItem("cart");
      if (!cart) {
        localStorage.setItem("cart", JSON.stringify([]));
      } else {
        setCartData(JSON.parse(cart));
      }
    } else {
      const config = { headers: { Authorization: `Bearer ${userToken}` } };

      try {
        const response = await axios.get("http://localhost:8090/cart", config);
        setCartData(response.data.cart.cartItems);
      } catch (error) {
        setError(error);
      }
    }
  };

  useEffect(() => {
    setProductLoadError(null);
    fetchCartData();
  }, [dropdown]);

  useEffect(() => {
    const checkIfClickedOutside = (e) => {
      if (
        dropdown &&
        dropdownRef.current &&
        !dropdownRef.current.contains(e.target)
      ) {
        setDropdown(false);
      }
    };

    document.addEventListener("mousedown", checkIfClickedOutside);

    return () => {
      document.removeEventListener("mousedown", checkIfClickedOutside);
    };
  }, [dropdown]);

  return (
    <>
      {error ? (
        <div className="pt-1">
          <i className="bi bi-cart cart-img-disabled h3 px-3"></i>
        </div>
      ) : (
        <>
          <div className="pt-1">
            <i
              className="bi bi-cart cart-img h3 px-3"
              onMouseEnter={() => setDropdown(true)}
            ></i>
          </div>

          {dropdown && (
            <div
              className="cart-dropdown-container"
              ref={dropdownRef}
              onMouseLeave={() => setDropdown(false)}
            >
              {productLoadError ? (
                <div className="text-center">Something went wrong :( </div>
              ) : (
                <div>
                  <div className="cart-dropdown">
                    {cartData.length > 0 ? (
                      cartData.map((cartItem, index) => (
                        <div className="my-3" key={index}>
                          <CartItem
                            cartItem={cartItem}
                            setDropdown={setDropdown}
                            setProductLoadError={setProductLoadError}
                            index={index}
                          />
                        </div>
                      ))
                    ) : (
                      <div className="d-flex justify-content-center font-bold">
                        Your cart seems empty :(
                      </div>
                    )}
                  </div>

                  {cartData.length > 0 ? (
                    <div className="cart-dropdown-action d-flex flex-row justify-content-center gap-3 mt-4">
                      <button
                        className="py-2 px-4 btn btn-sm btn-light"
                        onClick={() => {
                          setDropdown(false);
                          navigate("/cart");
                        }}
                      >
                        Go to cart
                      </button>
                      <button
                        className="py-2 px-4 btn btn-sm btn-dark"
                        onClick={() => {
                          if (!isUserLoggedIn()) {
                            navigate("/login");
                          }

                          navigate("/cart/buy");

                          setDropdown(false);
                        }}
                      >
                        Checkout
                      </button>
                    </div>
                  ) : (
                    <div className="d-flex flex-row justify-content-center mt-5">
                      <button
                        className="py-2 px-4 btn btn-sm btn-dark"
                        onClick={() => {
                          setDropdown(false);
                          navigate("/products");
                        }}
                      >
                        Let's go shopping
                      </button>
                    </div>
                  )}
                </div>
              )}
            </div>
          )}
        </>
      )}
    </>
  );
};

export default CartDropdown;
