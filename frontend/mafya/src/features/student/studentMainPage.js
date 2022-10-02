import { useEffect } from "react"
import { useState } from "react"
import AttendInformation from "./attend/attendInformation"
import Calender from "./calendar/calender"
import StudentHeader from "./header/studentHeader"
import TeamMember from "./team/teamMember"
import styles from "./studentMainPage.module.css"
import CustomModal from "../../common/modal/modal"
import axios1 from "../../common/api/axios"
import { API_URL } from "../../common/api"
import axios from "axios"

const StudentMainPage = () => {
  const [month, setMonth] = useState(null)
  const [modalOpen, setModalOpen] = useState(false)
  const [messages, setMessages] = useState("")
  const [ids, setIds] = useState(null)
  const [name, setName] = useState("")
  const [userId, setUserId] = useState("")
  const [isLoading, setIsLoading] = useState(true)
  const messegesHandler = (event) => {
    const tmpMessges = event.target.value
    setMessages(tmpMessges)
  }

  const openModal = (event) => {
    setModalOpen(true)
  }
  const closeModal = () => {
    setModalOpen(false)
    setMessages("")
  }

  const mmsHandler = (event) => {
    setModalOpen(true)
  }

  const mmsTransferHandler = (event) => {
    event.preventDefault()
    const formData = { ids: [ids], messages }
    console.log(formData)
    axios1
      .post(API_URL + "mms", formData, {
        headers: { accessToken: window.localStorage.getItem("token") },
      })
      .then((res) => {
        alert("성공")
        setModalOpen(false)
        setMessages("")
        setIds(null)
      })
      .catch((err) => {
        console.log(err)
      })
  }

  useEffect(() => {
    const userCode = localStorage.getItem("userCode")
    let today = new Date()
    setMonth(today.getMonth() + 1)
    axios
      .get(API_URL + `student/userCode/${userCode}`, {
        headers: {
          accessToken: window.localStorage.getItem("token"),
        },
      })
      .then((res) => {
        setUserId(res.data.userInfo.id)
        setName(res.data.userInfo.name)
      })
    setIsLoading(false)
  }, [])

  return (
    month && (
      <div>
        <CustomModal open={modalOpen} close={closeModal} header="">
          <form onSubmit={mmsTransferHandler}>
            <span>전송할 메시지를 입력하세요!</span>
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
        <div className={styles.wholePage}>
          {/* <StudentHeader /> */}
          <div className={styles.inner}>
            <div className={styles.statusBox}>
              <div className={styles.attendInfoBox}>
                <AttendInformation month={month} name={name} />
              </div>
              <div className={styles.teamMemberBox}>
                <TeamMember
                  mmsHandler={mmsHandler}
                  setIds={setIds}
                  userId={userId}
                />
              </div>
            </div>
            <div className={styles.calenderBox}>
              <Calender setMonth={setMonth} month={month} />
            </div>
          </div>
        </div>
      </div>
    )
  )
}

export default StudentMainPage
