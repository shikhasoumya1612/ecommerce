import React, { useState, useEffect } from "react";
import axios from "axios";
import isUserLoggedIn from "../../utils/isUserLoggedIn";
import "./Order.css";
import { useNavigate } from "react-router-dom";
import OrderItem from "../../components/OrderItem/OrderItem";
import Address from "../../components/Address/Address";
import PaymentMethod from "../../components/PaymentMethod/PaymentMethod";
import { ToastContainer, toast } from "react-toastify";
import Confetti from "react-confetti";
import ErrorComponent from "../../components/ErrorComponent/ErrorComponent";

const Order = () => {
  const navigate = useNavigate();

  const [orderState, setOrderState] = useState({
    initCardData: [],
    cartData: [],
    addresses: [],
    paymentMethods: [],
    addressDropdown: false,
    paymentMethodDropdown: false,
  });

  const [selectedAddress, setSelectedAddress] = useState();
  const [selectedPaymentMethod, setSelectedPaymentMethod] = useState();

  const [delivery, setDelivery] = useState(false);
  const [confetti, setConfetti] = useState(false);

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState();

  const fetchCartData = async () => {
    const userToken = isUserLoggedIn();

    setLoading(true);
    setError(null);

    if (!userToken) {
      const cart = localStorage.getItem("cart");
      if (!cart) {
        localStorage.setItem("cart", JSON.stringify([]));
      } else {
        try {
          const cartItems = JSON.parse(cart);

          if (cartItems.length < 0) {
            navigate("/cart");
          }

          setOrderState((prevState) => ({
            ...prevState,
            initCardData: cartItems,
          }));

          const products = await Promise.all(
            cartItems.map(async (item) => {
              const productResponse = await axios.get(
                `http://localhost:8060/products/products/${item.productId}`
              );
              return {
                ...productResponse.data.product,
                quantity: item.quantity,
                size: item.size,
              };
            })
          );

          setOrderState((prevState) => ({ ...prevState, cartData: products }));
        } catch (error) {
          setError(error);
        }
      }
    } else {
      try {
        const config = { headers: { Authorization: `Bearer ${userToken}` } };

        const response = await axios.get("http://localhost:8090/cart", config);
        const cartItems = response.data.cart.cartItems;
        if (cartItems.length == 0) {
          navigate("/cart");
        }

        setOrderState((prevState) => ({
          ...prevState,
          initCardData: cartItems,
        }));

        const products = await Promise.all(
          cartItems.map(async (item) => {
            const productResponse = await axios.get(
              `http://localhost:8060/products/products/${item.productId}`
            );
            return {
              ...productResponse.data.product,
              quantity: item.quantity,
              size: item.size,
            };
          })
        );

        setOrderState((prevState) => ({ ...prevState, cartData: products }));
      } catch (errorData) {
        setError(errorData);
      }
    }

    setLoading(false);
  };

  function formatDate(date) {
    const options = { day: "numeric", month: "short" };
    return new Intl.DateTimeFormat("en-IN", options).format(date);
  }

  const fetchAddress = async () => {
    try {
      const config = {
        headers: { Authorization: `Bearer ${isUserLoggedIn()}` },
      };

      const response = await axios.get(
        "http://localhost:8080/users/addresses",
        config
      );

      setOrderState((prevState) => ({
        ...prevState,
        addresses: response.data.addresses,
      }));

      setSelectedAddress(
        response.data.addresses.length > 0 ? response.data.addresses[0] : null
      );
    } catch (errorData) {}
  };

  const fetchPaymentMethods = async () => {
    try {
      const config = {
        headers: { Authorization: `Bearer ${isUserLoggedIn()}` },
      };

      const response = await axios.get(
        "http://localhost:8080/users/paymentMethods",
        config
      );

      setOrderState((prevState) => ({
        ...prevState,
        paymentMethods: response.data.paymentMethods,
      }));

      setSelectedPaymentMethod(
        response.data.paymentMethods.length > 0
          ? response.data.paymentMethods[0]
          : {}
      );
    } catch (errorData) {}
  };

  const createOrder = async () => {
    try {
      const config = {
        headers: { Authorization: `Bearer ${isUserLoggedIn()}` },
      };

      const createOrderBody = {
        orderItemList: orderState.initCardData,
        addressId: selectedAddress.id,
        paymentMethodId: selectedPaymentMethod.id,
      };

      const response = await axios.post(
        "http://localhost:8070/order/createOrder",
        createOrderBody,
        config
      );

      const clearCartResponse = await axios.get(
        "http://localhost:8090/cart/clearCart",
        config
      );

      setDelivery(true);

      setConfetti(true);
      setTimeout(() => {
        setConfetti(false);
      }, 5000);
    } catch (error) {
      toast.error(
        error.response ? error.response?.data.message : error.message
      );
    }
  };

  useEffect(() => {
    if (!isUserLoggedIn()) navigate("/login");

    fetchCartData();
    fetchAddress();
    fetchPaymentMethods();
  }, []);

  return (
    <>
      {error ? (
        <ErrorComponent status={error.response?.status} />
      ) : (
        <>
          {loading ? (
            <div className="loader mx-auto mt-5"></div>
          ) : (
            <div className="container-fluid mt-5 px-0">
              {!delivery ? (
                <>
                  <div className="row mx-5">
                    <div className="col-8 order-items">
                      <div className="mb-5 me-3">
                        <div className="d-flex flex-row justify-content-between">
                          <p className="title m-0 p-0">Select your address</p>
                          <i
                            className={`bi bi-chevron-${
                              orderState.addressDropdown ? "up" : "down"
                            }`}
                            role="button"
                            onClick={() =>
                              setOrderState((prevState) => ({
                                ...prevState,
                                addressDropdown: !orderState.addressDropdown,
                              }))
                            }
                          ></i>
                        </div>
                        <div className="order-address ">
                          {!orderState.addressDropdown ? (
                            <Address
                              address={selectedAddress}
                              selected={true}
                            />
                          ) : (
                            orderState.addresses.map((address, index) => (
                              <div
                                onClick={() => {
                                  setSelectedAddress(address);
                                  setOrderState((prevState) => ({
                                    ...prevState,
                                    addressDropdown:
                                      !orderState.addressDropdown,
                                  }));
                                }}
                                key={index}
                              >
                                <Address
                                  address={address}
                                  selected={selectedAddress?.id === address.id}
                                />
                              </div>
                            ))
                          )}
                        </div>
                      </div>

                      <div className="mb-5 me-3">
                        <div className="d-flex flex-row justify-content-between">
                          <p className="title m-0 p-0">
                            Select your payment method
                          </p>
                          <i
                            className={`bi bi-chevron-${
                              orderState.paymentMethodDropdown ? "up" : "down"
                            }`}
                            role="button"
                            onClick={() =>
                              setOrderState((prevState) => ({
                                ...prevState,
                                paymentMethodDropdown:
                                  !orderState.paymentMethodDropdown,
                              }))
                            }
                          ></i>
                        </div>
                        <div className="order-address ">
                          {!orderState.paymentMethodDropdown ? (
                            <PaymentMethod
                              paymentMethod={selectedPaymentMethod}
                              selected={true}
                            />
                          ) : (
                            orderState.paymentMethods.map(
                              (paymentMethod, index) => (
                                <div
                                  onClick={() => {
                                    setSelectedPaymentMethod(paymentMethod);
                                    setOrderState((prevState) => ({
                                      ...prevState,
                                      paymentMethodDropdown:
                                        !orderState.paymentMethodDropdown,
                                    }));
                                  }}
                                  key={index}
                                >
                                  <PaymentMethod
                                    paymentMethod={paymentMethod}
                                    selected={
                                      selectedPaymentMethod?.id ===
                                      paymentMethod.id
                                    }
                                  />
                                </div>
                              )
                            )
                          )}
                        </div>
                      </div>
                    </div>
                    {orderState.cartData.length > 0 && (
                      <div className="order-summary col-4">
                        <p className="title">Order Summary</p>
                        {[
                          {
                            name: "Subtotal Price",
                            value: orderState.cartData?.reduce(
                              (total, item) => {
                                return total + item.price * item.quantity;
                              },
                              0
                            ),
                          },

                          {
                            name: "Tax + Handling Charges",
                            value:
                              orderState.cartData?.reduce((total, item) => {
                                return total + item.price * item.quantity;
                              }, 0) * 0.1,
                          },
                        ].map((data, index) => (
                          <div
                            className="d-flex flex-row justify-content-between order-summary-items"
                            key={index}
                          >
                            <p>{data.name}</p>
                            <p>
                              ₹{" "}
                              {parseFloat(data.value.toFixed(2)).toLocaleString(
                                "en-IN"
                              )}
                            </p>
                          </div>
                        ))}

                        <hr />
                        {[
                          {
                            name: "Total Price",
                            value:
                              orderState.cartData?.reduce((total, item) => {
                                return total + item.price * item.quantity;
                              }, 0) * 1.1,
                          },
                        ].map((data, index) => (
                          <div
                            className="d-flex flex-row justify-content-between pt-2"
                            key={index}
                          >
                            <p className="order-summary-final">{data.name}</p>
                            <p className="order-summary-final">
                              ₹{" "}
                              {parseFloat(data.value.toFixed(2)).toLocaleString(
                                "en-IN"
                              )}
                            </p>
                          </div>
                        ))}
                        <hr />

                        <div className="pt-2">
                          <div className="order-item-list">
                            {orderState.cartData?.length > 0 ? (
                              orderState.cartData.map((orderItem, i) => (
                                <div className="my-3" key={i}>
                                  <OrderItem orderItem={orderItem} />

                                  {i !== orderState.cart?.length - 1 && (
                                    <hr className="mt-4" />
                                  )}
                                </div>
                              ))
                            ) : (
                              <div className="d-flex justify-content-center font-bold">
                                Your cart seems empty :(
                              </div>
                            )}
                          </div>
                        </div>
                      </div>
                    )}
                  </div>

                  <div className="checkout py-3 d-flex flex-row justify-content-end border border-top pe-5">
                    <button
                      className="btn btn-dark btn-md"
                      onClick={createOrder}
                    >
                      Proceed to Checkout
                    </button>
                  </div>
                </>
              ) : (
                <div className="container-fluid d-flex justify-content-center">
                  {confetti && (
                    <Confetti
                      width={window.innerWidth * 0.95}
                      height={window.innerHeight}
                    />
                  )}
                  <div className="delivery-card card">
                    <div className="card-title my-4">
                      <div className="d-flex flex-row justify-content-center">
                        <p>
                          Congratulations !! Your order has been successfully
                          placed
                        </p>
                      </div>
                    </div>
                    <div className="card-body">
                      <div className="row">
                        <div className="col-2"></div>
                        <div className="col-4 pt-3">
                          <p>
                            Your items will be delivered by{" "}
                            <span className="delivery-date">
                              {formatDate(
                                new Date().setDate(new Date().getDate() + 7)
                              )}
                            </span>
                          </p>
                          <button
                            className="btn btn-sm btn-dark mb-1"
                            onClick={() => navigate("/products")}
                          >
                            Continue Shopping
                          </button>
                        </div>
                        <div className="col-4 pt-3">
                          <div className="pt-2">
                            <div className="order-item-list">
                              {orderState.cartData?.length > 0 ? (
                                orderState.cartData.map((orderItem, i) => (
                                  <div className="my-3" key={i}>
                                    <OrderItem orderItem={orderItem} />

                                    {i !== orderState.cart?.length - 1 && (
                                      <hr className="mt-4" />
                                    )}
                                  </div>
                                ))
                              ) : (
                                <div className="d-flex justify-content-center font-bold">
                                  Your cart seems empty :(
                                </div>
                              )}
                            </div>
                          </div>
                        </div>

                        <div className="col-2"></div>
                      </div>
                      <div className="d-flex flex-row justify-content-center"></div>
                    </div>
                  </div>
                </div>
              )}
            </div>
          )}
        </>
      )}
    </>
  );
};

export default Order;
