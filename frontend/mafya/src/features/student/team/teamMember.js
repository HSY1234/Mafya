import { useState } from "react";
import { useEffect } from "react";
import TeamMemberRow from "./teamMemberRow.";
import styles from "./teamMember.module.css";

const TeamMember = () => {
  const [team, setTeam] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  useEffect(() => {
    setTeam([
      { id: "0", name: "박세현", phoneNum: "010456789" },
      { id: "1", name: "홍성영", phoneNum: "010456789" },
      { id: "2", name: "홍제민", phoneNum: "010456789" },
      { id: "3", name: "김병수", phoneNum: "010456789" },
      { id: "4", name: "김주한", phoneNum: "010456789" },
    ]);
    setIsLoading(false);
  }, []);

  return (
    !isLoading && (
      <div>
        <h3>팀원 현황</h3>
        <table className={styles.table}>
          <thead>
            <tr>
              <th>이름</th>
              <th>휴대폰 번호</th>
            </tr>
          </thead>
          <tbody>
            {team.map((student) => {
              return <TeamMemberRow student={student} />;
            })}
          </tbody>
        </table>
      </div>
    )
  );
};

export default TeamMember;
