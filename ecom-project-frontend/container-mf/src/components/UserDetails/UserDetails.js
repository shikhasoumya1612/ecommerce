import React, { useContext, useEffect, useState } from "react";
import FormComponent from "../FormComponent/FormComponent";
import * as Yup from "yup";
import "./UserDetails.css";

function UserDetails({ userData, setUserData, updateUser }) {
  const [isEditing, setIsEditing] = useState(false);

  const fields = [
    { name: "name", label: "Name", type: "text" },
    { name: "email", label: "Email", type: "email" },
  ];

  const initialValues = {
    name: userData?.name || "",
    email: userData?.email || "",
  };

  const validationSchema = Yup.object({
    name: Yup.string().required("Required"),
    email: Yup.string().email("Invalid email address").required("Required"),
  });

  const onSubmit = (values) => {
    updateUser({
      ...userData,
      name: values.name,
      email: values.email,
    });
    setIsEditing(false);
  };

  return (
    <div className="col-md-9 ">
      <div className="card px-5 user-details">
        <div className="card-title my-3 text-dark">
          User Details
          <span
            className="float-end text-dark"
            onClick={() => setIsEditing(!isEditing)}
          >
            <i className="bi bi-pencil-square" role="button"></i>
          </span>
        </div>

        {isEditing ? (
          <div className="card-body p-0 mx-0 text-dark">
            <FormComponent
              fields={fields}
              initialValues={initialValues}
              validationSchema={validationSchema}
              onSubmit={onSubmit}
              noLabel={true}
              buttonRight={true}
            />
          </div>
        ) : (
          <div className="card-body p-0 ms-2">
            <p className="my-3">{userData?.name}</p>
            <p className="my-3">{userData?.email}</p>
          </div>
        )}
      </div>
    </div>
  );
}
export default UserDetails;
