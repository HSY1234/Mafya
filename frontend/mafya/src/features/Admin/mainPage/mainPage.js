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

const MainPage = () => {
  const [students, setStudents] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [dangerList, setDangerList] = useState([]);
  const [studentList, setStudentList] = useState([]);
  const [activePage, setActivePage] = useState(1);
  const [totalPages, setTotalPages] = useState(null);
  const [itemsCountPerPage, setItemsCountPerPage] = useState(null);
  const [totalItemsCount, setTotalItemsCount] = useState(null);

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
        alert("학생 정보 제거");
      })
      .catch((err) => {
        console.log(err.response);
      });
  };

  const updateHandler = (stduent) => {
    history.push({ pathname: "/admin/form", state: stduent });
  };

  const fetchStudents = (page) => {
    let tmpPage = page - 1;
    axios1
      .get(API_URL + `student?page=${tmpPage}&size=5`, {
        headers: {
          accessToken: window.localStorage.getItem("token"),
        },
      })
      .then((res) => {
        setStudents(res.data.userList.content);
        setTotalPages(res.data.userList.totalPages);
        setItemsCountPerPage(res.data.userList.size);
        setTotalItemsCount(res.data.userList.totalElements);
      })
      .catch((err) => {
        alert("학생 정보를 불러오지 못했습니다.");
      });
  };

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

  const handlePageChange = (pageNumber) => {
    setActivePage(pageNumber);
  };

  useEffect(() => {
    fetchStudents(activePage);
  }, [activePage]);
  useEffect(() => {
    fetchStudents(activePage);
    setIsLoading(true);
    const classCode = window.localStorage.getItem("classCode");
    fetchDangerList(classCode);
    setIsLoading(true);
    fetchStudentList(classCode);
    setIsLoading(false);
  }, []);

  return !isLoading && students.length ? (
    <div>
      <AdminHeader />
      <DangerList dangerList={dangerList} />
      <StudentList studentList={studentList} />
      <div>
        <h3>학생 명단</h3>
        <table className={styles.table}>
          <thead>
            <tr>
              <th>이름</th>
              <th>학번</th>
              <th>반</th>
              <th>팀 코드</th>
              <th>휴대폰 번호</th>
              <th>팀장 여부</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {students.map((student) => {
              return (
                <ReadonlyRow
                  student={student}
                  deleteHandler={deleteHandler}
                  updateHandler={updateHandler}
                />
              );
            })}
          </tbody>
        </table>
        <div>
          <Pagination
            activePage={activePage}
            itemsCountPerPage={itemsCountPerPage}
            totalItemsCount={totalItemsCount}
            pageRangeDisplayed={5}
            prevPageText="<"
            nextPageText=">"
            onChange={handlePageChange}
          />
        </div>
      </div>
    </div>
  ) : (
    <div>
      <AdminHeader />
      <DangerList dangerList={dangerList} />
      <StudentList studentList={studentList} />
      <span>학생 정보가 없습니다.</span>
    </div>
  );
};

export default MainPage;
