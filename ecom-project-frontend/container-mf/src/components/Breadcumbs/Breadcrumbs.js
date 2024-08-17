import { useLocation, Link } from "react-router-dom";
import "./Breadcrumbs.css";
import axios from "axios";
import { useState, useEffect } from "react";

export default function Breadcrumbs() {
  const location = useLocation();

  const [product, setProduct] = useState();
  const [crumbs, setCrumbs] = useState([]);

  const fetchProductById = async (id) => {
    try {
      const response = await axios.get(
        `http://localhost:8060/products/products/${id}`
      );
      setProduct(response.data.product);
      return response.data.product.name;
    } catch (error) {
      return "";
    }
  };

  useEffect(() => {
    let currentLink = "";
    const crumbs = location.pathname
      .split("/")
      .filter((crumb) => crumb !== "")
      .map(async (crumb) => {
        currentLink += `/${crumb}`;

        const productPattern = /^\/products\/[a-zA-Z0-9]+$/;
        const isProductId = productPattern.test(currentLink);

        if (isProductId) {
          crumb = await fetchProductById(crumb);
        }

        return (
          <li className="breadcrumb-item" key={crumb}>
            <Link to={currentLink}>{crumb}</Link>
          </li>
        );
      });

    Promise.all(crumbs).then(setCrumbs);
  }, [location]);

  return (
    <>
      {crumbs.length > 0 && (
        <nav className="breadcrumb-bar mt-5">
          <ol className="ms-5 breadcrumb">
            <li className="breadcrumb-item" key={"home"}>
              <Link to={"/"}>Home</Link>
            </li>
            {crumbs}
          </ol>
        </nav>
      )}
    </>
  );
}
