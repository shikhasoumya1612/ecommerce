import React, { useState } from "react";
import { Formik, Field, Form, ErrorMessage } from "formik";
import "./FormComponent.css";

const FormComponent = ({
  fields,
  initialValues,
  validationSchema,
  onSubmit,
  noLabel,
  noButton,
  buttonRight,
  cols = 1,
}) => {
  const [showPassword, setShowPassword] = useState(false);

  const handleShowPassword = () => {
    setShowPassword(!showPassword);
  };

  const colClass = `col-${12 / cols}`;

  return (
    <Formik
      initialValues={initialValues}
      validationSchema={validationSchema}
      onSubmit={onSubmit}
      enableReinitialize
    >
      {(formik) => (
        <Form className="">
          <div className="row">
            {fields.map((field, index) => (
              <div className={`form-group ${colClass} px-3`} key={field.name}>
                {!noLabel && (
                  <label htmlFor={field.name} className="form-label text-dark">
                    {field.label}
                  </label>
                )}
                {field.type === "select" ? (
                  <Field
                    as="select"
                    name={field.name}
                    className={`form-control my-2 ${
                      formik.touched[field.name] && formik.errors[field.name]
                        ? "is-invalid"
                        : ""
                    }`}
                    disabled={field.disabled}
                  >
                    {field.options.map((option) => (
                      <option key={option.value} value={option.value}>
                        {option.label}
                      </option>
                    ))}
                  </Field>
                ) : (
                  <Field
                    name={field.name}
                    type={
                      field.type === "password"
                        ? showPassword
                          ? "text"
                          : "password"
                        : field.type
                    }
                    className={`form-control my-2 ${
                      formik.touched[field.name] && formik.errors[field.name]
                        ? "is-invalid"
                        : ""
                    }`}
                    disabled={field.disabled}
                  />
                )}
                {field.type === "password" && (
                  <div className="form-check">
                    <input
                      type="checkbox"
                      className="form-check-input"
                      id="showPasswordCheckbox"
                      onClick={handleShowPassword}
                    />
                    <label
                      className="form-check-label ms-2 text-dark"
                      htmlFor="showPasswordCheckbox"
                    >
                      Show password
                    </label>
                  </div>
                )}

                <ErrorMessage
                  name={field.name}
                  component="div"
                  className="invalid-feedback"
                />
              </div>
            ))}
          </div>

          {!noButton && (
            <div
              className={`d-flex flex-row ${
                buttonRight ? "justify-content-end" : ""
              }`}
            >
              <button
                type="button"
                data-dismiss="modal"
                className="btn btn-sm btn-dark mt-3"
                onClick={(event) => {
                  onSubmit(formik.values, {
                    setSubmitting: formik.setSubmitting,
                    resetForm: formik.resetForm,
                  });
                }}
                disabled={!formik.isValid || formik.isSubmitting}
              >
                Submit
              </button>
            </div>
          )}
        </Form>
      )}
    </Formik>
  );
};

export default FormComponent;
