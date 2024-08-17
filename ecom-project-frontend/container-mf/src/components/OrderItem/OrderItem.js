import axios from "axios";
import React, { useState, useEffect } from "react";
import "./OrderItem.css";
import { useNavigate } from "react-router-dom";
import { formatDate } from "../../utils/formatDate";
import isUserLoggedIn from "../../utils/isUserLoggedIn";
import { toast } from "react-toastify";
import Modal from "../Modal/Modal";
import { Rating } from "react-simple-star-rating";

const OrderItem = ({ orderItem, orderDate }) => {
  const navigate = useNavigate();

  const [modalState, setModalState] = useState({
    label: "",
    data: [],
  });

  const deliveryDate = new Date(
    new Date(orderDate).setDate(new Date(orderDate).getDate() + 7)
  );

  const [review, setReview] = useState({
    rating: 5,
    description: "",
  });

  const handleRating = (rating) => {
    setReview((prevState) => ({
      ...prevState,
      rating: rating,
    }));
  };

  const handleDescription = (event) => {
    setReview((prevState) => ({
      ...prevState,
      description: event.target.value,
    }));
  };

  const addReview = async (productId) => {
    try {
      if (!isUserLoggedIn()) {
        return;
      }
      const config = {
        headers: { Authorization: `Bearer ${isUserLoggedIn()}` },
      };
      const response = await axios.post(
        `http://localhost:8060/products/product/${orderItem?.productId}/review`,
        review,
        config
      );

      toast.success(response.data.message);
    } catch (error) {
      toast.success(
        error.response ? error.response.data.message : error.message
      );
    }
  };

  return (
    <div className="row order-item my-3">
      <div className="row">
        <div className="col-2">
          <img
            src={orderItem?.img}
            className="order-item-img"
            onClick={() => {
              navigate(`/products/${orderItem?.productId}`);
            }}
          />
        </div>

        <div className="col-8">
          <p className="order-item-name m-0 p-0">
            {deliveryDate < new Date() ? "Delivered on " : "Expected by "}
            <span className="order-delivery">{formatDate(deliveryDate)}</span>
          </p>
          <p className="order-item-quantity m-0 mt-1 p-0 text-muted">
            {orderItem?.productName}
          </p>
          <p className="order-item-quantity m-0 p-0 mb-1 text-muted">
            Size : {orderItem?.size}
          </p>

          <p className="order-item-quantity m-0 my-1 mt-2 p-0 text-muted">
            Quantity : {orderItem?.quantity}
          </p>
        </div>

        <div className="col-2">
          <button
            className="btn btn-sm btn-light"
            data-toggle="modal"
            data-target={`#addReviewModal${orderItem?.id}`}
            onClick={() => {
              setReview({
                rating: 5,
                description: "",
              });

              setModalState((prevState) => ({
                ...prevState,
                label: "Add review",
              }));
            }}
          >
            Add a review
          </button>
        </div>
      </div>

      <Modal
        modalState={modalState}
        setModalState={setModalState}
        onSubmit={() => {}}
        id={`addReviewModal${orderItem?.id}`}
        hideFooter={true}
        label={"Add Review"}
      >
        <Rating
          initialValue={review.rating}
          size="25px"
          onClick={handleRating}
          allowFraction
        />
        <textarea
          value={review.description}
          onChange={handleDescription}
          className="form-control mt-3"
          rows="4"
          placeholder="Write your review here..."
        />

        <div className="d-flex flex-row gap-3 justify-content-end mt-3 px-3">
          <button
            className="btn btn-sm btn-light"
            data-dismiss="modal"
            onClick={() => {
              setReview({
                rating: 5,
                description: "",
              });
            }}
          >
            Cancel
          </button>

          <button
            className="btn btn-sm btn-dark px-3"
            data-dismiss="modal"
            onClick={() => {
              addReview(orderItem?.productId);
            }}
          >
            Save
          </button>
        </div>
      </Modal>
    </div>
  );
};

export default OrderItem;
