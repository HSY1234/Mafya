const TeamMemberRow = ({ student }) => {
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

  const MmsHandler = () => {
    return;
  };
  return (
    <tr>
      <td>{student.name}</td>

      <td>{student.phoneNum}</td>
      <td>{status(student.attendanceStatus)}</td>
      <button onClick={MmsHandler}>전송</button>

      {/* 향후에 MMS 기능 넣으면 끝 */}
      {/* 
      <td>
        <button type="button" onClick={(event) => updateHandler(student)}>
          Edit
        </button>
      
      </td> */}
    </tr>
  );
};

export default TeamMemberRow;
