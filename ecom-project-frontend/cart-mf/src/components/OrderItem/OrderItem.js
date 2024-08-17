import axios from "axios";
import React, { useState, useEffect } from "react";
import "./OrderItem.css";
import { useNavigate } from "react-router-dom";

const OrderItem = ({ orderItem }) => {
  const navigate = useNavigate();

  return (
    <div className="row order-item">
      <div className="row">
        <div className="col-4">
          <img
            src={orderItem.imgLinks && orderItem?.imgLinks[0]}
            className="order-item-img"
            onClick={() => {
              navigate(`/products/${orderItem.id}`);
            }}
          />
        </div>

        <div className="col-8">
          <p className="order-item-name m-0 p-0">{orderItem?.name}</p>
          <p className="order-item-quantity m-0 my-1 p-0">
            Quantity : {orderItem?.quantity}
          </p>
          <p className="order-item-quantity m-0 my-1 p-0">
            Size : {orderItem?.size}
          </p>
          <p className="order-item-price">
            MRP : ₹ {orderItem?.price?.toLocaleString("en-IN")}
          </p>
        </div>
      </div>
    </div>
  );
};

export default OrderItem;
