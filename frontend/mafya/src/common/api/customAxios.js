import axios from "axios";

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
    const code = data.status;
    const originalRequest = config;

    if (code === 408) {
      // if (error.response.data.message === "") {
      if (!isTokenRefreshing) {
        isTokenRefreshing = true;
        console.log("reissue 전");
        const token = localStorage.getItem("token");
        axios
          .get(API_URL + "/token/reissue", { withCredentials: true })
          .then((response) => {
            console.log("reissue 정상");
            localStorage.setItem("user", response.data.accessToken);
            newAccessToken = response.dataaccessToken;
            isTokenRefreshing = false;
            originalRequest.headers = {
              ...originalRequest.headers,
              accessToken: newAccessToken,
            };
            // return axios(originalRequest)
            onTokenRefreshed(newAccessToken);
          })
          .catch((err) => {
            console.log("reissue 마무리");
          });
      }
      const retryOriginalRequest = new Promise((resolve) => {
        addRefreshSubscriber((accessToken) => {
          originalRequest.headers = {
            ...originalRequest.headers,
            accessToken: accessToken,
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
