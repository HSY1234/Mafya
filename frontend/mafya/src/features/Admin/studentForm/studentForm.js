import axios from "axios";
import { useState } from "react";
import { useRef } from "react";
import { useHistory, useLocation } from "react-router-dom";
import { API_URL } from "../../../common/api";
import AdminHeader from "../header/adminHeader";
// import classes from "./studentForm.module.css";

const StudentForm = () => {
  const location = useLocation();
  const history = useHistory();
  const student = location.state;
  const [name, setName] = useState(student ? student.name : "");
  const [userCode, setUserCode] = useState(student ? student.userCode : "");
  const [teamCode, setTeamCode] = useState(student ? student.teamCode : "");
  const [classCode, setClassCode] = useState(student ? student.classCode : "");
  const [phoneNum, setPhoneNum] = useState(student ? student.phoneNum : "");
  const [teamLeader, setTeamLeader] = useState(
    student ? student.teamLeader : null
  );
  const [isUserCodeUnique, setIsUserCodeUnique] = useState(
    student ? true : false
  );
  const [file, setFile] = useState(null);
  const [previewUrl, setPreviewURL] = useState(null);
  const fileRef = useRef();
  const nameChangeHandler = (event) => {
    const tempName = event.target.value;
    setName(tempName);
  };

  const userCodeChangeHandler = (event) => {
    const tempUserCode = event.target.value;
    setUserCode(tempUserCode);
  };

  const userCodeDupCheckHandler = async () => {
    const tempUserCode = userCode;
    if (userCode.trim() === "") {
      alert("학번을 입력해주세요");
      return;
    }
    axios
      .get(API_URL + `student/checkId/${tempUserCode}`)
      .then((res) => {
        if (res.data.resultCode == 0) {
          alert("사용 가능한 학번입니다.");
          setName((prevState) => name);
          setIsUserCodeUnique(() => true);
          document.getElementById("userCode").readOnly = true;
        } else if (res.data.resultCode == 1) {
          alert("이미 존재하는 학번입니다.");
          setIsUserCodeUnique(() => false);
          return;
        }
      })
      .catch((err) => {
        alert("에러 발생");
      });
  };

  const teamCodeChangeHandler = (event) => {
    const tempTeamCode = event.target.value;
    setTeamCode(tempTeamCode);
  };

  const classCodeChangeHandler = (event) => {
    const tempClassCode = event.target.value;
    setClassCode(tempClassCode);
  };

  const phoneNumChangeHandler = (event) => {
    const tempPhoneNum = event.target.value;
    setPhoneNum(tempPhoneNum);
  };

  const teamLeaderChangeHandler = (event) => {
    console.log(event);
    if (event.target.value === "true") {
      const tempTeamLeader = true;
      console.log(tempTeamLeader);
      setTeamLeader(tempTeamLeader);
    } else if (event.target.value === "false") {
      const tempTeamLeader = false;
      console.log(tempTeamLeader);
      setTeamLeader(tempTeamLeader);
    } else if (event.target.value === "null") {
      setTeamLeader(null);
    }
  };

  const handleFileOnChange = (event) => {
    event.preventDefault();
    let file = event.target.files[0];
    let reader = new FileReader();

    reader.onloadend = (e) => {
      setFile(file);
      setPreviewURL(reader.result);
    };
    if (file) reader.readAsDataURL(file);
  };

  // const handleFileButtonClick = (e) => {
  //   e.preventDefault();
  //   fileRef.current.click();
  // };

  const isTeamLeader = (value) => {
    console.log(typeof value);
    console.log(value);
    if (typeof value === "boolean") {
      return true;
    } else {
      return false;
    }
  };
  const formIsVaild =
    name &&
    userCode &&
    teamCode &&
    classCode &&
    phoneNum &&
    isUserCodeUnique &&
    isTeamLeader(teamLeader);

  const onSubmitHandler = async (event) => {
    event.preventDefault();
    const tmpStudentInfo = {
      name,
      userCode,
      teamCode,
      classCode,
      phoneNum,
      teamLeader,
      // file,
    };
    console.log(tmpStudentInfo);
    if (!student) {
      axios
        .post(API_URL + "student/", tmpStudentInfo, {
          headers: {
            "Content-Type": "application/json",
            // "Content-Type": "multipart/form-data",
          },
        })
        .then((res) => {
          alert("학생 정보 등록 완료");
          window.location.reload();
        })
        .catch((err) => {
          alert("학생 정보 등록 실패");
        });
    } else {
      axios
        .put(API_URL + `student/${student.id}`, tmpStudentInfo, {
          headers: {
            "Content-Type": "application/json",
            // "Content-Type": "multipart/form-data",
          },
        })
        .then((res) => {
          alert("학생 정보 수정 완료");
          history.push("/admin");
        })
        .catch((err) => {
          alert("학생 정보 수정 실패");
        });
    }
  };
  console.log(formIsVaild);
  return (
    // <div className={classes.v105_113}>
    //   <div className={classes.v105_123}>
    //     <span>학생 등록</span>
    //   </div>
    //   {/* <div className={classes.v105_123}></div> */}
    //   {/* <div className={classes.v105_124}></div> */}
    //   <div className={classes.v105_125}></div>
    //   {/* 이미지 공간 */}
    //   {/* <div className={classes.v107_143}></div> */}
    //   {/* <span className={classes.v107_147}>이번 달 지각</span>
    //   <span className={classes.v107_153}>특이 사항</span> */}
    //   {/* <div className={classes.v105_130}></div> */}
    //   <div className={classes.v105_134}></div>
    //   {/* <div className={classes.v107_144}></div> */}
    //   {/* <div className={classes.v107_152}></div> */}
    //   {/* <div className={classes.v107_145}></div> */}
    //   <div className={classes.v105_135}></div>
    //   <span className={classes.v105_136}>upload</span>
    //   {/* upload 버튼 */}

    //   <div className={classes.v107_150}></div>
    //   <span className={classes.v107_151}>Save</span>
    //   {/* save 버튼 */}
    //   <span className={classes.v105_137}>name</span>
    //   <span className={classes.v105_138}>age</span>
    //   <div className={classes.v105_139}></div>
    //   <span className={classes.v105_140}>team</span>
    // </div>

    <div>
      <AdminHeader />
      <div>
        <span>{student ? "학생 정보 수정" : "학생 정보 등록"}</span>
      </div>
      <form onSubmit={onSubmitHandler}>
        <h5>이름</h5>
        <div>
          <input
            type="text"
            id="name"
            value={name}
            placeholder="이름을 입력해 주세요"
            onChange={nameChangeHandler}
          />
        </div>
        <h5>학번</h5>
        <div>
          <input
            type="text"
            id="userCode"
            value={userCode}
            readOnly={student ? true : false}
            onChange={userCodeChangeHandler}
            placeholder="학번을 입력해 주세요"
          />
          <button onClick={userCodeDupCheckHandler} type="button">
            {student
              ? "수정 불가"
              : isUserCodeUnique
              ? "사용 가능"
              : "중복 확인"}
          </button>
        </div>
        <h5>팀 코드</h5>
        <div>
          <input
            type="text"
            id="teamCode"
            value={teamCode}
            onChange={teamCodeChangeHandler}
            placeholder="팀 코드를 입력해 주세요"
          />
        </div>
        <h5>반</h5>
        <div>
          <input
            type="text"
            id="classCode"
            value={classCode}
            onChange={classCodeChangeHandler}
            placeholder="반 정보를 입력해 주세요"
          />
        </div>
        <h5>핸드폰 번호</h5>
        <div>
          <input
            type="text"
            id="phoneNum"
            value={phoneNum}
            onChange={phoneNumChangeHandler}
            placeholder="연락처를 입력해 주세요"
          />
        </div>
        <h5>팀장 여부</h5>
        <div>
          <select defaultValue={teamLeader} onChange={teamLeaderChangeHandler}>
            <option key="default" value="null">
              팀장 여부 선택
            </option>
            <option key="teamLeader" value="true">
              팀장
            </option>
            <option key="teamMember" value="false">
              팀원
            </option>
          </select>
          {/* <input
            type="text"
            id="teamLeader"
            value={teamLeader}
            onChange={teamLeaderChangeHandler}
            placeholder="팀장 여부를 선택해 주세요"
          /> */}
        </div>
        <div>
          <h5>프로필 이미지</h5>
          <input
            type="file"
            accept="image/jpg,impge/png,image/jpeg,image/gif"
            name="profile_img"
            ref={fileRef}
            onChange={handleFileOnChange}
          ></input>
          {file && <img src={previewUrl} alt="preview" />}
        </div>
        <button disabled={!formIsVaild}>
          {student ? "정보 수정" : "정보 등록"}
        </button>
      </form>
    </div>
  );
};

export default StudentForm;
