import React from "react";
import EnterCamera from "./features/webcam/enterCamera";
import { Switch, BrowserRouter, Route } from "react-router-dom";
import studentForm from "./features/Admin/studentForm/studentForm";
import MainPage from "./features/Admin/mainPage/mainPage";
import LoginPage from "./features/login/loginPage";
import StudentMainPage from "./features/student/studentMainPage";
import ExitCamera from "./features/webcam/exitCamera";
import "./App.module.css";
function App() {
  return (
    <div>
      <BrowserRouter>
        <Switch>
          <Route exact path="/enter" component={EnterCamera}></Route>
          <Route exact path="/admin/form" component={studentForm}></Route>
          <Route exact path="/admin" component={MainPage}></Route>
          <Route exact path="/login" component={LoginPage} />
          <Route exact path="/exit" component={ExitCamera} />
          <Route exact path="/student" component={StudentMainPage} />
        </Switch>
      </BrowserRouter>
    </div>
  );
}
export default App;
