import classes from "./adminHeader.module.css";
import { Link, useHistory } from "react-router-dom";
import axios from "axios";
import { API_URL } from "../../../common/api";

const AdminHeader = () => {
  const history = useHistory();
  const logoutHandler = (event) => {
    axios
      .get(API_URL + "student/logout/")
      .then((res) => {
        delete axios.defaults.headers.common[`accessToken`];
        window.localStorage.clear();
        history.push("/");
      })
      .catch((err) => {
        console.log(err);
      });
  };
  return (
    <nav className={classes.navbar}>
      <Link to="/admin" className={classes.navbar__logo}>
        SSAFY
      </Link>
      <ul className={classes.navbar__menu}>
        <Link to="/admin" className={classes.navbar__item}>
          Main
        </Link>
        <Link to="/admin/form" state={null} className={classes.navbar__item}>
          Create
        </Link>
        <span className={classes.navbar__item} onClick={logoutHandler}>
          Logout
        </span>
        <Link to="/enter" className={classes.navbar__item}>
          입실
        </Link>
        <Link to="/exit" className={classes.navbar__item}>
          퇴실
        </Link>
        {/* <span className={classes.navbar__item}>Logout</span> */}
      </ul>
    </nav>
  );
};

export default AdminHeader;
