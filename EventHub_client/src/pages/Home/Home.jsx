import { useOutlet } from "react-router-dom";
import AuthContext from "../../context/authProvider";
import { Link, Outlet, useParams, useLocation } from "react-router-dom";
import { Map } from "./Map/Map";
import useAuth from "../../hooks/useAuth";
import { Button } from "antd";
import styles from "./Home.module.css";
import { useJsApiLoader } from "@react-google-maps/api";
import MenuButton from "./ProfileORlogin/ProfileButton";
import LoginRegisterButton from "./ProfileORlogin/LoginRegisterButton";
import React, { useContext } from "react";

import SearchEvents from "./Search/Search";
import CreateEvent from "./CreateEvent/CreateEvent";
import EventFilter from "./Filter/Filter";
import ProcessingEffect from "../../components/ProcessingEffect/ProcessingEffect";
import MyEvents from "./MyEvents/MyEvents";
import EventInfoSideBar from "./EventInfoSideBar/EventInfoSideBar";
import { useNavigate } from "react-router-dom";

import useLogin from "../../hooks/useLogin";

const MAP_API_KEY = process.env.REACT_APP_GOOGLE_MAPS_API_KEY;


const libraries = ["places"];
const Home = () => {
  //const authenticated = useLogin();
  const { auth, setAuth } = useAuth();
  const location = useLocation();

  const outlet = useOutlet();

  const { isLoaded } = useJsApiLoader({
    id: "google-map-script",
    googleMapsApiKey: MAP_API_KEY,
    libraries,
  });

  return (
    <div className={styles.Home}>
      {isLoaded ? (
        <>
          <Map  />

          {auth.token ? <MenuButton /> : <LoginRegisterButton />}

          <SearchEvents />
          <CreateEvent />
          <EventFilter />
          <MyEvents />

          {outlet}
        </>
      ) : (
        <ProcessingEffect />
      )}
    </div>
  );
};

export { Home };
