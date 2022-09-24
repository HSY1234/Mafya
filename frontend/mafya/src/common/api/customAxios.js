let isTokenRefreshing = false;
let refreshSubscribers = [];
let newAccessToken = "";

const onTokenRefreshed = (accessToken) => {
  refreshSubscribers.map((callback) => callback(accessToken));
};

const addRefreshSubscriber = (callback) => {
  refreshSubscribers.push(callback);
};
axios.interceptors.response.use(
  (response) => {
    console.log(response);
    // const nowTime = moment(new Date()).format()
    // const loginDate = localStorage.getItem('expireDate')
    // console.log(moment.duration((nowTime).diff(loginDate).asMilliseconds()))
    return response;
  },
  async (error) => {
    const {
      config,
      response: { data },
    } = error;
    console.log(config, data);
    const code = data.statue;
    const originalRequest = config;
  
    // if (code === 1013) {
    //   // if (error.response.data.message === "") {
    //   if (!isTokenRefreshing) {
    //     isTokenRefreshing = true;
    //     console.log("reissue 전");
    //     const user = JSON.parse(localStorage.getItem("user"));
    //     let accessToken = user ? user.accessToken : null;
    //     // let refreshToken = user ? user.refreshToken : null
    //     axios
    //       .post(
    //         API_URL + "reissue",
    //         { accessToken: accessToken },
    //         { withCredentials: true }
    //       )
    //       .then((response) => {
    //         console.log("reissue 정상");
    //         localStorage.setItem("user", JSON.stringify(response.data.data));
    //         newAccessToken = response.data.data.accessToken;
    //         isTokenRefreshing = false;
    //         originalRequest.headers = {
    //           ...originalRequest.headers,
    //           "X-Auth-Token": newAccessToken,
    //         };
    //         // return axios(originalRequest)
    //         onTokenRefreshed(newAccessToken);
    //       })
    //       .catch((err) => {
    //         console.log("reissue 마무리");
    //         window.location.href = "/";
    //       });
      // }
      const retryOriginalRequest = new Promise((resolve) => {
        addRefreshSubscriber((accessToken) => {
          originalRequest.headers = {
            ...originalRequest.headers,
            "X-Auth-Token": accessToken,
          };
          resolve(axios(originalRequest));
        });
      });
      return retryOriginalRequest;
      // }
    }
    return Promise.reject(error);
  }
);
