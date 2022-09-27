import classes from "./adminHeader.module.css";
import { Link, useHistory } from "react-router-dom";
import axios from "axios";
import { API_URL } from "../../../common/api";
import axios1 from "../../../common/api/axios";

const AdminHeader = () => {
  const history = useHistory();
  const logoutHandler = (event) => {
    axios1
      .get(API_URL + "student/logout/", {
        headers: {
          accessToken: window.localStorage.getItem("token"),
        },
      })
      .then((res) => {
        window.localStorage.clear();
        history.push("/");
      })
      .catch((err) => {
        console.log(err);
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
          프로님 버전
        </Link>
        <Link to="/exit" className={classes.navbar__item}>
          컨설턴트님 버전
        </Link>
        {/* <span className={classes.navbar__item}>Logout</span> */}
      </ul>
    </nav>
  );
};

export default AdminHeader;
