import axios from "axios";
import React, { useState, useEffect } from "react";
import "./CartItem.css";
import { useNavigate } from "react-router-dom";
import isUserLoggedIn from "../../utils/isUserLoggedIn";
import { anonymousAddToCart } from "../../utils/cartFunctions";
import { toast } from "react-toastify";

const CartItem = ({ cartItem, fetchCartData, changeCartQuantity, index }) => {
  const navigate = useNavigate();

  const handleAddToCart = async () => {
    const token = isUserLoggedIn();
    if (!token) {
      anonymousAddToCart(cartItem.id, 1, cartItem.size);
      changeCartQuantity(index, cartItem.cartQuantity + 1);
    } else {
      try {
        const config = { headers: { Authorization: `Bearer ${token}` } };

        const addToCartBody = {
          productId: cartItem.id,
          quantity: 1,
          size: cartItem.size,
        };

        const response = await axios.post(
          `http://localhost:8090/cart/addToCart`,
          addToCartBody,
          config
        );

        changeCartQuantity(index, cartItem.cartQuantity + 1);
      } catch (error) {
        if (error.response) {
          toast.error(error.data.response.message);
        } else {
          toast.error(error.message);
        }
      }
    }
  };

  const handleRemoveFromCart = async (number) => {
    const token = isUserLoggedIn();
    if (!token) {
      anonymousRemoveFromCart(cartItem.id, number, cartItem.size);
      changeCartQuantity(index, cartItem.cartQuantity - number);
    } else {
      try {
        const config = { headers: { Authorization: `Bearer ${token}` } };

        const removeFromCartBody = {
          productId: cartItem.id,
          quantity: number,
          size: cartItem.size,
        };

        const response = await axios.post(
          `http://localhost:8090/cart/removeFromCart`,
          removeFromCartBody,
          config
        );

        changeCartQuantity(index, cartItem.cartQuantity - number);
      } catch (error) {
        if (error.response) {
          toast.error(error.data.response.message);
        } else {
          toast.error(error.message);
        }
      }
    }
  };

  return (
    <div className="row cart-item">
      <div
        className={`row col-8 ${
          cartItem.quantity === 0 ? "out-of-stock-full" : ""
        }`}
      >
        <div className="col-4">
          <img
            src={cartItem.imgLinks && cartItem?.imgLinks[0]}
            className="cart-item-img"
            onClick={() => {
              navigate(`/products/${cartItem.id}`);
            }}
          />
        </div>
        <div className="col-8">
          <p className="cart-item-name m-0 my-2 p-0">{cartItem?.name}</p>
          <p className="cart-item-quantity mb-0 pb-0">Men's Category</p>
          <p className="cart-item-quantity ">{`Size : ${cartItem.size}`}</p>
          <p className="cart-item-quantity m-0 p-0 my-1">
            <button
              disabled={cartItem.quantity === 0}
              onClick={handleAddToCart}
            >
              <i class="bi bi-plus"></i>
            </button>
            <span className="mx-2">{cartItem?.cartQuantity}</span>

            <button
              disabled={cartItem.quantity === 0}
              onClick={() => handleRemoveFromCart(1)}
            >
              <i class="bi bi-dash"></i>
            </button>
          </p>

          {cartItem.quantity === 0 && (
            <p className="cart-item-quantity m-0 p-0 mt-2 out-of-stock">
              Out of stock
            </p>
          )}
        </div>
      </div>

      <div
        className={`col-3 mt-2 d-flex justify-content-end ${
          cartItem.quantity === 0 ? "out-of-stock-full" : ""
        }`}
      >
        <p className="cart-item-price">
          MRP : ₹ {cartItem?.price?.toLocaleString("en-IN")}
        </p>
      </div>

      <div className="row">
        <div className="col-10"></div>
        <div className="col-2">
          <i
            className="bi bi-trash h6 ps-3"
            role="button"
            onClick={() => handleRemoveFromCart(cartItem.cartQuantity)}
          ></i>
        </div>
      </div>

      <hr className="mt-4" />
    </div>
  );
};

export default CartItem;
