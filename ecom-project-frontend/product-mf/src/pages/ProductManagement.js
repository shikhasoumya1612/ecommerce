import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import isAdmin from "../utils/isAdmin";
import isUserLoggedIn from "../utils/isUserLoggedIn";
import { ToastContainer, toast } from "react-toastify";
import axios from "axios";
import DataTable, { createTheme } from "react-data-table-component";
import Modal from "../components/Modal/Modal";

import FormComponent from "../components/FormComponent/FormComponent";
import * as Yup from "yup";
import ErrorComponent from "../components/ErrorComponent/ErrorComponent";

const ProductManagement = () => {
  const [products, setProducts] = useState([]);

  const [modalState, setModalState] = useState({
    label: "",
    data: [],
  });

  const navigate = useNavigate();
  const [selectedRow, setSelectedRow] = useState();

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchAllProducts = async () => {
    setLoading(true);
    setError(null);
    const userToken = isUserLoggedIn();

    if (!userToken) {
      navigate("/login");
    }

    const config = { headers: { Authorization: `Bearer ${userToken}` } };
    try {
      const response = await axios.get(
        "http://localhost:8060/products/products",
        config
      );

      setProducts(response.data);
    } catch (error) {
      setError(error);
    }

    setLoading(false);
  };

  const updateProduct = async (values) => {
    const userToken = isUserLoggedIn();

    if (!userToken) {
      navigate("/login");
    }

    const admin = isAdmin();

    if (!admin) {
      navigate("/home");
    }

    const config = { headers: { Authorization: `Bearer ${userToken}` } };
    try {
      const response = await axios.patch(
        `http://localhost:8060/products/product/${selectedRow.id}`,
        {
          name: values.name,
          quantity: values.quantity,
        },
        config
      );
      toast.success(response.data.message);
      fetchAllProducts();
    } catch (error) {
      if (error.response) {
        toast.error(error.response.data.message);
      } else {
        toast.error(error.message);
      }
    }
  };

  const deleteProduct = async (row) => {
    const userToken = isUserLoggedIn();

    if (!userToken) {
      navigate("/login");
    }

    const admin = isAdmin();

    if (!admin) {
      navigate("/home");
    }

    const config = { headers: { Authorization: `Bearer ${userToken}` } };
    try {
      const response = await axios.delete(
        `http://localhost:8060/products/product/${row.id}`,
        config
      );

      fetchAllProducts();
    } catch (error) {
      if (error.response) {
        toast.error(error.response.data.message);
      } else {
        toast.error(error.message);
      }
    }
  };

  const columns = [
    {
      name: "ID",
      selector: (row) => row.id,
      sortable: true,
    },
    {
      name: "Name",
      selector: (row) => row.name,
      sortable: true,
    },
    {
      name: "Quantity",
      selector: (row) => row.quantity,
    },
    {
      name: "Actions",
      cell: (row) => (
        <div className="d-flex flex-row gap-4">
          <i
            className="bi bi-eye-fill"
            role="button"
            data-toggle="modal"
            data-target="#viewModal"
            onClick={() => {
              navigate(`/products/${row.id}`);
            }}
          ></i>
          <i
            className="bi bi-pencil-fill"
            role="button"
            data-toggle="modal"
            data-target="#editProductModal"
            onClick={() => {
              setModalState((prevState) => ({
                ...prevState,
                label: "Edit product data",
              }));

              setSelectedRow(row);
            }}
          ></i>

          <i
            className="bi bi-trash-fill"
            role="button"
            data-toggle="modal"
            data-target="#deleteModal"
            onClick={() => {
              setModalState((prevState) => ({
                ...prevState,
                label: "Delete product data",
              }));

              setSelectedRow(row);
            }}
          ></i>
        </div>
      ),
    },
  ];

  //   const userFields = [
  //     { name: "name", label: "Name", type: "text", disabled: true },
  //     { name: "email", label: "Email", type: "text", disabled: true },
  //     { name: "imgLink", label: "Image", type: "text", disabled: true },
  //     { name: "role", label: "Role", type: "text", disabled: true },
  //   ];

  const productEditFields = [
    { name: "name", label: "Name", type: "text" },
    { name: "quantity", label: "Quantity", type: "number" },
  ];

  const productValidationSchema = Yup.object().shape({
    name: Yup.string()
      .min(3, "Name should be at least 3 characters")
      .required("Required"),

    quantity: Yup.number()
      .min(0, "Quantity cannot be negative")
      .required("Required"),
  });

  useEffect(() => {
    fetchAllProducts();
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
            <div className="my-5 mx-3">
              <div className="mx-4 border border-1">
                <DataTable
                  columns={columns}
                  data={products}
                  pagination={products.length > 10}
                  striped={true}
                  highlightOnHover
                  responsive
                />
              </div>

              <Modal
                modalState={modalState}
                setModalState={setModalState}
                onSubmit={() => {}}
                id={"editProductModal"}
                hideFooter={true}
                label={"Edit Product Details"}
              >
                <div>
                  <FormComponent
                    fields={productEditFields}
                    initialValues={{
                      name: selectedRow?.name || "",
                      quantity: selectedRow?.quantity || "",
                    }}
                    validationSchema={productValidationSchema}
                    onSubmit={(values, { setSubmitting, resetForm }) => {
                      updateProduct(values);
                      setSubmitting(false);
                      resetForm();

                      document
                        .getElementById("editProductModal")
                        .classList.toggle("show");
                      document
                        .getElementsByClassName("modal-backdrop")[0]
                        .remove();
                    }}
                    cols={2}
                  />
                </div>
              </Modal>

              <Modal
                modalState={modalState}
                setModalState={setModalState}
                onSubmit={() => {}}
                id={"deleteModal"}
                hideFooter={true}
                label={"Delete Product"}
              >
                <div>
                  <p>Are you really sure you want to delete this product ?</p>
                </div>

                <div className="d-flex flex-row justify-content-end gap-2">
                  <button
                    className="btn btn-sm btn-light px-3"
                    data-dismiss="modal"
                  >
                    Cancel
                  </button>
                  <button
                    className="btn btn-sm btn-dark px-3"
                    data-dismiss="modal"
                    onClick={() => {
                      deleteProduct(selectedRow);
                    }}
                  >
                    Confirm
                  </button>
                </div>
              </Modal>
            </div>
          )}
        </>
      )}
    </>
  );
};

export default ProductManagement;
