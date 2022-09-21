import AttendInformation from "./attend/attendInformation";
import Calender from "./calendar/calender";
import StudentHeader from "./header/studentHeader";
import TeamMember from "./team/teamMember";

const StudentMainPage = () => {
  return (
    <div>
      <StudentHeader />
      <AttendInformation />
      <TeamMember />
      <Calender />
    </div>
  );
};

export default StudentMainPage;
