import "./Address.css";

const Address = ({ address, selected }) => {
  return (
    <div className={`address card my-2 ${selected ? "selected" : ""}`}>
      <div className="card-body px-3 py-2">
        <p className="address-title m-0 py-2 ">{address?.addressName}</p>
        <div className="address-text d-flex flex-wrap gap-2">
          <p>{address?.apartment},</p>
          <p>{address?.area ? address?.area + "," : ""}</p>
          <p>{address?.landmark ? address?.landmark + "," : ""}</p>
          <p>{address?.city ? address?.city + "," : ""}</p>
          <p>{address?.state ? address?.state : ""} - </p>
          <p>{address?.pincode}</p>
        </div>
      </div>
    </div>
  );
};

export default Address;
