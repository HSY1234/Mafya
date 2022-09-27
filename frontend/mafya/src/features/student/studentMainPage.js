import { useEffect } from "react";
import { useState } from "react";
import AttendInformation from "./attend/attendInformation";
import Calender from "./calendar/calender";
import StudentHeader from "./header/studentHeader";
import TeamMember from "./team/teamMember";
import styles from "./studentMainPage.module.css";
import CustomModal from "../../common/modal/modal";

const StudentMainPage = () => {
  const [month, setMonth] = useState(null);
  const [modalOpen, setModalOpen] = useState(false);
  const [messeges, setMesseges] = useState("");
  const [ids, setIds] = useState(null);
  const messegesHandler = (event) => {
    const tmpMessges = event.target.value;
    setMesseges(tmpMessges);
  };

  const openModal = (event) => {
    setModalOpen(true);
  };
  const closeModal = () => {
    setModalOpen(false);
    setMesseges("");
  };

  const mmsHandler = (event) => {
    setModalOpen(true);
  };

  const mmsTransferHandler = (event) => {
    event.preventDefault();
    const formData = { ids: [ids], messeges };
    console.log(formData);
    setModalOpen(false);
    setMesseges("");
    setIds(null);
  };

  useEffect(() => {
    let today = new Date();
    setMonth(today.getMonth() + 1);
  }, []);
  return (
    month && (
      <div className={styles.wholePage}>
        {/* <StudentHeader /> */}
        <CustomModal open={modalOpen} close={closeModal} header="Modal heading">
          <form onSubmit={mmsTransferHandler}>
            전송할 메시지를 입력하세요!
            <div>
              <input
                type="textarea"
                value={messeges}
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
        <div className={styles.inner}>
          <div className={styles.statusBox}>
            <div className={styles.attendInfoBox}>
              <AttendInformation month={month} />
            </div>
            <div className={styles.teamMemberBox}>
              <TeamMember mmsHandler={mmsHandler} setIds={setIds} />
            </div>
          </div>
          <div className={styles.calenderBox}>
            <Calender setMonth={setMonth} month={month} />
          </div>
        </div>
      </div>
    )
  );
};

export default StudentMainPage;
