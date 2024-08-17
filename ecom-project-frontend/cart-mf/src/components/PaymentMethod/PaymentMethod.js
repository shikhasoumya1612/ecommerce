import "../Address/Address.css";

const PaymentMethod = ({ paymentMethod, selected }) => {
  return (
    <div className={`address card my-2 ${selected ? "selected" : ""}`}>
      <div className="card-body px-3 py-2">
        <p className="address-title m-0 py-2 ">
          {paymentMethod?.type === "CREDIT_CARD"
            ? "Credit Card"
            : paymentMethod?.type === "DEBIT_CARD"
            ? "Debit Card"
            : "UPI"}
        </p>
        <div className="address-text d-flex flex-wrap gap-2">
          <p>{paymentMethod?.accountId}</p>
        </div>
      </div>
    </div>
  );
};

export default PaymentMethod;
