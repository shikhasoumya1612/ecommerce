import React from "react";
import "./Order.css";
import { formatDate } from "../../utils/formatDate";
import OrderItem from "../OrderItem/OrderItem";

const Order = ({ order }) => {
  return (
    <div className="container-fluid mt-3 mb-4 order-div">
      <div className="bg-light row order">
        <div className="col-3 my-2">
          <p className="order-details-head">Order Date</p>
          <p className="order-details-body">
            {formatDate(new Date(order?.orderDate))}
          </p>
        </div>

        <div className="col-3 my-2">
          <p className="order-details-head">Total</p>
          <p className="order-details-body">
            ₹ {order?.totalPrice.toLocaleString("en-IN")}
          </p>
        </div>

        <div className="col-3 my-2">
          <p className="order-details-head">Ship to</p>
          <p className="order-details-body">
            {order?.addressDetails
              .substring(
                order?.addressDetails.indexOf("Address - ") +
                  "Address - ".length,
                order?.addressDetails.indexOf(",")
              )
              .trim()}
          </p>
        </div>
      </div>

      <div>
        <div className="order-item-list">
          {order.orderItems.map((orderItem, index) => (
            <div className="order-item-list-item" key={index}>
              <OrderItem orderItem={orderItem} orderDate={order?.orderDate} />
              {index !== order?.orderItems?.length - 1 && (
                <hr className="my-2" />
              )}
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default Order;
