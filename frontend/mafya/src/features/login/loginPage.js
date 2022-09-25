import axios from "axios";
import { useState } from "react";
import { useHistory } from "react-router-dom";
import { API_URL } from "../../common/api";
import { login } from "./loginAPI";
import styles from "./loginPage.module.css";

const LoginPage = () => {
  const history = useHistory();
  const [userCode, setUserCode] = useState("");
  const [password, setPassword] = useState("");
  const userCodeHandler = (event) => {
    setUserCode(event.target.value);
  };

  const passwordHandler = (event) => {
    setPassword(event.target.value);
  };

  const formIsVaild = userCode && password;

  const loginHandler = async (event) => {
    event.preventDefault();
    const userForm = { userCode, password };
    axios
      .post(API_URL + "student/login/", userForm, {
        headers: { "Content-Type": "application/json" },
      })
      .then((res) => {
        if (res.data.resultCode === 0) {
          window.localStorage.setItem("userCode", userCode);
          const token = res.data.accessToken;
          window.localStorage.setItem("token", token);
          if (res.data.isManager === "Y") {
            window.localStorage.setItem("isManager", res.data.isManager);
            window.localStorage.setItem("classCode", res.data.classCode);

            history.push("/admin");
          } else {
            window.localStorage.setItem("teamCode", res.data.teamCode);

            history.push("/student");
          }
        } else {
          alert("로그인 에러");
        }
      })

      .catch((error) => {
        console.log(error);
      });
  };
  return (
    <div>
      <header>
        <h2>Login</h2>
      </header>
      <form onSubmit={loginHandler}>
        <div class={styles.input_box}>
          <input
            onChange={userCodeHandler}
            type="text"
            id="userCode"
            placeholder="아이디"
          />
          <label htmlFor="userCode">ID</label>
        </div>
        <div class={styles.input_box}>
          <input
            onChange={passwordHandler}
            type="password"
            id="password"
            placeholder="비밀번호"
          />
          <label htmlFor="password">비밀번호</label>
        </div>
        <div>
          <button type="submit" disabled={!formIsVaild}>
            로그인
          </button>
        </div>
      </form>
    </div>
  );
};

export default LoginPage;
