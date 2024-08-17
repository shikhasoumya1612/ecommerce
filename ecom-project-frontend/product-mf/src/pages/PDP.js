import { useEffect, useState } from "react";
import "../styles/pdp.css";
import { useNavigate, useParams } from "react-router-dom";
import axios from "axios";
import Skeleton from "react-loading-skeleton";
import "react-loading-skeleton/dist/skeleton.css";
import { toast } from "react-toastify";
import Confetti from "react-confetti";
import { anonymousAddToCart } from "../utils/cartFunctions";
import ErrorComponent from "../components/ErrorComponent/ErrorComponent";
import { Rating } from "react-simple-star-rating";

const PDP = () => {
  const params = useParams();
  const [product, setProduct] = useState();
  const [selectedImgLink, setSelectedImgLink] = useState();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [selectedSize, setSelectedSize] = useState(4);

  const handleMouseOver = (link) => {
    setSelectedImgLink(link);
  };

  const fetchProduct = async (token) => {
    setError(null);
    try {
      setLoading(true);
      const productId = params.id;

      const config = { headers: { Authorization: `Bearer ${token}` } };

      const response = await axios.get(
        `http://localhost:8060/products/products/${productId}`
      );

      setProduct(response.data.product);
      setSelectedImgLink(response.data.product.imgLinks[1]);

      // Traverse over the reviews array of the product
      if (token) {
        console.log(token);
        let reviews = response.data.product.reviews;
        let userPromises = reviews.map((review) =>
          axios.get(`http://localhost:8080/users/${review.userId}`, config)
        );
        let users = await Promise.all(userPromises);

        for (let i = 0; i < reviews.length; i++) {
          reviews[i].user = users[i].data.user;
          delete reviews[i].userId;
        }
        response.data.product.reviews = reviews;
      }

      setProduct(response.data.product);
    } catch (error) {
      setError(error);
    } finally {
      setLoading(false);
    }
  };

  const addToCart = async (token) => {
    try {
      const config = { headers: { Authorization: `Bearer ${token}` } };

      const addToCartBody = {
        productId: product.id,
        quantity: 1,
        size: `${selectedSize} UK`,
      };

      const response = await axios.post(
        `http://localhost:8090/cart/addToCart`,
        addToCartBody,
        config
      );
      toast.success(response.data.message);
    } catch (error) {
      if (error.response) {
        toast.error(error.data.response.message);
      } else {
        toast.error(error.message);
      }
    }
  };

  useEffect(() => {
    fetchProduct(localStorage.getItem("token") || "");
  }, [params]);

  return (
    <>
      {error ? (
        <ErrorComponent status={404} />
      ) : (
        <div className="container-fluid">
          {loading ? (
            <div className="pdp row mx-5">
              <div className="col-md-7">
                <div className="row">
                  <div className="col-md-1"></div>
                  <div className="col-md-1"></div>
                  <div className="col-md-1">
                    {[1, 2, 3, 4, 5].map(() => (
                      <div className={`pdp-side-img my-2`}>
                        <Skeleton width="100%" height="100%" />
                      </div>
                    ))}
                  </div>
                  <div className="col-md-9 px-5 py-2">
                    <div className="pdp-main-img-skeleton">
                      <Skeleton width="100%" height="100%" />
                    </div>
                  </div>
                </div>
              </div>
              <div className="pdp-details col-md-4 col-xl-3">
                <p className="name">
                  <Skeleton width="300px" height="30px" />
                </p>
                <p className="text-muted">
                  <Skeleton width="200px" height="30px" />
                </p>

                <p className="price">
                  <Skeleton width="400px" height="60px" />
                </p>

                <p className="description">
                  <Skeleton width="400px" height="20px" count={5} />
                </p>

                <Skeleton width="400px" height="30px" />
              </div>
            </div>
          ) : (
            <div className="pdp row mx-5">
              <div className="col-md-7">
                <div className="row">
                  <div className="col-md-1"></div>
                  <div className="col-md-1"></div>
                  <div className="col-md-1">
                    {product?.imgLinks?.map(
                      (link, index) =>
                        index >= 1 && (
                          <img
                            key={index}
                            src={link}
                            alt="product"
                            className={`pdp-side-img my-2 ${
                              link === selectedImgLink ? "opacity-50" : ""
                            }`}
                            onMouseOver={(e) => handleMouseOver(link)}
                          />
                        )
                    )}
                  </div>
                  <div className="col-md-9 px-5 py-2">
                    <img
                      src={selectedImgLink}
                      alt="product"
                      className="pdp-main-img"
                    />
                  </div>
                </div>
              </div>
              <div className="pdp-details col-md-4">
                {product?.quantity === 0 && (
                  <p className="out-of-stock">
                    This product is currently out of stock.
                  </p>
                )}
                <p className="name">{product?.name}</p>
                <p className="text-muted">
                  {!product.gender || product?.gender === "Unisex"
                    ? "Unisex"
                    : product?.gender + "'s category"}
                </p>

                <p className="price">
                  MRP : ₹ {product?.price?.toLocaleString("en-IN")}
                </p>

                <p className="description">{product?.description}</p>

                <h6 className="mt-5">Select your size</h6>
                <div className="product-sizes d-flex flex-row flex-wrap gap-2">
                  {[4, 4.5, 5, 6, 6.5, 7, 8, 9, 10, 11].map((data) => (
                    <p
                      className={`border m-0 ${
                        selectedSize === data ? "border-black" : ""
                      }`}
                      role="button"
                      onClick={() => setSelectedSize(data)}
                      key={data}
                    >
                      {data} UK
                    </p>
                  ))}
                </div>

                <button
                  className={`mt-5 py-3 add-to-cart-btn ${
                    product?.quantity === 0 ? "out-of-stock-btn" : ""
                  }`}
                  onClick={() => {
                    // setDropdown(true);
                    if (localStorage.getItem("token"))
                      addToCart(localStorage.getItem("token"));
                    else {
                      anonymousAddToCart(product.id, 1, `${selectedSize} UK`);
                    }
                  }}
                  disabled={product?.quantity === 0}
                >
                  Add to cart
                </button>
              </div>
            </div>
          )}

          <div className="row my-5">
            <div className="col-md-2"></div>
            <div className="product-attributes col-md-5">
              <p className="head my-3 ">Attributes</p>
              {product?.attributes.map((attribute, index) => (
                <div className="product-attribute row " key={index}>
                  <div className="attribute-name col-md-3 py-1">
                    {attribute.name}
                  </div>
                  <div className="attribute-value col-md-7 py-1 ms-2 ">
                    {attribute.value}
                  </div>
                </div>
              ))}
            </div>
            {localStorage.getItem("token") && (
              <div
                className="product-reviews col-md-4 pe-5"
                style={{
                  fontFamily: "Poppins",
                  color: "black",
                  backgroundColor: "white",
                }}
              >
                <p className="head my-3">
                  Reviews {`(${product?.reviews?.length})`}
                </p>
                <div className="product-reviews-list pe-2">
                  {product?.reviews.map((review, index) => (
                    <div className="card mb-3 product-review " key={index}>
                      <div className="card-body">
                        <div className="d-flex justify-content-between ">
                          <div className="d-flex flex-row gap-4">
                            <img
                              src={review.user?.imgLink}
                              className="img-fluid rounded-circle review-img"
                              alt="User"
                            />

                            <h5 className="card-title mt-2">
                              {review.user?.name}
                            </h5>
                          </div>
                          <div className="mt-2">
                            <Rating
                              initialValue={review.rating}
                              readonly
                              allowFraction
                              size={"20px"}
                            />
                          </div>
                        </div>
                        <div className="ms-5">
                          <p className="review-description">
                            {review.description}
                          </p>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>
        </div>
      )}
    </>
  );
};

export default PDP;
