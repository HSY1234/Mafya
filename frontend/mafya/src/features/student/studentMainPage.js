import { useEffect } from "react"
import { useState } from "react"
import AttendInformation from "./attend/attendInformation"
import Calender from "./calendar/calender"
import StudentHeader from "./header/studentHeader"
import TeamMember from "./team/teamMember"
import styles from "./studentMainPage.module.css"

const StudentMainPage = () => {
  const [month, setMonth] = useState(null)
  useEffect(() => {
    let today = new Date()
    setMonth(today.getMonth() + 1)
  }, [])
  return (
    month && (
      <div className={styles.wholePage}>
        <StudentHeader />
        <div className={styles.inner}>
          <div className={styles.statusBox}>
            <div className={styles.attendInfoBox}>
              <AttendInformation month={month} />
            </div>
            <div className={styles.teamMemberBox}>
              <TeamMember />
            </div>
          </div>
          <div className={styles.calenderBox}>
            <Calender setMonth={setMonth} month={month} />
          </div>
        </div>
      </div>
    )
  )
}

export default StudentMainPage
