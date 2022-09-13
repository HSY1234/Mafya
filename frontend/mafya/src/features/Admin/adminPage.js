import axios from "axios";
import { useState } from "react";
import { useRef } from "react";
import { API_URL } from "../../common/api";
import classes from "./adminPage.module.css";

const AdminPage = () => {
  const [name, setName] = useState("");
  const [userCode, setUserCode] = useState("");
  const [teamCode, setTeamCode] = useState("");
  const [classCode, setClassCode] = useState("");
  const [phoneNum, setPhoneNum] = useState("");
  const [teamLeader, setTeamLeader] = useState("");
  const [isUserCodeUnique, setIsUserCodeUnique] = useState(false);

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
        if (res.status == 0) {
          alert("사용 가능한 학번입니다.");
          setName((prevState) => name);
          setIsUserCodeUnique(() => true);
        } else if (res.status == 1) {
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
    const tempTeamLeader = event.target.value;
    setTeamLeader(tempTeamLeader);
  };

  const formIsVaild =
    name && userCode && teamCode && classCode && phoneNum && teamLeader;
  // && isUserCodeUnique;

  const onSubmitHandler = async (event) => {
    event.preventDefault();
    const tmpStudentInfo = {
      name,
      userCode,
      teamCode,
      classCode,
      phoneNum,
      teamLeader,
    };
    console.log(tmpStudentInfo);

    axios
      .post(API_URL + "student/", tmpStudentInfo, {
        headers: {
          "Content-Type": "application/json",
        },
      })
      .then((res) => {
        alert("학생 정보 등록 완료");
      })
      .catch((err) => {
        alert("학생 정보 등록 실패");
      });
  };
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
      <div>
        <span>학생 정보 등록</span>
      </div>
      <form onSubmit={onSubmitHandler}>
        <h5>이름</h5>
        <div>
          <input
            type="text"
            id="name"
            value={name}
            defaultValue=""
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
            onChange={userCodeChangeHandler}
            placeholder="학번을 입력해 주세요"
          />
          <button onClick={userCodeDupCheckHandler} type="button">
            {isUserCodeUnique ? "사용 가능" : "중복 확인"}
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
          <input
            type="text"
            id="teamLeader"
            value={teamLeader}
            onChange={teamLeaderChangeHandler}
            placeholder="팀장 여부를 선택해 주세요"
          />
        </div>
        <button disabled={!formIsVaild}>정보 등록</button>
      </form>
    </div>
  );
};

export default AdminPage;
