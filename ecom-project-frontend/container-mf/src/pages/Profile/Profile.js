import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import UserContext from "../../context/UserContext";
import { useContext } from "react";
import { useEffect } from "react";
import isUserLoggedIn from "../../utils/isUserLoggedIn";
import axios from "axios";
import "./Profile.css";
import Modal from "../../components/Modal/Modal";
import { avatars } from "../../utils/avatars";
import { ToastContainer, toast } from "react-toastify";
import UserDetails from "../../components/UserDetails/UserDetails";
import Address from "../../components/Address/Address";
import PaymentMethod from "../../components/PaymentMethod/PaymentMethod";
import Order from "../../components/Order/Order";
import FormComponent from "../../components/FormComponent/FormComponent";
import * as Yup from "yup";

const Profile = () => {
  const { user, setUser } = useContext(UserContext);
  const navigate = useNavigate();

  const [userData, setUserData] = useState();

  const [display, setDisplay] = useState("Orders");

  const [orders, setOrders] = useState([]);

  const [orderError, setOrderError] = useState();
  const [loading, setLoading] = useState(false);

  const fetchOrders = async () => {
    setOrderError(null);
    setLoading(true);
    const userToken = isUserLoggedIn();

    if (!userToken) {
      navigate("/login");
    }

    const config = { headers: { Authorization: `Bearer ${userToken}` } };

    try {
      const response = await axios.get(
        "http://localhost:8070/order/all",
        config
      );

      setOrders(response.data.orders);
    } catch (error) {
      setDisplay("Addresses");
      setOrderError(error);
    }

    setLoading(false);
  };

  const addAddress = async (address) => {
    const userToken = isUserLoggedIn();

    if (!userToken) {
      navigate("/login");
    }

    const config = { headers: { Authorization: `Bearer ${userToken}` } };

    try {
      const response = await axios.post(
        "http://localhost:8080/users/addresses",
        address,
        config
      );

      toast.success(response.data.message);

      const newAddresses = [...user?.addresses, response.data.address];

      setUser((prevState) => ({
        ...prevState,
        addresses: newAddresses,
      }));
    } catch (error) {
      if (error.response) {
        toast.error(error.response.data.message);
      } else {
        toast.error(error.message);
      }
    }
  };

  const addPaymentMethod = async (paymentMethod) => {
    const userToken = isUserLoggedIn();

    if (!userToken) {
      navigate("/login");
    }

    const config = { headers: { Authorization: `Bearer ${userToken}` } };

    try {
      const response = await axios.post(
        "http://localhost:8080/users/paymentMethods",
        paymentMethod,
        config
      );

      toast.success(response.data.message);

      const newPaymentMethods = [
        ...user?.paymentMethods,
        response.data.paymentMethod,
      ];

      setUser((prevState) => ({
        ...prevState,
        paymentMethods: newPaymentMethods,
      }));
    } catch (error) {
      if (error.response) {
        toast.error(error.response.data.message);
      } else {
        toast.error(error.message);
      }
    }
  };

  const fetchUserData = async () => {
    const userToken = isUserLoggedIn();

    if (!userToken) {
      navigate("/login");
    }

    const config = { headers: { Authorization: `Bearer ${userToken}` } };

    try {
      const response = await axios.get(
        "http://localhost:8080/users/my-data",
        config
      );

      setUser(response.data.user);
      setUserData({
        name: response.data.user.name,
        email: response.data.user.email,
        imgLink: response.data.user.imgLink,
      });
    } catch (error) {}
  };

  const [modalState, setModalState] = useState({
    label: "",
    data: [],
    onSubmit: updateUser,
  });

  const updateUser = async (updateUserData) => {
    const userToken = isUserLoggedIn();

    if (!userToken) {
      navigate("/login");
    }

    const config = { headers: { Authorization: `Bearer ${userToken}` } };
    try {
      const response = await axios.patch(
        "http://localhost:8080/users",
        updateUserData,
        config
      );

      setUser(response.data.user);
      setUserData({
        name: response.data.user.name,
        email: response.data.user.email,
        imgLink: response.data.user.imgLink,
      });

      toast.success(response.data.message);
    } catch (error) {
      if (error.response) {
        toast.error(error.response.data.message);
      } else {
        toast.error(error.message);
      }
    }
  };

  const addressFields = [
    { name: "addressName", label: "Address Name", type: "text" },
    { name: "apartment", label: "Apartment", type: "text" },
    { name: "area", label: "Area", type: "text" },
    { name: "landmark", label: "Landmark", type: "text" },
    { name: "city", label: "City", type: "text" },
    { name: "state", label: "State", type: "text" },
    { name: "pincode", label: "Pincode", type: "text" },
  ];

  const paymentMethodfields = [
    {
      name: "type",
      label: "Type",
      type: "select",
      options: [
        { value: 0, label: "Debit Card" },
        { value: 1, label: "Credit Card" },
        { value: 2, label: "UPI" },
      ],
    },
    {
      name: "accountId",
      label: "Account ID",
      type: "text",
    },
  ];

  const paymentMethodValidation = Yup.object({
    type: Yup.number().required("Required"),
    accountId: Yup.string()
      .required("Required")
      .min(10, "Account ID should be greater than 10"),
  });

  const addressValidationSchema = Yup.object().shape({
    addressName: Yup.string().required("Required"),
    apartment: Yup.string().required("Required"),
    area: Yup.string().required("Required"),
    landmark: Yup.string(),
    city: Yup.string().required("Required"),
    state: Yup.string().required("Required"),
    pincode: Yup.string()
      .required("Required")
      .matches(/^[0-9]{6}$/, "Must be exactly 6 digits"),
  });

  useEffect(() => {
    fetchUserData();
    fetchOrders();
  }, []);

  return (
    <div className="container">
      <div className="row">
        <div className="col-md-3">
          <div className="card avatars ">
            <div className="card-body">
              <div className="d-flex flex-row justify-content-center">
                <img
                  src={user?.imgLink}
                  alt="avatar"
                  data-toggle="modal"
                  data-target="#exampleModal"
                  onClick={() => {
                    setModalState((prevState) => ({
                      ...prevState,
                      label: "Choose Avatar",
                    }));
                  }}
                  role="button"
                />
              </div>

              <div className="d-flex flex-row justify-content-center my-2 user-role">
                <p>{user?.role}</p>
              </div>

              <div className="d-flex flex-row justify-content-center my-4">
                <button
                  className="btn btn-sm btn-light px-3"
                  onClick={() => {
                    localStorage.removeItem("token");
                    localStorage.removeItem("cart");
                    localStorage.removeItem("isAdmin");
                    toast.success("Logged out successfully");
                    navigate("/login");
                  }}
                >
                  Logout
                </button>
              </div>
            </div>
          </div>
        </div>

        <UserDetails
          userData={userData}
          setUserData={setUserData}
          updateUser={updateUser}
        />
      </div>

      {loading ? (
        <div className="loader mx-auto mt-3"></div>
      ) : (
        <div>
          <div className="d-flex flex-row gap-2 mt-5">
            {orderError ? (
              <button
                type="button"
                className={`btn btn-${
                  display === "Orders" ? "dark" : "light"
                } btn-sm px-3`}
                onClick={() => setDisplay("Orders")}
                disabled={true}
                data-toggle="tooltip"
                data-placement="top"
                title="Currently not available"
              >
                Orders
              </button>
            ) : (
              <button
                type="button"
                className={`btn btn-${
                  display === "Orders" ? "dark" : "light"
                } btn-sm px-3`}
                onClick={() => setDisplay("Orders")}
              >
                Orders
              </button>
            )}

            <button
              type="button"
              className={`btn btn-${
                display === "Addresses" ? "dark" : "light"
              } btn-sm px-3`}
              onClick={() => setDisplay("Addresses")}
            >
              Addresses
            </button>

            <button
              type="button"
              className={`btn btn-${
                display === "Payment Methods" ? "dark" : "light"
              } btn-sm px-3`}
              onClick={() => setDisplay("Payment Methods")}
            >
              Payment Methods
            </button>
          </div>

          <div className="row mt-5">
            {display === "Orders" && (
              <div className="mt-2">
                {orders
                  ?.sort(
                    (a, b) => new Date(b.orderDate) - new Date(a.orderDate)
                  )
                  .map((order) => (
                    <Order order={order} key={order.id} />
                  ))}
              </div>
            )}

            {display === "Addresses" && (
              <div className="mt-2">
                {user?.addresses.map((address) => (
                  <Address address={address} key={address.id} />
                ))}

                <button
                  className="btn btn-sm btn-light my-3"
                  data-toggle="modal"
                  data-target="#addressModal"
                  onClick={() =>
                    setModalState((prevState) => ({
                      ...prevState,
                      label: "Add address",
                    }))
                  }
                >
                  <i className="bi bi-plus"></i> Add Address
                </button>
              </div>
            )}

            {display === "Payment Methods" && (
              <div className="mt-2">
                {user?.paymentMethods.map((paymentMethod) => (
                  <PaymentMethod
                    paymentMethod={paymentMethod}
                    key={paymentMethod.id}
                  />
                ))}

                <button
                  className="btn btn-sm btn-light my-3"
                  data-toggle="modal"
                  data-target="#paymentMethodModal"
                  onClick={() =>
                    setModalState((prevState) => ({
                      ...prevState,
                      label: "Add payment method",
                    }))
                  }
                >
                  <i className="bi bi-plus"></i> Add Payment Method
                </button>
              </div>
            )}
          </div>
        </div>
      )}

      <Modal
        modalState={modalState}
        setModalState={setModalState}
        onSubmit={() => {}}
        id={"addressModal"}
        hideFooter={true}
        label={"Add address"}
      >
        <div>
          <FormComponent
            fields={addressFields}
            initialValues={{
              addressName: "",
              apartment: "",
              area: "",
              landmark: "",
              city: "",
              state: "",
              pincode: "",
            }}
            validationSchema={addressValidationSchema}
            onSubmit={(values, { setSubmitting, resetForm }) => {
              addAddress(values);
              setSubmitting(false);
              resetForm();

              document.getElementById("addressModal").classList.toggle("show");
              document
                .getElementsByClassName("modal-backdrop")[0]
                .classList.toggle("show");
            }}
            cols={2}
          />
        </div>
      </Modal>

      <Modal
        modalState={modalState}
        setModalState={setModalState}
        onSubmit={() => {}}
        id={"paymentMethodModal"}
        hideFooter={true}
        label={"Add payment method"}
      >
        <div>
          <FormComponent
            fields={paymentMethodfields}
            initialValues={{
              type: 0,
              accountId: "",
            }}
            validationSchema={paymentMethodValidation}
            onSubmit={(values, { setSubmitting, resetForm }) => {
              addPaymentMethod(values);
              setSubmitting(false);
              resetForm();
            }}
            cols={2}
          />
        </div>
      </Modal>

      <Modal
        modalState={modalState}
        setModalState={setModalState}
        onSubmit={() => updateUser(userData)}
        id={"exampleModal"}
        label={"Choose avatar"}
      >
        <div className="avatar-modal mx-2">
          <img src={userData?.imgLink} alt="avatar" width="60px" />
        </div>

        <div className="card avatars-modal my-2">
          <div className="card-body gap-2 d-flex flex-row flex-wrap">
            {avatars.map((data, index) => (
              <img
                key={index}
                src={data}
                alt="avatar"
                className={
                  userData?.imgLink === data
                    ? "border border-dark border-3"
                    : ""
                }
                onClick={() => {
                  setUserData((prevState) => ({
                    ...prevState,
                    imgLink: data,
                  }));
                }}
              />
            ))}
          </div>
        </div>
      </Modal>
    </div>
  );
};

export default Profile;
