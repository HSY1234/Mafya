import axios from "axios";

import { useState } from "react";
import { useEffect } from "react";
import { useHistory } from "react-router-dom";
import { API_URL } from "../../../common/api";
import AdminHeader from "../header/adminHeader";
import ReadonlyRow from "./ReadOnlyRow";
import classes from "./mainPage.module.css";

const MainPage = () => {
  const [students, setStudents] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const history = useHistory();
  const deleteHandler = (studentId) => {
    axios
      .delete(API_URL + `student/${studentId}`)
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
  useEffect(() => {
    axios
      .get(API_URL + "student")
      .then((res) => {
        setStudents(res.data.userList);
        setIsLoading(false);
      })
      .catch((err) => {
        alert("학생 정보를 불러오지 못했습니다.");
        setIsLoading(false);
      });
  }, []);
  return !isLoading && students ? (
    <div>
      <AdminHeader />
      <div>
        <table>
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
      </div>
    </div>
  ) : (
    <div>
      <AdminHeader />
      <span>학생 정보가 없습니다.</span>
    </div>
  );
};

export default MainPage;
