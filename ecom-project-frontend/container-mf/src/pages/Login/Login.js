import React from "react";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { ToastContainer, toast } from "react-toastify";
import * as Yup from "yup";
import axios from "axios";
import { mergeCart } from "../../utils/cartFunctions";
import { useContext } from "react";
import UserContext from "../../context/UserContext";
import FormComponent from "../../components/FormComponent/FormComponent.js";
import "./Login.css";
import login from "../../assets/login.jpg";

const Login = () => {
  const navigate = useNavigate();

  const { user, setUser } = useContext(UserContext);

  const fields = [
    { name: "email", label: "Email Address *", type: "email" },
    { name: "password", label: "Password *", type: "password" },
  ];

  const initialValues = { email: "", password: "" };

  const validationSchema = Yup.object({
    email: Yup.string().email("Invalid email address").required("Required"),
    password: Yup.string()
      .min(8, "Password is too short - should be 8 chars minimum.")
      .matches(/[a-z]/, "Password must contain at least one lowercase char.")
      .matches(/[A-Z]/, "Password must contain at least one uppercase char.")
      .matches(/[0-9]/, "Password must contain at least one number.")
      .matches(
        /[@$!%*#?&]/,
        "Password must contain at least one special char (@,!,#, etc)."
      )
      .required("No password provided."),
  });

  const handleLogin = async (values) => {
    try {
      const response = await axios.post(
        "http://localhost:8080/users/login",
        values
      );
      setUser(response.data.user);
      localStorage.setItem("token", response.data.token);
      localStorage.setItem("isAdmin", response.data.user.role === "ADMIN");
      if (response.data.token) {
        mergeCart();
        navigate("/");
      }
      toast.success(response.data.message, {
        toastId: "login-success",
      });
    } catch (error) {
      toast.error(error.response ? error.response.data.message : error, {
        toastId: "login-error",
      });
    }
  };

  const onSubmit = (values, { setSubmitting }) => {
    handleLogin(values);
    setSubmitting(false);
  };

  return (
    <div className="container-fluid px-5">
      <div className="card login-card bg-light">
        <div className="card-body row">
          <div className="login-img col-6">
            <img src={login} alt="login" />
          </div>
          <div className="col-6 pe-5">
            <p className="welcome-message mt-5 mb-0 pb-0">
              Welcome to SoleBliss
            </p>
            <p className="text-muted mb-5 register-message">
              Here for the first time?{" "}
              <span role="button" onClick={() => navigate("/register")}>
                Register here
              </span>
            </p>
            <FormComponent
              fields={fields}
              initialValues={initialValues}
              validationSchema={validationSchema}
              onSubmit={onSubmit}
            />
          </div>
        </div>
      </div>
    </div>
  );
};

export default Login;
