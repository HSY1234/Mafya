import axios from "axios";
import { useCallback, useEffect, useMemo, useState } from "react";
import { useRef } from "react";
import { useHistory, useLocation } from "react-router-dom";
import { API_URL } from "../../../common/api";
import AdminHeader from "../header/adminHeader";
import styles from "./studentForm.module.css";

import Grid from "@material-ui/core/Grid";
import axios1 from "../../../common/api/axios";

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

  useEffect(() => {
    if (!student) {
      setName("");
      setUserCode("");
      setTeamCode("");
      setClassCode("");
      setPhoneNum("");
      setTeamLeader(false);
      setIsUserCodeUnique(false);
      setFile(null);
      setPreviewURL(null);
      const fileInput = document.querySelector('input[type="file"]');
      const dataTransfer = new DataTransfer();
      fileInput.files = dataTransfer.files;
    } else {
      axios
        .get(API_URL + `img/${student.userCode}`, {
          headers: {
            accessToken: window.localStorage.getItem("token"),
          },
        })
        .then(async (res) => {
          const url = res.data;
          setPreviewURL(url);
          const response = await fetch(url, {
            mode: "cors",
          });
          console.log(response);
          const data = await response.blob();
          console.log(data);
          const ext = url.split(".").pop();
          const filename = url.split("/").pop();
          const metadata = { type: `image/${ext}` };
          const tmpFile = new File([data], filename, metadata);
          setFile(tmpFile);
          const fileInput = document.querySelector('input[type="file"]');
          const dataTransfer = new DataTransfer();
          dataTransfer.items.add(tmpFile);
          fileInput.files = dataTransfer.files;
        });
    }
  }, [student]);
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
      alert("????????? ??????????????????");
      return;
    }
    axios
      .get(API_URL + `student/checkId/${tempUserCode}`, {
        headers: {
          accessToken: window.localStorage.getItem("token"),
        },
      })
      .then((res) => {
        if (res.data.resultCode === 0) {
          alert("?????? ????????? ???????????????.");
          setName((prevState) => name);
          setIsUserCodeUnique(() => true);
          document.getElementById("userCode").readOnly = true;
        } else if (res.data.resultCode === 1) {
          alert("?????? ???????????? ???????????????.");
          setIsUserCodeUnique(() => false);
          return;
        }
      })
      .catch((err) => {
        alert("?????? ??????");
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
    console.log(event.target);
    setTeamLeader(!teamLeader);
    // if (event.target.value === "true") {
    //   const tempTeamLeader = true
    //   console.log(tempTeamLeader)
    //   setTeamLeader(tempTeamLeader)
    // } else if (event.target.value === "false") {
    //   const tempTeamLeader = false
    //   console.log(tempTeamLeader)
    //   setTeamLeader(tempTeamLeader)
    // } else if (event.target.value === "null") {
    //   setTeamLeader(null)
    // }
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
    isTeamLeader(teamLeader) &&
    file;

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

    if (!student) {
      axios1
        .post(API_URL + "student/", tmpStudentInfo, {
          headers: {
            "Content-Type": "application/json",
            accessToken: window.localStorage.getItem("token"),
            // "Content-Type": "multipart/form-data",
          },
        })
        .then((res) => {
          console.log("?????? ?????? ?????? ??????");
        })
        .catch((err) => {
          alert("?????? ?????? ?????? ??????");
        });
      let formData = new FormData();
      formData.set("file", file);
      formData.set("userCode", userCode);
      axios1
        .post(API_URL + `img/register/${userCode}`, formData, {
          headers: {
            "Content-Type": "multipart/form-data",
            accessToken: window.localStorage.getItem("token"),
            // "Access-Control-Allow-Origin": "*",
          },
        })
        .then((res) => {
          alert("?????? ?????? ?????? ??????");
          window.location.reload();
        })
        .catch((err) => {
          alert("?????? ?????? ?????? ??????");
        });
    } else {
      axios1
        .put(API_URL + `student/${student.id}`, tmpStudentInfo, {
          headers: {
            "Content-Type": "application/json",
            accessToken: window.localStorage.getItem("token"),
            // "Content-Type": "multipart/form-data",
          },
        })
        .then((res) => {
          console.log("?????? ?????? ?????? ??????");
        })
        .catch((err) => {
          alert("?????? ?????? ?????? ??????");
        });

      let formData = new FormData();
      formData.set("file", file);
      formData.set("userCode", userCode);
      axios1
        .post(API_URL + `img/register/${userCode}`, formData, {
          headers: {
            "Content-Type": "multipart/form-data",
            accessToken: window.localStorage.getItem("token"),
            // "Access-Control-Allow-Origin": "*",
          },
        })
        .then((res) => {
          alert("?????? ?????? ?????? ??????");
          history.push("/admin");
        })
        .catch((err) => {
          alert("?????? ?????? ?????? ??????");
        });
    }
  };

  const backHandler = () => {
    history.push("/admin");
  };
  const resetButtonHandler = (event) => {
    event.preventDefault();
    setName("");
    setUserCode("");
    setTeamCode("");
    setClassCode("");
    setPhoneNum("");
    setTeamLeader(false);
    setIsUserCodeUnique(false);
    setFile(null);
    setPreviewURL(null);
  };
  return (
    // <div className={classes.v105_113}>
    //   <div className={classes.v105_123}>
    //     <span>?????? ??????</span>
    //   </div>
    //   {/* <div className={classes.v105_123}></div> */}
    //   {/* <div className={classes.v105_124}></div> */}
    //   <div className={classes.v105_125}></div>
    //   {/* ????????? ?????? */}
    //   {/* <div className={classes.v107_143}></div> */}
    //   {/* <span className={classes.v107_147}>?????? ??? ??????</span>
    //   <span className={classes.v107_153}>?????? ??????</span> */}
    //   {/* <div className={classes.v105_130}></div> */}
    //   <div className={classes.v105_134}></div>
    //   {/* <div className={classes.v107_144}></div> */}
    //   {/* <div className={classes.v107_152}></div> */}
    //   {/* <div className={classes.v107_145}></div> */}
    //   <div className={classes.v105_135}></div>
    //   <span className={classes.v105_136}>upload</span>
    //   {/* upload ?????? */}

    //   <div className={classes.v107_150}></div>
    //   <span className={classes.v107_151}>Save</span>
    //   {/* save ?????? */}
    //   <span className={classes.v105_137}>name</span>
    //   <span className={classes.v105_138}>age</span>
    //   <div className={classes.v105_139}></div>
    //   <span className={classes.v105_140}>team</span>
    // </div>

    <div className={styles.wholePage}>
      <AdminHeader onPage={2} />
      <div className={styles.firstPageBox}>
        <div className={styles.overlay}>
          <form onSubmit={onSubmitHandler}>
            <Grid container spacing={2}>
              <Grid item xs={4} className={styles.imagePosition}>
                <div>
                  <div className={styles.imagePosition}>
                    <div className={styles.imagePlace}>
                      <div className={styles.cutImage}>
                        <img
                          className={styles.imageBox}
                          src={previewUrl}
                          alt=""
                          onClick={() => {
                            fileRef.current.click();
                          }}
                        />
                      </div>
                      <input
                        type="file"
                        accept="image/jpg,impge/png,image/jpeg,image/gif"
                        name="profile_img"
                        id="file"
                        ref={fileRef}
                        style={{ display: "none" }}
                        onChange={handleFileOnChange}
                      ></input>
                    </div>
                  </div>
                  <div>
                    <input
                      className={`${styles.tgl} ${styles.tglskewed}`}
                      id="cb5"
                      type="checkbox"
                      checked={teamLeader}
                      onChange={teamLeaderChangeHandler}
                    />
                    <label
                      className={styles.tglbtn}
                      data-tg-off="??????"
                      data-tg-on="??????"
                      for="cb5"
                    ></label>
                  </div>
                </div>
              </Grid>
              <Grid item xs={8}>
                {/* <div className={styles.createTitle}>
                  <span>{student ? "?????? ?????? ??????" : "?????? ?????? ??????"}</span>
                </div> */}
                <span> &nbsp;&nbsp;??????</span>
                <div>
                  <input
                    type="text"
                    id="name"
                    value={name}
                    placeholder="??????"
                    onChange={nameChangeHandler}
                    className={styles.userInput}
                  />
                </div>
                <div className={styles.spacingLine}>
                  <span className={styles.userInputSpan}>&nbsp;&nbsp;??????</span>
                </div>
                <Grid container spacing={3} className={styles.updown}>
                  <Grid item xs={9}>
                    <input
                      type="text"
                      id="userCode"
                      value={userCode}
                      readOnly={student ? true : false}
                      onChange={userCodeChangeHandler}
                      placeholder="??????"
                      className={styles.userCodeInput}
                    />
                  </Grid>

                  <Grid item xs={3}>
                    <button
                      className={
                        isUserCodeUnique
                          ? styles.userCodeBtnsFalse
                          : styles.userCodeBtns
                      }
                      onClick={userCodeDupCheckHandler}
                      type="button"
                    >
                      {student
                        ? "?????? ??????"
                        : isUserCodeUnique
                        ? "?????? ??????"
                        : "?????? ??????"}
                    </button>
                  </Grid>
                </Grid>
                <Grid container spacing={5}>
                  <Grid item xs={2}>
                    <span>&nbsp;&nbsp;&nbsp;???</span>
                    <input
                      type="text"
                      id="classCode"
                      value={classCode}
                      onChange={classCodeChangeHandler}
                      placeholder="???"
                      className={styles.userInput}
                    />
                  </Grid>
                  <Grid item xs={10}>
                    <span>&nbsp;&nbsp;??? ??????</span>
                    <input
                      type="text"
                      id="teamCode"
                      value={teamCode}
                      onChange={teamCodeChangeHandler}
                      placeholder="??? ??????"
                      className={styles.userTeamInput}
                    />
                  </Grid>
                </Grid>
                <div className={styles.spacingLine}>
                  <span>&nbsp;&nbsp;????????? ??????</span>
                </div>
                <div>
                  <input
                    type="text"
                    id="phoneNum"
                    value={phoneNum}
                    onChange={phoneNumChangeHandler}
                    placeholder="- ?????? ???????????????"
                    className={styles.userInput}
                  />
                </div>
                {/* <div>
                  <input
                    type="radio"
                    name="teamLeader"
                    onChange={teamLeaderChangeHandler}
                    value="true"
                  />
                  {"??????"}
                  <input
                    type="radio"
                    name="teamLeader"
                    onChange={teamLeaderChangeHandler}
                    value="false"
                  />
                  {"??????"}
                </div> */}
                <Grid container>
                  <Grid item xs={12} className={styles.centered}>
                    <button
                      disabled={!formIsVaild}
                      className={
                        formIsVaild
                          ? styles.registerBtn
                          : styles.registerBtnFalse
                      }
                    >
                      {student ? "?????? ??????" : "Register"}
                    </button>
                  </Grid>
                  <Grid item xs={6} className={styles.centered}>
                    <button
                      onClick={resetButtonHandler}
                      className={styles.resetBtn}
                    >
                      {" "}
                      Reset{" "}
                    </button>
                  </Grid>
                  <Grid item xs={6} className={styles.centered}>
                    <button onClick={backHandler} className={styles.backBtn}>
                      {" "}
                      Back{" "}
                    </button>
                  </Grid>
                </Grid>
              </Grid>
            </Grid>
          </form>
        </div>
      </div>
    </div>
  );
};

export default StudentForm;
