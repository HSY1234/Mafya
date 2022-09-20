import React, { useEffect } from "react";
import { Route, Redirect, useHistory } from "react-router-dom";
import { isLogin } from "../api/isLogin";

export default function PrivateRoute({ component: Component, ...rest }) {
  // }

  return (
    <Route
      {...rest}
      render={(props) =>
        isLogin() ? <Component {...props} /> : <Redirect to="/login" />
      }
    />
  );
}
