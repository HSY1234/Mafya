import axios from "axios";

import { useState } from "react";
import { useEffect } from "react";
import { useHistory } from "react-router-dom";
import { API_URL } from "../../../common/api";
import AdminHeader from "../header/adminHeader";
import ReadonlyRow from "./ReadOnlyRow";
import styles from "./mainPage.module.css";
import AttendStudents from "./attendStudents";
import NotAttendStudents from "./notAttendStudents";

const MainPage = () => {
  const [students, setStudents] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [attList, setAttList] = useState([]);
  const [notAttList, setNotAttList] = useState([]);
  const [classSearch, setClassSearch] = useState("");
  const [teamSearch, setTeamSearch] = useState("");
  const [filterStudents, setFilterStudents] = useState([]);
  const [filterAttList, setFilterAttList] = useState([]);
  const [notAttdatas, setNotAttDatas] = useState([]);
  const [scrollOptions, setScrollOptions] = useState({
    childLength: 5,
    fullHeight: 0,
  });

  const [filterNotAttList, setFilterNotAttList] = useState([]);

  useEffect(() => {
    setNotAttDatas(filterNotAttList.slice(0, scrollOptions.childLength));
  }, [filterNotAttList, scrollOptions.childLength]);
  // useEffect(()=>{
  //   setFilterAttList(attList)
  //   setFilterNotAttList(notAttList)
  // }, [attList, notAttList])

  const history = useHistory();
  const deleteHandler = (studentId) => {
    axios
      .delete(API_URL + `student/${studentId}`)
      .then((res) => {
        const newStudents = [...students];
        const index = students.findIndex((stduent) => stduent.id === studentId);
        newStudents.splice(index, 1);
        setStudents(newStudents);
        const newIndex = filterStudents.findIndex(
          (stduent) => stduent.id === studentId
        );
        const newFilterStudents = [...filterStudents];
        if (newIndex !== -1) {
          newFilterStudents.splice(newIndex, 1);
          setFilterStudents(newFilterStudents);
        }
        const newAttList = [...attList];
        const attIndex = attList.findIndex(
          (stduent) => stduent.id === studentId
        );
        if (attIndex == -1) {
          const newNotAttList = [...notAttList];
          const notAttIndex = notAttList.findIndex(
            (stduent) => stduent.id === studentId
          );
          newNotAttList.splice(notAttIndex, 1);
          const newNotFilterAttList = [...filterNotAttList];
          const notfilterAttIndex = filterNotAttList.findIndex(
            (stduent) => stduent.id === studentId
          );
          if (notfilterAttIndex !== -1) {
            newNotFilterAttList.splice(notfilterAttIndex, 1);
            setFilterNotAttList(newNotFilterAttList);
          }
          setNotAttList(newNotAttList);
        } else {
          const newFilterAttList = [...filterAttList];
          const filterAttIndex = filterAttList.findIndex(
            (stduent) => stduent.id === studentId
          );
          if (filterAttIndex !== -1) {
            newFilterAttList.splice(filterAttIndex, 1);
            setFilterAttList(newFilterAttList);
          }
          newAttList.splice(attIndex, 1);
          setAttList(newAttList);
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
  useEffect(() => {
    axios
      .get(API_URL + "student")
      .then((res) => {
        setStudents(res.data.userList);
        setFilterStudents(res.data.userList);
      })
      .catch((err) => {
        alert("학생 정보를 불러오지 못했습니다.");
      });
    setIsLoading(true);
    axios
      .get(API_URL + "student/attend")
      .then((res) => {
        setAttList(res.data.attList);
        setNotAttList(res.data.notAttList);
        setFilterAttList(res.data.attList);
        setFilterNotAttList(res.data.notAttList);
      })
      .catch((err) => {
        alert("출석 정보를 불러오지 못했습니다.");
      });
    setIsLoading(false);
  }, []);

  const searchClassHandler = (event) => {
    event.preventDefault();
    if (classSearch == null || classSearch.trim() === "") {
      setFilterAttList(attList);
      setFilterNotAttList(notAttList);
      setFilterStudents(students);
    } else {
      const filterList = students.filter((student) =>
        student.classCode.includes(classSearch)
      );
      const fiterAtt = attList.filter((student) =>
        student.classCode.includes(classSearch)
      );
      const filterNotAtt = notAttList.filter((student) =>
        student.classCode.includes(classSearch)
      );
      setFilterStudents(filterList);
      setFilterAttList(fiterAtt);
      setFilterNotAttList(filterNotAtt);
    }
    setClassSearch("");
  };

  const searchTeamHandler = (event) => {
    event.preventDefault();
    if (teamSearch == null || teamSearch.trim() === "") {
      setFilterAttList(attList);
      setFilterNotAttList(notAttList);
      setFilterStudents(students);
    } else {
      const filterList = students.filter((student) =>
        student.teamCode.includes(teamSearch)
      );
      const fiterAtt = attList.filter((student) =>
        student.teamCode.includes(teamSearch)
      );
      const filterNotAtt = notAttList.filter((student) =>
        student.teamCode.includes(teamSearch)
      );
      setFilterStudents(filterList);
      setFilterAttList(fiterAtt);
      setFilterNotAttList(filterNotAtt);
    }
    setTeamSearch("");
  };
  const classSearchHandler = (event) => {
    event.preventDefault();
    setClassSearch(event.target.value);
  };

  const teamSearchHandler = (event) => {
    event.preventDefault();
    setTeamSearch(event.target.value);
  };

  return !isLoading && filterStudents.length ? (
    <div>
      <AdminHeader />
      <AttendStudents attList={filterAttList} />
      <NotAttendStudents
        notAttList={filterNotAttList}
        notAttDatas={notAttdatas}
        scrollOptions={scrollOptions}
        setScrollOptions={setScrollOptions}
      />

      <div>
        <form onSubmit={searchClassHandler}>
          <input
            type="text"
            value={classSearch}
            placeholder="반을 입력하세요"
            onChange={classSearchHandler}
          ></input>
          <button type="submit">검색</button>
        </form>
        <form onSubmit={searchTeamHandler}>
          <input
            type="text"
            value={teamSearch}
            placeholder="팀 코드를 입력하세요"
            onChange={teamSearchHandler}
          ></input>
          <button type="submit">검색</button>
        </form>
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
            {filterStudents.map((student) => {
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
      <AttendStudents attList={filterAttList} />
      <NotAttendStudents notAttList={filterNotAttList} />
      <form onSubmit={searchClassHandler}>
        <input
          type="text"
          value={classSearch}
          placeholder="반을 입력하세요"
          onChange={classSearchHandler}
        ></input>
        <button type="submit">검색</button>
      </form>
      <form onSubmit={searchTeamHandler}>
        <input
          type="text"
          value={teamSearch}
          placeholder="반을 입력하세요"
          onChange={teamSearchHandler}
        ></input>
        <button type="submit">검색</button>
      </form>
      <span>학생 정보가 없습니다.</span>
    </div>
  );
};

export default MainPage;
