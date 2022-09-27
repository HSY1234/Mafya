import axios from "axios"
import { useEffect } from "react"
import { useState } from "react"
import { API_URL } from "../../../common/api"
import axios1 from "../../../common/api/axios"
import styles from "./attendInformation.module.css"
import styled from "@emotion/styled"

const AnimatedCircle = styled.circle`
  animation: circle-fill-animation 2s ease;

  @keyframes circle-fill-animation {
    0% {
      stroke-dasharray: 0 ${2 * Math.PI * 90};
    }
  }
`
const AttendInformation = (props) => {
  const [information, setInformation] = useState({})
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    const userCode = localStorage.getItem("userCode")

    axios1
      .get(API_URL + `attendance/situation/${userCode}/${props.month}`, {
        headers: {
          accessToken: window.localStorage.getItem("token"),
        },
      })
      .then((res) => {
        setInformation(res.data)
      })
    setIsLoading(false)
  }, [])

  return (
    !isLoading && (
      <div>
        <h3>{props.month}월 출결 현황</h3>
        <div className={styles.attendOverlay}>
          <div className={styles.leftSide}>
            <div className={styles.userNameBox}>
              <p>
                <span className={styles.userName}>홍제민</span> 님
              </p>
            </div>
            <div>
              <div className={styles.percentageBox}>
                <p className={styles.percentageP}>NN%</p>
              </div>
              <div style={{ width: "170px", height: "170px" }}>
                <svg viewBox="0 0 200 200">
                  <circle
                    cx="100"
                    cy="100"
                    r="78"
                    fill="none"
                    stroke="white"
                    strokeWidth="1"
                  />
                  <circle
                    cx="100"
                    cy="100"
                    r="90"
                    fill="none"
                    stroke="rgba(255, 255, 255, 0.2)"
                    strokeWidth="20"
                  />
                  <AnimatedCircle
                    cx="100"
                    cy="100"
                    r="90"
                    fill="none"
                    stroke="white"
                    strokeWidth="20"
                    strokeDasharray={`${2 * Math.PI * 90 * 0.6} ${
                      2 * Math.PI * 90 * 0.4
                    }`}
                    strokeDashoffset={2 * Math.PI * 90 * 0.25}
                  />
                </svg>
              </div>
            </div>
            <div className={styles.logoutBtnBox}>
              <button className={styles.logoutBtn}>
                <span class="material-symbols-outlined">power_rounded</span>
              </button>
            </div>
          </div>
          <div className={styles.rightSide}>
            <div className={styles.attendOneFloor}>
              <div>
                <p>출석수</p>
                <div>{information.totalAttend}</div>
              </div>
              <div>
                <p>교육 일수</p>
                <div>{information.totalDay}</div>
              </div>
            </div>
            <div className={styles.attendTwoFloor}>
              <div>
                <p>지각</p>
                <div>{information.trady}</div>
              </div>
              <div>
                <p>결석</p>
                <div>{information.absent}</div>
              </div>
            </div>
          </div>
        </div>
        {/* <table className={styles.table}>
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
        </table> */}
      </div>
    )
  )
}

export default AttendInformation
