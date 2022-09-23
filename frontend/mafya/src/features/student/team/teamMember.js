import { useState } from "react";
import { useEffect } from "react";
import TeamMemberRow from "./teamMemberRow.";
import styles from "./teamMember.module.css";
import axios from "axios";
import { API_URL } from "../../../common/api";

const TeamMember = () => {
  localStorage.getItem("teamCode");

  const [team, setTeam] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  useEffect(() => {
    const teamCode = localStorage.getItem("teamCode");
    axios.get(API_URL + `attendance/team/${teamCode}`).then((res) => {
      const data = res.data;
      setTeam(data);
    });

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
