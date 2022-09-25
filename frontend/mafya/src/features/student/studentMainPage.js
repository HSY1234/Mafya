import { useEffect } from "react";
import { useState } from "react";
import AttendInformation from "./attend/attendInformation";
import Calender from "./calendar/calender";
import StudentHeader from "./header/studentHeader";
import TeamMember from "./team/teamMember";

const StudentMainPage = () => {
  const [month, setMonth] = useState(null);
  useEffect(() => {
    let today = new Date();
    setMonth(today.getMonth() + 1);
  }, []);
  return (
    month && (
      <div>
        <StudentHeader />
        <AttendInformation month={month} />}
        <TeamMember />
        <Calender setMonth={setMonth} month={month} />
      </div>
    )
  );
};

export default StudentMainPage;
