import React, { useState } from "react";
import { useNavigate } from "react-router-dom";

function DeliveryModal({ deliveryModal, setDeliveryModal }) {
  const navigate = useNavigate();

  return (
    <>
      <div className="modal" id="deliveryModal" tabIndex="-1">
        <div className="modal-dialog">
          <div className="modal-content">
            <div className="modal-header">
              <h5 className="modal-title">Delivery Information</h5>
            </div>
            <div className="modal-body">
              Your order will be delivered by 7 days from now.
            </div>
            <div className="modal-footer">
              <button
                type="button"
                className="btn btn-sm btn-light"
                data-bs-dismiss="modal"
                onClick={() => setShow(false)}
              >
                Close
              </button>
              <button
                type="button"
                className="btn btn-sm btn-dark"
                onClick={() => navigate("/profile")}
              >
                See my orders
              </button>
            </div>
          </div>
        </div>
      </div>
    </>
  );
}
export default DeliveryModal;
