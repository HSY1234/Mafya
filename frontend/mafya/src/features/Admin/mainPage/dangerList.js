const DangerList = ({ dangerList }) => {
  const content = dangerList.length ? (
    dangerList.map((student) => (
      <li key={student.id}>
        <p>{student.name}</p>
        <p>{student.phoneNum}</p>
      </li>
    ))
  ) : (
    <span>결석한 학생이 없습니다.</span>
  );
  return (
    <div>
      <h3>결석 위험군</h3>
      <ul>{content}</ul>
    </div>
  );
};

export default DangerList;