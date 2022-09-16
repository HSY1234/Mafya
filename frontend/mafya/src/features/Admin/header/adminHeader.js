import classes from "./adminHeader.module.css";
import { Link } from "react-router-dom";
const AdminHeader = () => {
  return (
    <nav className={classes.navbar}>
      <Link to={"/admin"} className={classes.navbar__logo}>
        SSAFY
      </Link>
      <ul className={classes.navbar__menu}>
        <Link to={"/admin"} className={classes.navbar__item}>
          Main
        </Link>
        <Link to={"/admin/form"} className={classes.navbar__item}>
          Create
        </Link>
        {/* <span className={classes.navbar__item}>Logout</span> */}
      </ul>
    </nav>
  );
};

export default AdminHeader;
