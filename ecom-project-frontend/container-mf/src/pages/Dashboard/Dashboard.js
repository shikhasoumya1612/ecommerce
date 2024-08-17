import React, { useEffect, useState } from "react";
import styles from "./Dashboard.module.css";

import { useNavigate } from "react-router-dom";

// shoes
import shoe1 from "../../assets/shoe-1.png";
import shoe2 from "../../assets/shoe-2.png";
import shoe3 from "../../assets/shoe-3.png";

// famous-people
import famous1 from "../../assets/famous-1.jpg";
import famous2 from "../../assets/famous-2.jpeg";
import famous3 from "../../assets/famous-3.png";

import about from "../../assets/Nike-cartoon.png";

import axios from "axios";
import isAdmin from "../../utils/isAdmin";
import ErrorComponent from "../../components/ErrorComponent/ErrorComponent";

const Dashboard = () => {
  const navigate = useNavigate();
  const [categories, setCategories] = useState([]);

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState();

  const shoes = [
    { name: "COMFORT", color: "#8f1ca3", img: shoe1 },
    { name: "STYLE", color: "#00FFFF", img: shoe3 },
    { name: "DURABLITY", color: "#FF69B4", img: shoe2 },
  ];

  const [selectedShoe, setSelectedShoe] = useState(shoes[0]);

  const fetchAllCategories = async () => {
    try {
      setLoading(true);
      setError();
      const response = await axios.get(
        "http://localhost:8060/products/categories"
      );
      setCategories(response.data);
    } catch (error) {
      setError(error);
    }
    setLoading(false);
  };

  const navigateToProducts = (categoryId) => {
    navigate(`/products`, {
      state: { categoryId: categoryId, showFilters: true },
    });
  };

  useEffect(() => {
    fetchAllCategories();

    const interval = setInterval(() => {
      setSelectedShoe((prevShoe) => {
        const currentIndex = shoes.indexOf(prevShoe);
        const nextIndex = (currentIndex + 1) % shoes.length;
        return shoes[nextIndex];
      });
    }, 3000);

    return () => clearInterval(interval);
  }, []);

  return (
    <>
      {/* Hero */}
      <div
        className={`${styles.bg} ${styles["shoe-container"]} ${
          styles[selectedShoe.name]
        } py-3`}
      >
        <div className={` ${styles.content} mx-5`}>
          <div>
            <div className="text-center" style={{ marginTop: "160px" }}>
              <img
                src={selectedShoe.img}
                alt="shoe"
                className={`mt-5 ${styles.shoe}`}
              />
            </div>
          </div>
        </div>
      </div>

      {/* About */}

      <div className="py-5 container-fluid">
        <div className={`pt-5 mx-5 ${styles.players}`}>
          <p>You are what you wear</p>
        </div>
        <div className="py-5 row">
          <div className="py-5 col-7 text-center">
            <img src={about} className={`${styles["about-img"]}`} />
          </div>
          <div className={`${styles["about-description"]} col-4`}>
            <p>
              Welcome to a world where style meets comfort. Our curated
              collection of footwear spans from trendy sneakers to elegant
              heels, ensuring that you find the perfect pair for every occasion.
              We prioritize quality, affordability, and an easy shopping
              experience. Step into fashion with confidence, because we believe
              that the right shoes can elevate your entire day! Whether you’re
              conquering the urban jungle, hitting the gym, or stepping out for
              a special occasion, we’ve got you covered. Our commitment to
              excellence ensures that you not only look good but also feel great
              in every step. Explore our range and discover the perfect fit for
              your unique style. Happy shoe shopping!
            </p>
          </div>
        </div>
      </div>

      {/* Collection */}

      <div className="pt-5 container-fluid ">
        <div className={`pt-5 mx-5 ${styles.players}`}>
          <p>Our Collections</p>
        </div>

        {error ? (
          <ErrorComponent status={error.response?.data} />
        ) : (
          <>
            {loading ? (
              <div className="loader mx-auto mt-3"></div>
            ) : (
              <div className="d-flex flex-wrap justify-content-around py-5">
                {categories.map((category, index) => (
                  <div key={category.id} className={`py-3`}>
                    <div className={`${styles["category-card"]}`}>
                      <img
                        src={category.imgLink}
                        alt="category"
                        className={`${styles["category-card-img"]} rounded`}
                      />
                      <p className={`${styles["category-card-title"]}`}>
                        {category.name}
                      </p>
                      <div
                        className={`${styles["category-card-overlay"]}`}
                      ></div>
                      <div className={`${styles["category-card-button"]}`}>
                        <button onClick={() => navigateToProducts(category.id)}>
                          Explore <i className="ps-2 bi bi-arrow-right"></i>
                        </button>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </>
        )}
      </div>

      {/* Athletes */}
      <div className="my-5 pt-5">
        <div className={`pt-5 mx-5 ${styles.players}`}>
          <p>Athletes</p>
        </div>
        <div className={`my-5 d-flex flex-row justify-content-around `}>
          <div className={`${styles["famous-slots"]}`}>
            <div className="text-center">
              <img
                src={famous1}
                alt="famous"
                className={`text-center  ${styles["players-img"]} rounded`}
                width="350px"
              />
            </div>

            <p className="mt-2 text-muted px-3">SoleBliss x Neymar</p>
            <p className={`${styles["player-title"]} px-3`}>The WONDER-kid</p>
            <p className={`${styles["player-description"]} px-3`}>
              Neymar signed with SoleBliss in 2005, when he was only 13 years
              old, and became one of the most prominent faces of the brand. He
              wore SoleBliss boots, jerseys, and accessories, and starred in
              several SoleBliss campaigns and commercials. He also had his own
              signature line of SoleBliss products.
            </p>
          </div>

          <div className={`${styles["famous-slots"]}`}>
            <div className="text-center">
              <img
                src={famous2}
                alt="famous"
                className={`text-center ${styles["players-img"]} rounded`}
                width="350px"
              />
            </div>
            <p className="mt-2 text-muted px-3">SoleBliss x LeBron</p>
            <p className={`${styles["player-title"]} px-3`}>The Chosen One</p>
            <p className={`${styles["player-description"]} px-3`}>
              LeBron James has been with SoleBliss since 2003, when he signed a
              seven-year, $90 million deal as a rookie. Since then, he has
              become one of the most influential and lucrative athletes for
              SoleBliss, earning an estimated $32 million per year from the
              brand
            </p>
          </div>

          <div className={`${styles["famous-slots"]}`}>
            <div className="text-center">
              <img
                src={famous3}
                alt="famous"
                className={`text-center ${styles["players-img"]} rounded`}
                width="350px"
              />
            </div>

            <p className="mt-2 text-muted px-3">SoleBliss x Woods</p>
            <p className={`${styles["player-title"]} px-3`}>The Silent Tiger</p>
            <p className={`${styles["player-description"]} px-3`}>
              Tiger Woods signed with SoleBliss in 1996, when he turned pro, and
              became the first golfer to have a major endorsement deal with the
              brand. He helped SoleBliss establish its presence and reputation
              in the golf industry, wearing SoleBliss golf shoes, apparel, and
              equipment, and promoting SoleBliss Golf products and services.
            </p>
          </div>
        </div>
      </div>
    </>
  );
};

export default Dashboard;
