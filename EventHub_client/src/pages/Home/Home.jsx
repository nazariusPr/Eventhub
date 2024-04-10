import { useContext, useEffect, useState } from "react";
import AuthContext from "../../context/authProvider";
import { Link, Outlet, useParams } from "react-router-dom";
import { Map } from "./Map/Map";
import useAuth from "../../hooks/useAuth";
import { Button } from "antd";
import styles from "./Home.module.css";
import { useJsApiLoader } from "@react-google-maps/api";
import MenuButton from "./ProfileORlogin/ProfileButton";
import LoginRegisterButton from "./ProfileORlogin/LoginRegisterButton";

import SearchEvents from "./Search/Search";
import CreateEvent from "./CreateEvent/CreateEvent";
import EventFilter from "./Filter/Filter";
import FilteredEvents from "./Filter/FilteredEvents";
import MyEvents from "./MyEvents/MyEvents";
import EventInfoSideBar from "./EventInfoSideBar/EventInfoSideBar";
import { jwtDecode } from "jwt-decode";

const MAP_API_KEY = process.env.REACT_APP_GOOGLE_MAPS_API_KEY;

const defaultCenter = {
  lat: 49.83826,
  lng: 24.02324,
};
const libraries = ["places"];
const Home = () => {
  const { auth, setAuth } = useAuth();

  const { ownerId, eventId } = useParams();

  const { isLoaded } = useJsApiLoader({
    id: "google-map-script",
    googleMapsApiKey: MAP_API_KEY,
    libraries,
  });

  const [user, setUser] = useState(null);

  useEffect(() => {
    setUser(jwtDecode(localStorage.getItem("token")));
  }, []);

  return (
    <div className={styles.Home}>
      {isLoaded ? (
        <>
          <Map center={defaultCenter} />

          {auth.token ? (
            user && <MenuButton user={user} />
          ) : (
            <LoginRegisterButton />
          )}

          <SearchEvents />
          <CreateEvent />
          <EventFilter />
          <MyEvents />

          {ownerId && eventId && (
            <EventInfoSideBar ownerId={ownerId} eventId={eventId} />
          )}
        </>
      ) : (
        <h1>Loading</h1>
      )}
    </div>
  );
};

export { Home };
