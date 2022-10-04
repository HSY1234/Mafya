import classes from "./adminHeader.module.css"
import { Link, useHistory } from "react-router-dom"
import axios from "axios"
import { API_URL } from "../../../common/api"
import { useState, useRef } from "react"
import axios1 from "../../../common/api/axios"

const AdminHeader = () => {
  const history = useHistory()
  const [userExcel, setUserExcel] = useState(null)
  const logoutHandler = (event) => {
    axios1
      .get(API_URL + "student/logout/", {
        headers: {
          accessToken: window.localStorage.getItem("token"),
        },
      })
      .then((res) => {
        window.localStorage.clear()
        history.push("/")
      })
      .catch((err) => {
        console.log(err)
        console.log(err)
      })
  }
  const changeUserExcelHandler = (event) => {
    event.preventDefault()
    let file = event.target.files[0]
    setUserExcel(file)
  }

  const submitUserExcelHandler = (event) => {
    event.preventDefault()
    let formData = new FormData()
    formData.set("uploadUserFile", userExcel)
    axios1
      .post(API_URL + "excel/upload", formData, {
        headers: {
          "Content-Type": "multipart/form-data",
          accessToken: window.localStorage.getItem("token"),
          // "Access-Control-Allow-Origin": "*",
        },
      })
      .then((res) => {
        alert("등록되었습니다.")
        setUserExcel(null)
        const fileInput = document.querySelector('input[type="file"]')
        const dataTransfer = new DataTransfer()
        fileInput.files = dataTransfer.files
      })
      .catch((err) => {
        alert("등록 실패")
        setUserExcel(null)
        const fileInput = document.querySelector('input[type="file"]')
        const dataTransfer = new DataTransfer()
        fileInput.files = dataTransfer.files
      })
  }
  const userExcelHandler = (event) => {
    event.preventDefault()

    axios1
      .get(API_URL + "excel/download", {
        headers: {
          accessToken: window.localStorage.getItem("token"),
          "Content-Type": "application/json",
        },
        responseType: "blob",
      })
      .then((response) => {
        const url = window.URL.createObjectURL(
          new Blob([response.data], {
            type: response.headers["content-type"],
          })
        )
        const link = document.createElement("a")
        link.href = url
        link.setAttribute("download", "user.xlsx")
        document.body.appendChild(link)
        link.click()
      })
  }
  const fileInput = useRef()
  return (
    // <nav className={classes.navbar}>
    // <Link to="/admin" className={classes.navbar__logo}>
    //   SSAFY
    // </Link>
    //   <ul className={classes.navbar__menu}>
    //     <Link to="/admin" className={classes.navbar__item}>
    //       Main
    //     </Link>
    // <Link to="/admin/form" state={null} className={classes.navbar__item}>
    //   Create
    // </Link>
    //     <span className={classes.navbar__item} onClick={logoutHandler}>
    //       Logout
    //     </span>
    //     <Link to="/enter" className={classes.navbar__item}>
    //       프로님 버전
    //     </Link>
    //     <Link to="/exit" className={classes.navbar__item}>
    //       컨설턴트님 버전
    //     </Link>
    //     {/* <span className={classes.navbar__item}>Logout</span> */}
    //   </ul>
    // </nav>
    <nav role="navigation">
      <div className={classes.menuToggle}>
        <input type="checkbox" />

        <span></span>
        <span></span>
        <span></span>

        <ul className={classes.menu}>
          <a href="#">
            <li>
              <Link to="/admin" className={classes.navbar__item}>
                Main
              </Link>
            </li>
          </a>
          <a href="#">
            <li>
              <span className={classes.navbar__item} onClick={logoutHandler}>
                Logout
              </span>
            </li>
          </a>
          <a href="#">
            <li>
              <Link
                to="/admin/form"
                state={null}
                className={classes.navbar__item}
              >
                Create
              </Link>
            </li>
          </a>
          <a href="#">
            <li>
              {" "}
              <Link to="/enter" className={classes.navbar__item}>
                프로님 버전
              </Link>
            </li>
          </a>
          <a href="#" target="_blank">
            <li>
              {" "}
              <Link to="/exit" className={classes.navbar__item}>
                컨설턴트님 버전
              </Link>
            </li>
          </a>
          <a>
            <li>
              <button
                onClick={() => {
                  fileInput.current.click()
                }}
              >
                upload
              </button>
              <form onSubmit={submitUserExcelHandler}>
                <input
                  type="file"
                  id="user_excel"
                  accept=".xls,.xlsx"
                  ref={fileInput}
                  onChange={changeUserExcelHandler}
                />

                <button type="submit">
                  <span class="material-symbols-outlined">file_upload</span>
                </button>
              </form>
            </li>
          </a>
          <a>
            <li>
              <div>
                <span
                  onClick={userExcelHandler}
                  class="material-symbols-outlined"
                >
                  system_update_alt
                </span>
              </div>
            </li>
          </a>
        </ul>
      </div>
    </nav>
  )
}

export default AdminHeader
