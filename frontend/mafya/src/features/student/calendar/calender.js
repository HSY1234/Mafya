import FullCalendar, { preventContextMenu } from "@fullcalendar/react";
import dayGridPlugin from "@fullcalendar/daygrid";
import { useState } from "react";
import { useEffect } from "react";
import EvenItem from "./evenItem";
import axios from "axios";
import { API_URL } from "../../../common/api";

const Calender = (props) => {
  const [events, setEvents] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [currentMonth, setCurrentMonth] = useState(null);
  // 달 이동 시에 state 변환
  useEffect(() => {
    const userCode = localStorage.getItem("userCode");
    axios.get(API_URL + `attendance/calendar/${userCode}`).then((res) => {
      const calenderInformation = res.data;
      const data = [];
      calenderInformation.forEach((dayInformation) => {
        if (dayInformation.enterTime !== "") {
          let enterInformation = {
            title: dayInformation.enterTime + " 입실",
            date: dayInformation.date,
            type: "입실",
          };
          data.push(enterInformation);
        }
        if (dayInformation.exitTime !== "") {
          let exitInformation = {
            title: dayInformation.exitTime + " 퇴실",
            date: dayInformation.date,
            type: "퇴실",
          };
          data.push(exitInformation);
        }
        const status = (attendanceStatus) => {
          if (attendanceStatus === 0) {
            return "입실";
          } else if (attendanceStatus === 10) {
            return "지각";
          } else if (attendanceStatus === 11) {
            return "조퇴";
          } else if (attendanceStatus === 12) {
            return "지각";
          } else if (attendanceStatus === 2) {
            return "조퇴";
          } else if (attendanceStatus === 3) {
            return "퇴실";
          } else if (attendanceStatus === 4) {
            return "오류";
          } else {
            return "결석";
          }
        };
        let typeInformation = {
          title: status(dayInformation.type),
          date: dayInformation.date,
          type: "현황",
        };
        data.push(typeInformation);
      });
      setEvents(data);
    });
    setIsLoading(false);
  }, []);

  // const handleClick = (event) => {
  //   event.preventDefault();
  //   console.log(event.dateStr);
  // };
  return (
    !isLoading && (
      <div>
        <FullCalendar
          defaultView="dayGridMonth"
          plugins={[dayGridPlugin]}
          weekends={true}
          events={events}
          eventContent={(info) => <EvenItem info={info} />}
          eventBackgroundColor={"#ffff"}
          eventBorderColor={"#ffff"}
          eventTextColor={"#000"}
        />
      </div>
    )
  );
};

export default Calender;
