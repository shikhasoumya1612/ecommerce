import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import isAdmin from "../../utils/isAdmin";
import isUserLoggedIn from "../../utils/isUserLoggedIn";
import { ToastContainer, toast } from "react-toastify";
import axios from "axios";
import DataTable, { createTheme } from "react-data-table-component";
import "./UserManagement.css";
import Modal from "../../components/Modal/Modal";

import FormComponent from "../../components/FormComponent/FormComponent";
import * as Yup from "yup";

const UserManagement = () => {
  const [users, setUsers] = useState([]);

  const [modalState, setModalState] = useState({
    label: "",
    data: [],
  });

  const navigate = useNavigate();
  const [selectedRow, setSelectedRow] = useState();

  const fetchAllUsers = async () => {
    const userToken = isUserLoggedIn();

    if (!userToken) {
      navigate("/login");
    }

    const config = { headers: { Authorization: `Bearer ${userToken}` } };
    try {
      const response = await axios.get("http://localhost:8080/users", config);

      setUsers(
        response.data.users.map((user) => {
          return {
            email: user.email,
            role: user.role,
            name: user.name,
            id: user.id,

            imgLink: user.imgLink,
          };
        })
      );
    } catch (error) {
      if (error.response) {
        toast.error(error.response.data.message);
      } else {
        toast.error(error.message);
      }
    }
  };

  const updateUserData = async (values) => {
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
        `http://localhost:8080/users/${selectedRow.id}`,
        {
          name: values.name,
          email: values.email,
          imgLink: values.imgLink,
        },
        config
      );

      fetchAllUsers();
    } catch (error) {
      if (error.response) {
        toast.error(error.response.data.message);
      } else {
        toast.error(error.message);
      }
    }
  };

  const deleteUser = async (row) => {
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
        `http://localhost:8080/users/${row.id}`,
        config
      );

      toast.success(response.data.message);

      fetchAllUsers();
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
    },
    {
      name: "Name",
      selector: (row) => row.name,
      sortable: true,
    },
    {
      name: "Email",
      selector: (row) => row.email,
    },
    {
      name: "Role",
      selector: (row) => row.role,
    },
    {
      name: "Image Link",
      selector: (row) => row.imgLink,
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
              setModalState((prevState) => ({
                ...prevState,
                label: "View user data",
              }));

              setSelectedRow(row);
            }}
          ></i>
          <i
            className="bi bi-pencil-fill"
            role="button"
            data-toggle="modal"
            data-target="#editModal"
            onClick={() => {
              setModalState((prevState) => ({
                ...prevState,
                label: "Edit user data",
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
                label: "Delete user data",
              }));

              setSelectedRow(row);
            }}
          ></i>
        </div>
      ),
    },
  ];

  const userFields = [
    { name: "name", label: "Name", type: "text", disabled: true },
    { name: "email", label: "Email", type: "text", disabled: true },
    { name: "imgLink", label: "Image", type: "text", disabled: true },
    { name: "role", label: "Role", type: "text", disabled: true },
  ];

  const userEditFields = [
    { name: "name", label: "Name", type: "text" },
    { name: "email", label: "Email", type: "text" },
    { name: "imgLink", label: "Image", type: "text" },
    { name: "role", label: "Role", type: "text", disabled: true },
  ];

  const userValidationSchema = Yup.object().shape({
    name: Yup.string()
      .min(3, "Name should be at least 3 characters")
      .required("Required"),

    email: Yup.string().email("Invalid email address").required("Required"),
    imgLink: Yup.string().required("Required"),
  });

  useEffect(() => {
    fetchAllUsers();
  }, []);

  return (
    <div className="mx-3">
      <div className="mx-4 border border-1 my-5">
        <DataTable
          columns={columns}
          data={users}
          pagination={users.length > 10}
          striped={true}
          highlightOnHover
          responsive
        />
      </div>

      <Modal
        modalState={modalState}
        setModalState={setModalState}
        onSubmit={() => {}}
        id={"viewModal"}
        hideFooter={true}
        label={"View user data"}
      >
        <div>
          <FormComponent
            fields={userFields}
            initialValues={{
              name: selectedRow?.name || "",
              email: selectedRow?.email || "",
              imgLink: selectedRow?.imgLink || "",
              role: selectedRow?.role || "",
            }}
            validationSchema={userValidationSchema}
            onSubmit={(values, { setSubmitting, resetForm }) => {
              addAddress(values);
              setSubmitting(false);
              resetForm();
            }}
            cols={2}
            noButton={true}
          />
        </div>
      </Modal>

      <Modal
        modalState={modalState}
        setModalState={setModalState}
        onSubmit={() => {}}
        id={"editModal"}
        hideFooter={true}
        label={"Edit user data"}
      >
        <div>
          <FormComponent
            fields={userEditFields}
            initialValues={{
              name: selectedRow?.name || "",
              email: selectedRow?.email || "",
              imgLink: selectedRow?.imgLink || "",
              role: selectedRow?.role || "",
            }}
            validationSchema={userValidationSchema}
            onSubmit={(values, { setSubmitting, resetForm }) => {
              updateUserData(values);
              setSubmitting(false);
              resetForm();
              document.getElementById("editModal").classList.toggle("show");
              document.getElementsByClassName("modal-backdrop")[0].remove();
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
        label={"Delete user"}
      >
        <div>
          <p>Are you really sure you want to delete this user ?</p>
        </div>

        <div className="d-flex flex-row justify-content-end gap-2">
          <button className="btn btn-sm btn-light px-3" data-dismiss="modal">
            Cancel
          </button>
          <button
            className="btn btn-sm btn-dark px-3"
            data-dismiss="modal"
            onClick={() => {
              document.getElementById("deleteModal").classList.toggle("show");
              document.getElementsByClassName("modal-backdrop")[0].remove();
              deleteUser(selectedRow);
            }}
          >
            Confirm
          </button>
        </div>
      </Modal>
    </div>
  );
};

export default UserManagement;
