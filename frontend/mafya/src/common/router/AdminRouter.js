import React, { useEffect } from "react";
import { Route, Redirect, useHistory } from "react-router-dom";
import { isAdmin } from "../api/isLogin";

export default function PrivateRoute({ component: Component, ...rest }) {
  return (
    <Route
      {...rest}
      render={(props) =>
        isAdmin() ? <Component {...props} /> : <Redirect to="/login" />
      }
    />
  );
}
