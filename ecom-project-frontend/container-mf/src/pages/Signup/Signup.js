import React from "react";
import FormComponent from "../../components/FormComponent/FormComponent";
import * as Yup from "yup";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import { useContext } from "react";
import { ToastContainer } from "react-toastify";
import UserContext from "../../context/UserContext";
import "../Login/Login.css";
import login from "../../assets/login.jpg";

const Signup = () => {
  const navigate = useNavigate();

  const { user, setUser } = useContext(UserContext);

  const fields = [
    { name: "name", label: "Name *", type: "text" },
    { name: "email", label: "Email Address *", type: "email" },
    { name: "password", label: "Password *", type: "password" },
  ];

  const initialValues = { name: "", email: "", password: "" };

  const validationSchema = Yup.object({
    name: Yup.string()
      .min(3, "Name should atleast have 3 characters")
      .required("Required"),
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

  const handleSignup = async (values) => {
    try {
      const response = await axios.post(
        "http://localhost:8080/users/register",
        values
      );

      const loginResponse = await axios.post(
        "http://localhost:8080/users/login",
        { email: values.email, password: values.password }
      );

      localStorage.setItem("token", loginResponse.data.token);
      setUser(loginResponse.data.user);

      if (loginResponse.data.token) {
        navigate("/");
      }
      toast.success(response.data.message, { toastId: "signup-success" });
    } catch (error) {
      if (error.response)
        toast.error(error.response.data.message, { toastId: "signup-error" });
      else toast.error(error.message);
    }
  };

  const onSubmit = (values, { setSubmitting }) => {
    handleSignup(values);
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
              Already created an account ?
              <span role="button" onClick={() => navigate("/login")}>
                Login here
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

export default Signup;
