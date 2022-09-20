import { useState } from "react";
import styles from "./loginPage.module.css";

const LoginPage = () => {
  const [userCode, setUserCode] = useState("");
  const [password, setPassword] = useState("");
  const userCodeHandler = (event) => {
    setUserCode(event.target.value);
  };

  const passwordHandler = (event) => {
    setPassword(event.target.value);
  };

  const formIsVaild = userCode && password;

  const loginHandler = (event) => {
    event.preventDefault();
    const userForm = { userCode, password };
    // 이후 AXIOS와 이걸로 분기 결정
    console.log(userForm);
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
