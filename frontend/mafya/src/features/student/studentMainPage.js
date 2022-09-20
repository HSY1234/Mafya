import AttendInformation from "./attendInformation";
import Calender from "./calender";
import TeamMember from "./teamMember";

const StudentMainPage = () => {
  return (
    <div>
      <AttendInformation />
      <TeamMember />
      <Calender />
    </div>
  );
};

export default StudentMainPage;
