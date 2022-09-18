import React from "react"
import Camera from "./features/webcam/camera"
import { Switch, BrowserRouter, Route } from "react-router-dom"
import studentForm from "./features/Admin/studentForm/studentForm"
import MainPage from "./features/Admin/mainPage/mainPage"
function App() {
  return (
    <div>
      <BrowserRouter>
        <Switch>
          <Route exact path="/" component={Camera}></Route>
          <Route exact path="/admin/form" component={studentForm}></Route>
          <Route exact path="/admin" component={MainPage}></Route>
        </Switch>
      </BrowserRouter>
    </div>
  )
}
export default App
