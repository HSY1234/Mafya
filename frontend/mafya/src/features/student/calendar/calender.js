import FullCalendar from "@fullcalendar/react";
import dayGridPlugin from "@fullcalendar/daygrid";
import { useState } from "react";
import { useEffect } from "react";
import EvenItem from "./evenItem";

const Calender = () => {
  const [events, setEvents] = useState([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    setEvents([
      { title: "08 : 32 입실", date: "2022-09-21", id: 0, type: "입실" },
      { title: "18 : 00 퇴실", date: "2022-09-21", id: 1, type: "퇴실" },
      { title: "출석", date: "2022-09-21", id: 2, type: "현황" },
      { title: "08 : 32 입실", date: "2022-09-20", id: 3, type: "입실" },
      { title: "18 : 00 퇴실", date: "2022-09-20", id: 4, type: "퇴실" },
      { title: "출석", date: "2022-09-20", id: 5, type: "현황" },
    ]);
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
