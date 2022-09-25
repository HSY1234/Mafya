import axios from "axios";
import { useEffect } from "react";
import { useState } from "react";
import { API_URL } from "../../../common/api";
import axios1 from "../../../common/api/axios";
import styles from "./attendInformation.module.css";
const AttendInformation = (props) => {
  const [information, setInformation] = useState({});
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const userCode = localStorage.getItem("userCode");

    axios1
      .get(API_URL + `attendance/situation/${userCode}/${props.month}`, {
        headers: {
          accessToken: window.localStorage.getItem("token"),
        },
      })
      .then((res) => {
        setInformation(res.data);
      });
    setIsLoading(false);
  }, []);

  return (
    !isLoading && (
      <div>
        <h3>{props.month}월 출결 현황</h3>
        <table className={styles.table}>
          <thead>
            <tr>
              <th>출석</th>
              <th>결석</th>
              <th>지각</th>
              <th>교육 지원금</th>
              <th>교육 일수</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>{information.totalAttend}</td>
              <td>{information.absent}</td>
              <td>{information.trady}</td>
              <td>{information.money}</td>
              <td>{information.totalDay}</td>
            </tr>
          </tbody>
        </table>
      </div>
    )
  );
};

export default AttendInformation;
