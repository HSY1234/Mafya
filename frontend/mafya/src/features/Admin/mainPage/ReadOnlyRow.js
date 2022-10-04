const ReadonlyRow = ({ student, deleteHandler, updateHandler }) => {
  return (
    <tr>
      <td>{student.date.substr(5)}</td>
      <td>{student.classCode}</td>
      <td>{student.teamCode}</td>
      <td>{student.userCode}</td>
      <td>{student.name}</td>
      <td>
        {" "}
        {student.phoneNum.slice(0, 3)}-{student.phoneNum.slice(3, 7)}-
        {student.phoneNum.slice(7, 11)}
      </td>
      <td>{student.teamLeader ? "팀장" : "팀원"}</td>
      <td>{student.absent}</td>
      <td>{student.trady}</td>
      <td>{student.trace}</td>
      <td>
        <button type="button" onClick={(event) => updateHandler(student)}>
          <span class="material-symbols-outlined">edit</span>
        </button>
        <button type="button" onClick={() => deleteHandler(student.id)}>
          <span class="material-symbols-outlined">delete</span>
        </button>
      </td>
    </tr>
  );
};

export default ReadonlyRow;
