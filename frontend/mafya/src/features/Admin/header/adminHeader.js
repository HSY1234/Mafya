import classes from "./adminHeader.module.css"
import { Link, useHistory } from "react-router-dom"
import axios from "axios"
import { API_URL } from "../../../common/api"
import { useState, useRef } from "react"
import axios1 from "../../../common/api/axios"

const AdminHeader = (props) => {
  const { onPage } = props
  const history = useHistory()
  const [userExcel, setUserExcel] = useState(null)
  const [listRegi, setlistRegi] = useState(false)
  const [listDown, setlistDown] = useState(false)
  const [fileName, setfileName] = useState("파일을 선택해 주세요")
  const [onFile, setonFile] = useState(false)
  const Swal = require("sweetalert2")
  const logoutHandler = (event) => {
    Swal.fire({
      title: "확실한가요?",
      text: "관리할 학생들이 없는지 확인하세요.",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#3396f4",
      cancelButtonColor: "#dc143cac",
      confirmButtonText: "로그아웃",
      cancelButtonText: "취소",
    }).then((result) => {
      if (result.isConfirmed) {
        Swal.fire({
          icon: "success",
          title: `로그아웃 되었습니다.`,
          timer: 1500,
        })
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
    })
  }
  const changeUserExcelHandler = (event) => {
    event.preventDefault()
    console.log(event.target.files[0])
    let file = event.target.files[0]
    setUserExcel(file)
    setfileName(event.target.files[0].name)
    setonFile(true)
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
    Swal.fire({
      title: "학생 정보를 저장하시겠습니까?",
      text: "등록된 모든 학생 정보를 저장합니다.",
      icon: "question",
      showCancelButton: true,
      confirmButtonColor: "#3396f4",
      cancelButtonColor: "#dc143cac",
      confirmButtonText: "저장",
      cancelButtonText: "취소",
    }).then((result) => {
      if (result.isConfirmed) {
        Swal.fire({
          icon: "success",
          title: `저장 되었습니다.`,
          timer: 1500,
        })
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
    })
  }
  const fileInput = useRef()
  const clickRegi = () => {
    setlistRegi(!listRegi)
  }
  const clickDown = () => {
    setlistDown(!listDown)
  }

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
          <li>
            {onPage === 1 ? (
              <div className={classes.onThisPage}>학생 조회</div>
            ) : (
              <Link to="/admin" className={classes.navbar__item}>
                학생 조회
              </Link>
            )}
            {/* <Link to="/admin" className={classes.navbar__item}>
              Main
            </Link> */}
          </li>

          <li>
            {onPage === 2 ? (
              <div className={classes.onThisPage}>학생 추가</div>
            ) : (
              <Link
                to="/admin/form"
                state={null}
                className={classes.navbar__item}
              >
                학생 추가
              </Link>
            )}
          </li>
          <li className={classes.webCamList}>
            <span className={classes.webCamBtn} onClick={clickDown}>
              출석 시스템
            </span>
            <div
              className={
                listDown ? classes.downAllUserBox : classes.noDownAllUserBox
              }
            >
              <Link to="/enter" className={classes.navbar__item}>
                프로님 버전
              </Link>
              <Link to="/exit" className={classes.navbar__item}>
                컨설턴트님 버전
              </Link>
            </div>
          </li>
          <li>
            <span className={classes.regiAllUser} onClick={clickRegi}>
              사용자 일괄 등록
            </span>
            <div
              className={
                listRegi ? classes.regiAllUserBox : classes.noRegiAllUserBox
              }
            >
              <div
                className={
                  onFile ? classes.uploadedFile : classes.notUploadedFile
                }
                onClick={() => {
                  fileInput.current.click()
                }}
              >
                {fileName}
              </div>
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
                  <span className="material-symbols-outlined">file_upload</span>
                </button>
              </form>
            </div>
          </li>
          <li>
            <span className={classes.downAllUser} onClick={userExcelHandler}>
              사용자 일괄 저장
            </span>
            {/* <div
              className={
                listDown ? classes.downAllUserBox : classes.noDownAllUserBox
              }
            >
              <span
                onClick={userExcelHandler}
                className="material-symbols-outlined"
              >
                system_update_alt
              </span>
            </div> */}
          </li>
          <li>
            <span className={classes.logoutBtn} onClick={logoutHandler}>
              로그아웃
            </span>
          </li>
        </ul>
      </div>
    </nav>
  )
}

export default AdminHeader
