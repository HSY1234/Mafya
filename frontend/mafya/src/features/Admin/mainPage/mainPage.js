import { useState } from "react";
import { useEffect } from "react";
import { useHistory } from "react-router-dom";
import { API_URL } from "../../../common/api";
import AdminHeader from "../header/adminHeader";
import ReadonlyRow from "./ReadOnlyRow";
import styles from "./mainPage.module.css";
import AttendStudents from "./dangerList";
import NotAttendStudents from "./studentList";
import Pagination from "react-js-pagination";
import "./mainPage.css";
import DangerList from "./dangerList";
import StudentList from "./studentList";
import axios from "axios";
import axios1 from "../../../common/api/axios";
import CustomPagination from "./customPagination";
import CustomModal from "../../../common/modal/modal";

const MainPage = () => {
  const [students, setStudents] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [dangerList, setDangerList] = useState([]);
  const [studentList, setStudentList] = useState([]);
  const [limit, setLimit] = useState(10);
  const [page, setPage] = useState(1);
  const [total, setTotal] = useState(null);
  const offset = (page - 1) * limit;
  const [checkItems, setCheckItems] = useState([]);
  const [modalOpen, setModalOpen] = useState(false);
  const [messages, setMessages] = useState("");
  const [isAbsent, setIsAbsent] = useState(false);
  const [isTrady, setIsTrady] = useState(false);
  const [search, setSearch] = useState("");
  const [searchLoading, setSearchLoading] = useState(true);

  const messegesHandler = (event) => {
    const tmpMessges = event.target.value;
    setMessages(tmpMessges);
  };

  const openModal = () => {
    setModalOpen(true);
  };
  const closeModal = () => {
    setModalOpen(false);
    setMessages("");
  };
  // const [activePage, setActivePage] = useState(1);
  // const [totalPages, setTotalPages] = useState(null);
  // const [itemsCountPerPage, setItemsCountPerPage] = useState(null);
  // const [totalItemsCount, setTotalItemsCount] = useState(null);

  const history = useHistory();
  const deleteHandler = (studentId) => {
    axios1
      .delete(API_URL + `student/${studentId}`, {
        headers: {
          accessToken: window.localStorage.getItem("token"),
        },
      })
      .then((res) => {
        const newStudents = [...students];
        const index = students.findIndex((stduent) => stduent.id === studentId);
        newStudents.splice(index, 1);
        setStudents(newStudents);
        const newDangerList = [...dangerList];
        const dangerIndex = dangerList.findIndex(
          (stduent) => stduent.id === studentId
        );
        if (dangerIndex !== -1) {
          newDangerList.splice(dangerIndex, 1);
          setDangerList(newDangerList);
        }
        const newStudentList = [...studentList];
        const studentListIndex = studentList.findIndex(
          (stduent) => stduent.id === studentId
        );
        if (studentListIndex !== -1) {
          newStudentList.splice(studentListIndex, 1);
          setStudentList(newStudentList);
        }

        alert("학생 정보 제거");
      })
      .catch((err) => {
        console.log(err.response);
      });
  };

  const updateHandler = (stduent) => {
    history.push({ pathname: "/admin/form", state: stduent });
  };

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
  //       alert("학생 정보를 불러오지 못했습니다.");
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
        setDangerList(res.data);
      })
      .catch((err) => {
        alert("위험 리스트 정보를 불러오지 못했습니다.");
      });
  };

  const fetchStudentList = (classCode) => {
    axios1
      .get(API_URL + `attendance/class/${classCode}`, {
        headers: {
          accessToken: window.localStorage.getItem("token"),
        },
      })
      .then((res) => {
        setStudentList(res.data);
      })
      .catch((err) => {
        alert("학생 리스트 정보를 불러오지 못했습니다.");
      });
  };

  const fetchStudents = () => {
    setSearchLoading(true);
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
        setStudents(res.data);
        setTotal(res.data.length);
        console.log(res.data);
      })
      .catch((err) => {
        alert("학생 리스트 정보를 불러오지 못했습니다.");
      });
    setSearchLoading(false);
  };
  // const handlePageChange = (pageNumber) => {
  //   setActivePage(pageNumber);
  // };

  // useEffect(() => {
  //   fetchStudents(activePage);
  // }, [activePage]);
  useEffect(() => {
    // fetchStudents(activePage);
    setIsLoading(true);
    const classCode = window.localStorage.getItem("classCode");
    fetchDangerList(classCode);
    setIsLoading(true);
    fetchStudentList(classCode);

    setIsLoading(false);
  }, []);

  const searchChangeHandler = (event) => {
    const tmpSearch = event.target.value;
    setSearch(tmpSearch);
  };

  const searchHandler = (event) => {
    event.preventDefault();
    setSearchLoading(true);
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
        setStudents(res.data);
        setTotal(res.data.length);
      })
      .catch((err) => {
        alert("학생 리스트 정보를 불러오지 못했습니다.");
      });
    setSearchLoading(false);
    // fetchStudents();
  };

  const searchAbsentHandler = (event) => {
    event.preventDefault();
    setIsAbsent(!isAbsent);
    fetchStudents();
  };

  const searchTardyHandler = (event) => {
    event.preventDefault();
    setIsTrady(!isTrady);
    fetchStudents();
  };

  const handleSingleCheck = (checked, id) => {
    if (checked) {
      setCheckItems((prev) => [...prev, id]);
    } else {
      setCheckItems(checkItems.filter((el) => el !== id));
    }
  };

  const handleAllCheck = (checked) => {
    if (checked) {
      const idArray = [];
      studentList.forEach((el) => idArray.push(el.id));
      setCheckItems(idArray);
    } else {
      setCheckItems([]);
    }
  };

  const mmsHandler = (event) => {
    event.preventDefault();
    setModalOpen(true);
  };

  const mmsTransferHandler = (event) => {
    event.preventDefault();
    const formData = { ids: checkItems, messages };
    axios1
      .post(API_URL + "mms", formData, {
        headers: { accessToken: window.localStorage.getItem("token") },
      })
      .then((res) => {
        alert("성공");
        setModalOpen(false);
        setMessages("");
      })
      .catch((err) => {
        console.log(err);
      });
  };

  return (
    !isLoading && (
      <div>
        <AdminHeader />
        <CustomModal
          open={modalOpen}
          close={closeModal}
          ids={checkItems}
          header="Modal heading"
        >
          <form onSubmit={mmsTransferHandler}>
            전송할 메시지를 입력하세요!
            <div>
              <input
                type="textarea"
                value={messages}
                onChange={messegesHandler}
              />
            </div>
            <div>
              <button type="submit" className="close">
                전송
              </button>
            </div>
          </form>
        </CustomModal>
        {dangerList?.length ? (
          <DangerList dangerList={dangerList} />
        ) : (
          <p>결석 위험군이 없습니다.</p>
        )}
        <div>
          <table>
            <thead>
              <tr>
                <th>
                  <input
                    type="checkbox"
                    name="select-all"
                    onChange={(e) => handleAllCheck(e.target.checked)}
                    checked={
                      checkItems.length === studentList.length ? true : false
                    }
                  />
                </th>
                <th>이름</th>
                <th>전화번호</th>
              </tr>
            </thead>
            <tbody>
              {studentList?.map((data, key) => (
                <tr key={key}>
                  <td>
                    <input
                      type="checkbox"
                      name={`select-${data.id}`}
                      onChange={(e) =>
                        handleSingleCheck(e.target.checked, data.id)
                      }
                      checked={checkItems.includes(data.id) ? true : false}
                    />
                  </td>
                  <td>{data.name}</td>
                  <td>{data.phoneNum}</td>
                </tr>
              ))}
            </tbody>
          </table>
          <form onSubmit={mmsHandler}>
            <button type="submit">MMS 전송</button>
          </form>
        </div>

        <div>
          <h3>학생 명단</h3>
          <form onSubmit={searchHandler}>
            <input type="text" onChange={searchChangeHandler} />
            <button type="submit">검색</button>
          </form>
          {students.length ? (
            <table className={styles.table}>
              <thead>
                <tr>
                  <th>이름</th>
                  <th>학번</th>
                  <th>반</th>
                  <th>팀 코드</th>
                  <th>휴대폰 번호</th>
                  <th>팀장 여부</th>
                  <th onClick={searchAbsentHandler}>결석</th>
                  <th onClick={searchTardyHandler}>지각</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {students.slice(offset, offset + limit).map((student) => {
                  return (
                    <ReadonlyRow
                      key={student.id}
                      student={student}
                      deleteHandler={deleteHandler}
                      updateHandler={updateHandler}
                    />
                  );
                })}
              </tbody>
            </table>
          ) : (
            <p>검색 결과가 없습니다.</p>
          )}
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
      </div>
    )
  );
};

export default MainPage;
