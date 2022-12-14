import { useState } from "react"
import { useEffect } from "react"
import { useHistory } from "react-router-dom"
import { API_URL } from "../../../common/api"
import AdminHeader from "../header/adminHeader"
import ReadonlyRow from "./ReadOnlyRow"
import styles from "./mainPage.module.css"
import AttendStudents from "./dangerList"
import NotAttendStudents from "./studentList"
import Pagination from "react-js-pagination"
import "./mainPage.css"
import DangerList from "./dangerList"
import StudentList from "./studentList"
import axios from "axios"
import axios1 from "../../../common/api/axios"
import CustomPagination from "./customPagination"
import CustomModal from "../../../common/modal/modal"

const MainPage = () => {
  const [students, setStudents] = useState([])
  const [isLoading, setIsLoading] = useState(true)
  const [dangerList, setDangerList] = useState([])
  const [studentList, setStudentList] = useState([])
  const [limit, setLimit] = useState(10)
  const [page, setPage] = useState(1)
  const [total, setTotal] = useState(null)
  const offset = (page - 1) * limit
  const [checkItems, setCheckItems] = useState([])
  const [dangerCheckItems, setDangerCheckItems] = useState([])
  const [modalOpen, setModalOpen] = useState(false)
  const [messages, setMessages] = useState("")
  const [isDate, setIsDate] = useState(false)
  const [isName, setIsName] = useState(false)
  const [isUserCode, setIsUserCode] = useState(false)
  const [isClassCode, setIsClassCode] = useState(false)
  const [isTeamCode, setIsTeamCode] = useState(false)
  const [isAbsent, setIsAbsent] = useState(false)
  const [isTrady, setIsTrady] = useState(false)
  const [isStatus, setIsStatus] = useState(false)
  const [isTeamLeader, setIsTeamLeader] = useState(false)
  const [userExcel, setUserExcel] = useState(null)
  const [search, setSearch] = useState("")
  const [searchLoading, setSearchLoading] = useState(true)
  const [dangerModalOpen, setDangerModalOpen] = useState(false)
  const [searchBox, setSearchBox] = useState(false)
  const [searchModalOpen, setSearchModalOpen] = useState(false)
  const [ids, setIds] = useState(null)

  const Swal = require("sweetalert2")

  const searchOpenModal = () => {
    setSearchModalOpen(true)
  }
  const searchCloseModal = () => {
    setSearchModalOpen(false)
    setMessages("")
  }

  const dangerOpenModal = () => {
    setDangerModalOpen(true)
  }
  const dangerCloseModal = () => {
    setDangerModalOpen(false)
    setMessages("")
  }

  const messegesHandler = (event) => {
    const tmpMessges = event.target.value
    setMessages(tmpMessges)
  }

  const openModal = () => {
    setModalOpen(true)
  }
  const closeModal = () => {
    setModalOpen(false)
    setMessages("")
  }
  // const [activePage, setActivePage] = useState(1);
  // const [totalPages, setTotalPages] = useState(null);
  // const [itemsCountPerPage, setItemsCountPerPage] = useState(null);
  // const [totalItemsCount, setTotalItemsCount] = useState(null);

  const history = useHistory()
  const deleteHandler = (studentId) => {
    axios1
      .delete(API_URL + `student/${studentId}`, {
        headers: {
          accessToken: window.localStorage.getItem("token"),
        },
      })
      .then((res) => {
        const newStudents = [...students]
        const index = students.findIndex((stduent) => stduent.id === studentId)
        newStudents.splice(index, 1)
        setStudents(newStudents)
        const newDangerList = [...dangerList]
        const dangerIndex = dangerList.findIndex(
          (stduent) => stduent.id === studentId
        )
        if (dangerIndex !== -1) {
          newDangerList.splice(dangerIndex, 1)
          setDangerList(newDangerList)
        }
        const newStudentList = [...studentList]
        const studentListIndex = studentList.findIndex(
          (stduent) => stduent.id === studentId
        )
        if (studentListIndex !== -1) {
          newStudentList.splice(studentListIndex, 1)
          setStudentList(newStudentList)
        }

        alert("?????? ?????? ??????")
      })
      .catch((err) => {
        console.log(err.response)
      })
  }

  const updateHandler = (stduent) => {
    history.push({ pathname: "/admin/form", state: stduent })
  }

  // const fetchStudents = (page) => {
  //   let tmpPage = page - 1;
  //   axios1
  //     .get(API_URL + `student?page=${tmpPage}&size=5`, {
  //       headers: {
  //         accessToken: window.localStorage.getItem("token"),
  //       },
  //     })
  //     .then((res) => {
  //       setStudents(res.data.userList.content);
  //       setTotalPages(res.data.userList.totalPages);
  //       setItemsCountPerPage(res.data.userList.size);
  //       setTotalItemsCount(res.data.userList.totalElements);
  //     })
  //     .catch((err) => {
  //       alert("?????? ????????? ???????????? ???????????????.");
  //     });
  // };

  const fetchDangerList = (classCode) => {
    axios1
      .get(API_URL + `attendance/danger/${classCode}`, {
        headers: {
          accessToken: window.localStorage.getItem("token"),
        },
      })
      .then((res) => {
        setDangerList(res.data)
      })
      .catch((err) => {
        alert("?????? ????????? ????????? ???????????? ???????????????.")
      })
  }

  const fetchStudentList = (classCode) => {
    axios1
      .get(API_URL + `attendance/class/${classCode}`, {
        headers: {
          accessToken: window.localStorage.getItem("token"),
        },
      })
      .then((res) => {
        setStudentList(res.data)
      })
      .catch((err) => {
        alert("?????? ????????? ????????? ???????????? ???????????????.")
      })
  }

  const fetchStudents = () => {
    setSearchLoading(true)
    axios1
      .post(
        API_URL + "search",
        { content: search, tradyOrder: isTrady, absentOrder: isAbsent },
        {
          headers: {
            accessToken: window.localStorage.getItem("token"),
            "Content-Type": "application/json",
          },
        }
      )
      .then((res) => {
        setStudents(res.data)
        setTotal(res.data.length)
        console.log(res.data)
      })
      .catch((err) => {
        alert("?????? ????????? ????????? ???????????? ???????????????.")
      })
    setSearchLoading(false)
  }
  // const handlePageChange = (pageNumber) => {
  //   setActivePage(pageNumber);
  // };

  // useEffect(() => {
  //   fetchStudents(activePage);
  // }, [activePage]);
  useEffect(() => {
    // fetchStudents(activePage);
    setIsLoading(true)
    const classCode = window.localStorage.getItem("classCode")
    fetchDangerList(classCode)
    setSearch(classCode)
    setIsLoading(true)
    fetchStudentList(classCode)

    setIsLoading(false)
    setSearchLoading(true)
    axios1
      .post(
        API_URL + "search",
        { content: search, tradyOrder: isTrady, absentOrder: isAbsent },
        {
          headers: {
            accessToken: window.localStorage.getItem("token"),
            "Content-Type": "application/json",
          },
        }
      )
      .then((res) => {
        setStudents(res.data)
        setTotal(res.data.length)
        setIsDate(false)
        setIsAbsent(!isAbsent)
        setIsClassCode(false)
        setIsName(false)
        setIsTeamCode(false)
        setIsUserCode(false)
        setIsTrady(!isTrady)
        setIsTeamLeader(false)
        setPage(1)
      })
      .catch((err) => {
        alert("?????? ????????? ????????? ???????????? ???????????????.")
      })
    setSearchLoading(false)
  }, [])

  const searchChangeHandler = (event) => {
    const tmpSearch = event.target.value
    setSearch(tmpSearch)
  }

  const searchHandler = (event) => {
    event.preventDefault()
    setSearchLoading(true)
    axios1
      .post(
        API_URL + "search",
        { content: search, tradyOrder: isTrady, absentOrder: isAbsent },
        {
          headers: {
            accessToken: window.localStorage.getItem("token"),
            "Content-Type": "application/json",
          },
        }
      )
      .then((res) => {
        setStudents(res.data)
        setTotal(res.data.length)
        setIsDate(false)
        setIsAbsent(!isAbsent)
        setIsClassCode(false)
        setIsName(false)
        setIsTeamCode(false)
        setIsUserCode(false)
        setIsTrady(!isTrady)
        setIsTeamLeader(false)
        setPage(1)
      })
      .catch((err) => {
        alert("?????? ????????? ????????? ???????????? ???????????????.")
      })
    setSearchLoading(false)
    // fetchStudents();
  }

  const searchDateHandler = (event) => {
    event.preventDefault()
    if (isDate && students.length) {
      setIsDate(!isDate)
      const tmpStudents = students
      tmpStudents.sort((a, b) => new Date(a.date) - new Date(b.date))
      setStudents(tmpStudents)
      setPage(1)
    } else if (!isDate && students.length) {
      setIsDate(!isDate)
      const tmpStudents = students
      tmpStudents.sort((a, b) => new Date(b.date) - new Date(a.date))
      setStudents(tmpStudents)
      setPage(1)
    }
  }

  const searchNameHandler = (event) => {
    event.preventDefault()
    if (isName && students.length) {
      setIsName(!isName)
      const tmpStudents = students
      tmpStudents.sort((x, y) => x.name.localeCompare(y.name))
      setStudents(tmpStudents)
      setPage(1)
    } else if (!isName && students.length) {
      setIsName(!isName)
      const tmpStudents = students
      tmpStudents.sort((x, y) => y.name.localeCompare(x.name))
      setStudents(tmpStudents)
      setPage(1)
    }
  }

  const searchTeamLeaderHandler = (event) => {
    event.preventDefault()
    if (isTeamLeader && students.length) {
      setIsTeamLeader(!isTeamLeader)
      const tmpStudents = students
      tmpStudents.sort((x, y) => x.teamLeader - y.teamLeader)
      setStudents(tmpStudents)
      setPage(1)
    } else if (!isTeamLeader && students.length) {
      setIsTeamLeader(!isTeamLeader)
      const tmpStudents = students
      tmpStudents.sort((x, y) => y.teamLeader - x.teamLeader)
      setStudents(tmpStudents)
      setPage(1)
    }
  }

  const searchUserCodeHandler = (event) => {
    event.preventDefault()
    if (isUserCode && students.length) {
      setIsUserCode(!isUserCode)
      const tmpStudents = students
      tmpStudents.sort((x, y) => x.userCode.localeCompare(y.userCode))
      setStudents(tmpStudents)
      setPage(1)
    } else if (!isUserCode && students.length) {
      setIsUserCode(!isUserCode)
      const tmpStudents = students
      tmpStudents.sort((x, y) => y.userCode.localeCompare(x.userCode))
      setStudents(tmpStudents)
      setPage(1)
    }
  }

  const searchTeamCodeHandler = (event) => {
    event.preventDefault()
    if (isTeamCode && students.length) {
      setIsTeamCode(!isTeamCode)
      const tmpStudents = students
      tmpStudents.sort((x, y) => x.teamCode.localeCompare(y.teamCode))
      setStudents(tmpStudents)
      setPage(1)
    } else if (!isTeamCode && students.length) {
      setIsTeamCode(!isTeamCode)
      const tmpStudents = students
      tmpStudents.sort((x, y) => y.teamCode.localeCompare(x.teamCode))
      setStudents(tmpStudents)
      setPage(1)
    }
  }

  const searchClassCodeHandler = (event) => {
    event.preventDefault()
    if (isClassCode && students.length) {
      setIsClassCode(!isClassCode)
      const tmpStudents = students
      tmpStudents.sort((x, y) => x.classCode.localeCompare(y.classCode))
      setStudents(tmpStudents)
      setPage(1)
    } else if (!isClassCode && students.length) {
      setIsClassCode(!isClassCode)
      const tmpStudents = students
      tmpStudents.sort((x, y) => y.classCode.localeCompare(x.classCode))
      setStudents(tmpStudents)
      setPage(1)
    }
  }

  const searchAbsentHandler = (event) => {
    event.preventDefault()
    if (isAbsent && students.length) {
      setIsAbsent(!isAbsent)
      const tmpStudents = students
      tmpStudents.sort((x, y) => x.absent - y.absent)
      setStudents(tmpStudents)
      setPage(1)
    } else if (!isAbsent && students.length) {
      setIsAbsent(!isAbsent)
      const tmpStudents = students
      tmpStudents.sort((x, y) => y.absent - x.absent)
      setStudents(tmpStudents)
      setPage(1)
    }
  }

  const searchTardyHandler = (event) => {
    event.preventDefault()
    if (isTrady && students.length) {
      setIsTrady(!isTrady)
      const tmpStudents = students
      tmpStudents.sort((x, y) => x.trady - y.trady)
      setStudents(tmpStudents)
      setPage(1)
    } else if (!isTrady && students.length) {
      setIsTrady(!isTrady)
      const tmpStudents = students
      tmpStudents.sort((x, y) => y.trady - x.trady)
      setStudents(tmpStudents)
      setPage(1)
    }
  }

  const searchStatusHandler = (event) => {
    event.preventDefault()
    if (isStatus && students.length) {
      setIsStatus(!isStatus)
      const tmpStudents = students

      tmpStudents.sort((x, y) => x.trace.localeCompare(y.trace))
      setStudents(tmpStudents)
      setPage(1)
    } else if (!isStatus && students.length) {
      setIsStatus(!isStatus)
      const tmpStudents = students
      tmpStudents.sort((x, y) => y.trace.localeCompare(x.trace))
      setStudents(tmpStudents)
      setPage(1)
    }
  }

  const handleSingleCheck = (checked, id) => {
    if (checkItems.includes(id)) {
      setCheckItems(checkItems.filter((el) => el !== id))
    } else {
      setCheckItems((prev) => [...prev, id])
    }
    // if (checked) {
    //   setCheckItems((prev) => [...prev, id])
    // } else {
    //   setCheckItems(checkItems.filter((el) => el !== id))
    // }
    console.log(checkItems)
  }

  const dangerhandleSingleCheck = (checked, id) => {
    if (dangerCheckItems.includes(id)) {
      setDangerCheckItems(dangerCheckItems.filter((el) => el !== id))
    } else {
      setDangerCheckItems((prev) => [...prev, id])
    }
    // if (checked) {
    //   setCheckItems((prev) => [...prev, id])
    // } else {
    //   setCheckItems(checkItems.filter((el) => el !== id))
    // }
    // console.log(checkItems)
  }

  const handleAllCheck = (checked) => {
    // if (checked) {
    //   const idArray = []
    //   studentList.forEach((el) => idArray.push(el.id))
    //   setCheckItems(idArray)
    // } else {
    //   setCheckItems([])
    // }
    if (checkItems.length === studentList.length) {
      setCheckItems([])
    } else {
      const idArray = []
      studentList.forEach((el) => idArray.push(el.id))
      setCheckItems(idArray)
    }
  }
  const dangerHandleAllCheck = (checked) => {
    // if (checked) {
    //   const idArray = []
    //   dangerList.forEach((el) => idArray.push(el.id))
    //   setDangerCheckItems(idArray)
    // } else {
    //   setDangerCheckItems([])
    // }
    if (dangerCheckItems.length === dangerList.length) {
      setDangerCheckItems([])
    } else {
      const idArray = []
      dangerList.forEach((el) => idArray.push(el.id))
      setDangerCheckItems(idArray)
    }
  }
  const mmsHandler = (event) => {
    event.preventDefault()
    setModalOpen(true)
  }

  const mmsDangerHandler = (event) => {
    event.preventDefault()
    setDangerModalOpen(true)
  }
  const searchMmsHandler = (event) => {
    setSearchModalOpen(true)
  }
  const searchMmsTransferHandler = (event) => {
    event.preventDefault()
    const formData = { ids: [ids], messages }
    console.log(formData)
    axios1
      .post(API_URL + "mms", formData, {
        headers: { accessToken: window.localStorage.getItem("token") },
      })
      .then((res) => {
        Swal.fire({
          icon: "success",
          title: `???????????? ?????????????????????.`,
          timer: 1500,
        })
        setSearchModalOpen(false)
        setMessages("")
        setIds(null)
      })
      .catch((err) => {
        console.log(err)
      })
  }

  const mmsDangerTransferHandler = (event) => {
    event.preventDefault()
    console.log("?????????", dangerCheckItems)
    const formData = { ids: dangerCheckItems, messages }
    axios1
      .post(API_URL + "mms", formData, {
        headers: { accessToken: window.localStorage.getItem("token") },
      })
      .then((res) => {
        Swal.fire({
          icon: "success",
          title: `???????????? ?????????????????????.`,
          timer: 1500,
        })
        setDangerModalOpen(false)
        setMessages("")
      })
      .catch((err) => {
        console.log(err)
      })
  }

  const mmsTransferHandler = (event) => {
    event.preventDefault()

    const formData = { ids: checkItems, messages }
    axios1
      .post(API_URL + "mms", formData, {
        headers: { accessToken: window.localStorage.getItem("token") },
      })
      .then((res) => {
        Swal.fire({
          icon: "success",
          title: `???????????? ?????????????????????.`,
          timer: 1500,
        })
        setModalOpen(false)
        setMessages("")
      })
      .catch((err) => {
        console.log(err)
      })
  }
  const clickSearchBox = () => {
    setSearchBox(true)
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
        alert("?????????????????????.")
        setUserExcel(null)
        const fileInput = document.querySelector('input[type="file"]')
        const dataTransfer = new DataTransfer()
        fileInput.files = dataTransfer.files
      })
      .catch((err) => {
        alert("?????? ??????")
        setUserExcel(null)
        const fileInput = document.querySelector('input[type="file"]')
        const dataTransfer = new DataTransfer()
        fileInput.files = dataTransfer.files
      })
  }

  const searchExcelHandler = (event) => {
    event.preventDefault()
    if (students.length) {
      axios1
        .post(
          API_URL + "excel/search",
          { content: search, tradyOrder: isTrady, absentOrder: isAbsent },
          {
            headers: {
              accessToken: window.localStorage.getItem("token"),
              "Content-Type": "application/json",
            },
            responseType: "blob",
          }
        )
        .then((response) => {
          const url = window.URL.createObjectURL(
            new Blob([response.data], {
              type: response.headers["content-type"],
            })
          )
          const link = document.createElement("a")
          link.href = url
          link.setAttribute("download", "search.xlsx")
          document.body.appendChild(link)
          link.click()
        })
    }
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
  const clickGuideLine = () => {
    Swal.fire({
      icon: "info",
      title: "?????? ??????",
      html: `

      <div style='text-align:start;line-height:2rem; font-family:"Pretendard-Regular";'>
      1. ["" or "??????"]<br><div style='width:100%;height:0.5rem;'></div>
      2. ["??????" or "??????"] [??? ??????, ... Default X]<br> &nbsp&nbsp[?????????, ... Default X] [??????, Default ??????] <br><div style='width:100%;height:0.5rem;'></div>
      3. [??? ??????, ... Deafult X] [??? ??????, ... Deafult X]<br>&nbsp &nbsp[??????, ... or "??????" Default ??????]<br><div style='width:100%;height:0.5rem;'></div>
      4. ["??????", ... Deafult X] [??????, ... or "??????" Default ??????]<br> 
      </div>`,
    })
  }

  return (
    !isLoading && (
      <div>
        <CustomModal open={modalOpen} close={closeModal} header="">
          <form onSubmit={mmsTransferHandler}>
            <span>????????? ???????????? ???????????????!</span>
            <div>
              <input
                type="textarea"
                value={messages}
                onChange={messegesHandler}
              />
            </div>
            <div>
              <button type="submit" className="close">
                ??????
              </button>
            </div>
          </form>
        </CustomModal>
        <CustomModal open={dangerModalOpen} close={dangerCloseModal} header="">
          <form onSubmit={mmsDangerTransferHandler}>
            <span>????????? ???????????? ???????????????!</span>
            <div>
              <input
                type="textarea"
                value={messages}
                onChange={messegesHandler}
              />
            </div>
            <div>
              <button type="submit" className="close">
                ??????
              </button>
            </div>
          </form>
        </CustomModal>
        <CustomModal open={searchModalOpen} close={searchCloseModal} header="">
          <form onSubmit={searchMmsTransferHandler}>
            <span>????????? ???????????? ???????????????!</span>
            <div>
              <input
                type="textarea"
                value={messages}
                onChange={messegesHandler}
              />
            </div>
            <div>
              <button type="submit" className="close">
                ??????
              </button>
            </div>
          </form>
        </CustomModal>
        <div className={styles.wholePage}>
          <AdminHeader onPage={1} />

          <div className={styles.inner}>
            <div className={styles.leftSideBox}>
              <div className={styles.teamStudentBox}>
                <div className={styles.boxTitle}>?????? ????????? ??????</div>

                <div className={styles.teamStudentList}>
                  <div>
                    {dangerList?.map((data, key) => (
                      <div
                        className={
                          dangerCheckItems.includes(data.id)
                            ? styles.studentListItem
                            : styles.studentListItemFalse
                        }
                        onClick={(e) =>
                          dangerhandleSingleCheck(data.id, data.id)
                        }
                        key={key}
                      >
                        <div className={styles.studentListInner}>
                          <input
                            className={styles.studentListCheckBox}
                            type="checkbox"
                            name={`select-${data.id}`}
                            id={data.id}
                            checked={
                              dangerCheckItems.includes(data.id) ? true : false
                            }
                          />
                          <div>
                            <span className={styles.studentName}>
                              {data.name}
                            </span>
                          </div>
                          <div>
                            <span>
                              {data.phoneNum.slice(0, 3)}-
                              {data.phoneNum.slice(3, 7)}-
                              {data.phoneNum.slice(7, 11)}
                            </span>
                          </div>
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
                <div className={styles.dropdown}>
                  <div className={styles.dropdownContent}>
                    <div className={styles.dropdownItem}>
                      <form onSubmit={mmsDangerHandler}>
                        <button
                          className={
                            dangerCheckItems.length
                              ? styles.sendBtn
                              : styles.falseSendBtn
                          }
                          disabled={dangerCheckItems.length ? false : true}
                          type="submit"
                        >
                          <span className="material-symbols-outlined">
                            outgoing_mail
                          </span>
                        </button>
                      </form>
                    </div>
                    <div
                      onClick={dangerHandleAllCheck}
                      className={styles.dropdownItem}
                    >
                      {dangerCheckItems.length &&
                      dangerCheckItems.length === dangerList.length ? (
                        <div className={styles.checked}>
                          <span className="material-symbols-outlined">
                            done_all
                          </span>
                        </div>
                      ) : (
                        <div className={styles.notChecked}>
                          <span className="material-symbols-outlined">
                            done_all
                          </span>
                        </div>
                      )}
                    </div>
                  </div>
                  <button className={styles.dropbtn}>
                    <span className="material-symbols-outlined">send</span>
                  </button>
                </div>
              </div>

              <div className={styles.teamStudentBox}>
                <div className={styles.boxTitle}>?????? ????????? ??????</div>

                <div className={styles.teamStudentList}>
                  <div>
                    {studentList?.map((data, key) => (
                      <div
                        className={
                          checkItems.includes(data.id)
                            ? styles.studentListItem
                            : styles.studentListItemFalse
                        }
                        onClick={(e) => handleSingleCheck(data.id, data.id)}
                        key={key}
                      >
                        <div className={styles.studentListInner}>
                          <input
                            className={styles.studentListCheckBox}
                            type="checkbox"
                            name={`select-${data.id}`}
                            id={data.id}
                            checked={
                              checkItems.includes(data.id) ? true : false
                            }
                          />
                          <div>
                            <span className={styles.studentName}>
                              {data.name}
                            </span>
                          </div>
                          <div>
                            <span>
                              {data.phoneNum.slice(0, 3)}-
                              {data.phoneNum.slice(3, 7)}-
                              {data.phoneNum.slice(7, 11)}
                            </span>
                          </div>
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
                <div className={styles.dropdown}>
                  <div className={styles.dropdownContent}>
                    <div className={styles.dropdownItem}>
                      <form onSubmit={mmsHandler}>
                        <button
                          className={
                            checkItems.length
                              ? styles.sendBtn
                              : styles.falseSendBtn
                          }
                          disabled={checkItems.length ? false : true}
                          type="submit"
                        >
                          <span className="material-symbols-outlined">
                            outgoing_mail
                          </span>
                        </button>
                      </form>
                    </div>
                    <div
                      onClick={handleAllCheck}
                      className={styles.dropdownItem}
                    >
                      {checkItems.length &&
                      checkItems.length === studentList.length ? (
                        <div className={styles.checked}>
                          <span className="material-symbols-outlined">
                            done_all
                          </span>
                        </div>
                      ) : (
                        <div className={styles.notChecked}>
                          <span className="material-symbols-outlined">
                            done_all
                          </span>
                        </div>
                      )}
                    </div>
                  </div>
                  <button className={styles.dropbtn}>
                    <span className="material-symbols-outlined">send</span>
                  </button>
                </div>
              </div>
            </div>
            <div className={styles.rightSideBox}>
              <div className={styles.boxTitle}>
                ?????? ??????
                <span
                  onClick={clickGuideLine}
                  className="material-symbols-outlined"
                >
                  help
                </span>
              </div>
              <div className={styles.searchBox}>
                {searchBox ? (
                  <div
                    className={styles.searchBoxBefore}
                    onClick={clickSearchBox}
                  >
                    <div className={styles.searchBtnBefore}>
                      <span className="material-symbols-outlined">search</span>
                    </div>
                  </div>
                ) : (
                  <div className={styles.searchBoxAfter}>
                    <div className={styles.searchEnzineBox}>
                      <form onSubmit={searchHandler}>
                        <input
                          className={styles.searchEnzineInput}
                          type="text"
                          onChange={searchChangeHandler}
                        />
                        <button
                          className={styles.searchEnzineBtn}
                          type="submit"
                        >
                          <span className="material-symbols-outlined">
                            search
                          </span>
                        </button>
                      </form>
                    </div>
                    <div className={styles.studentTableBox}>
                      {students.length ? (
                        <div>
                          <table className={styles.studentTable}>
                            <thead>
                              <tr>
                                <th onClick={searchDateHandler}>??????</th>
                                <th onClick={searchClassCodeHandler}>???</th>
                                <th onClick={searchTeamCodeHandler}>??? ??????</th>
                                <th onClick={searchUserCodeHandler}>??????</th>
                                <th onClick={searchNameHandler}>??????</th>
                                <th>????????????</th>
                                <th onClick={searchTeamLeaderHandler}>??????</th>
                                <th onClick={searchAbsentHandler}>??????</th>
                                <th onClick={searchTardyHandler}>??????</th>
                                <th onClick={searchStatusHandler}>??????</th>
                                <th></th>
                              </tr>
                            </thead>
                            <tbody>
                              {students
                                .slice(offset, offset + limit)
                                .map((student) => {
                                  return (
                                    <ReadonlyRow
                                      key={students.indexOf(student)}
                                      student={student}
                                      searchMmsHandler={searchMmsHandler}
                                      updateHandler={updateHandler}
                                      setIds={setIds}
                                    />
                                  )
                                })}
                            </tbody>
                          </table>
                          <div className={styles.excelBox}>
                            <button onClick={searchExcelHandler}>
                              <span class="material-symbols-outlined">
                                download
                              </span>
                            </button>
                          </div>
                        </div>
                      ) : (
                        <div className={styles.noResult}>
                          <p>?????? ????????? ????????????.</p>
                        </div>
                      )}
                    </div>
                    {students.length ? (
                      <CustomPagination
                        total={total}
                        limit={limit}
                        page={page}
                        setPage={setPage}
                      />
                    ) : (
                      <div></div>
                    )}
                  </div>
                )}
              </div>
            </div>
          </div>
        </div>
      </div>
    )
  )
}

export default MainPage
