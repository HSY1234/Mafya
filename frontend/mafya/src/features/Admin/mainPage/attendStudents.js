const AttendStudents = ({ attList }) => {
  const content = attList.length ? (
    attList.map((student) => (
      <li key={student.id}>
        <p>{student.name}</p>
        <p>{student.phoneNum}</p>
      </li>
    ))
  ) : (
    <span>출석한 학생이 없습니다.</span>
  );
  return (
    <div>
      <h3>출석</h3>
      <ul>{content}</ul>
    </div>
  );
};

export default AttendStudents;
