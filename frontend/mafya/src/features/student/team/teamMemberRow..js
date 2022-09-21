const TeamMemberRow = ({ student }) => {
  return (
    <tr>
      <td>{student.name}</td>

      <td>{student.phoneNum}</td>
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
