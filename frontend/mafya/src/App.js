import React from "react";
import EnterCamera from "./features/webcam/enterCamera";
import { Switch, BrowserRouter, Route } from "react-router-dom";
import studentForm from "./features/Admin/studentForm/studentForm";
import MainPage from "./features/Admin/mainPage/mainPage";
import LoginPage from "./features/login/loginPage";
import StudentMainPage from "./features/student/studentMainPage";
import ExitCamera from "./features/webcam/exitCamera";
import "./App.module.css";
import PublicRoute from "./common/router/PublicRouter";
import PrivateRoute from "./common/router/PrivateRouter";
import AdminRoute from "./common/router/AdminRouter";

function App() {
  return (
    <div>
      <BrowserRouter>
        <Switch>
          <AdminRoute exact path="/enter" component={EnterCamera}></AdminRoute>
          <AdminRoute
            exact
            path="/admin/form"
            component={studentForm}
          ></AdminRoute>
          <AdminRoute exact path="/admin" component={MainPage}></AdminRoute>
          <PublicRoute restricted exact path="/" component={LoginPage} />
          <AdminRoute exact path="/exit" component={ExitCamera} />
          <PrivateRoute exact path="/student" component={StudentMainPage} />
        </Switch>
      </BrowserRouter>
    </div>
  );
}
export default App;
