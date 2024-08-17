import React, { useState } from "react";
import "./Modal.css";

function Modal({
  modalState,
  setModalState,
  onSubmit,
  id,
  hideFooter,
  children,
  label,
}) {
  return (
    <div
      className="modal fade"
      id={id}
      tabIndex="-1"
      role="dialog"
      aria-labelledby="exampleModalLabel"
      aria-hidden="true"
    >
      <div className="modal-dialog" role="document">
        <div className="modal-content">
          <div className="modal-header">
            <p className="modal-title" id="exampleModalLabel">
              {label}
            </p>
          </div>
          <div className="modal-body">{children}</div>
          {!hideFooter && (
            <div className="modal-footer">
              <button
                type="button"
                className="btn btn-sm btn-light px-3"
                data-dismiss="modal"
              >
                Close
              </button>

              <button
                type="button"
                className="btn btn-sm btn-dark"
                data-dismiss="modal"
                onClick={onSubmit}
              >
                Save changes
              </button>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
export default Modal;
