import classes from "./student.module.css";
import { Link } from "react-router-dom";

const StudentHeader = () => {
  return (
    <nav className={classes.navbar}>
      <Link to="/student" className={classes.navbar__logo}>
        SSAFY
      </Link>
      <ul className={classes.navbar__menu}>
        <Link to="/admin" className={classes.navbar__item}>
          Main
        </Link>
        {/* 향후에 드랍다운으로 처리합시다.  할꺼는 패스워드 바꾸기, 로그아웃*/}
        {/* <span className={classes.navbar__item}>Logout</span> */}
      </ul>
    </nav>
  );
};

export default StudentHeader;
