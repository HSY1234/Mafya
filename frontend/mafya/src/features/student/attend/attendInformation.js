import { useEffect } from "react";
import { useState } from "react";
import styles from "./attendInformation.module.css";
const AttendInformation = () => {
  const [information, setInformation] = useState({});
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    setInformation({ attend: 15, notAttend: 3, late: 2, out: 1 });
    setIsLoading(false);
  }, []);

  return (
    !isLoading && (
      <div>
        <h3>월별 출결 현황</h3>
        <table className={styles.table}>
          <thead>
            <tr>
              <th>출석</th>
              <th>결석</th>
              <th>지각</th>
              <th>외출</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>{information.attend}</td>
              <td>{information.notAttend}</td>
              <td>{information.late}</td>
              <td>{information.out}</td>
            </tr>
          </tbody>
        </table>
      </div>
    )
  );
};

export default AttendInformation;
