import React from "react";
import Camera from './features/Camera/webcam'
import { Switch, BrowserRouter, Route } from "react-router-dom";
import AdminCreatePage from "./features/Admin/adminCreate/adminCreatePage";
import AdminPage from "./features/Admin/adminPage";
function App() {
  return (
    <div>
      <BrowserRouter>
        <Switch>
          <Route exact path="/" component={Camera}></Route>
          <Route
            exact
            path="/admin/addstudent"
            component={AdminCreatePage}
          ></Route>
          <Route exact path="/admin" component={AdminPage}></Route>
        </Switch>
      </BrowserRouter>
    </div>
  );
}
export default App;
