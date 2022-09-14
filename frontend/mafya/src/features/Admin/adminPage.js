import axios from "axios";
import { useState } from "react";
import { useEffect } from "react";
import { API_URL } from "../../common/api";
import AdminHeader from "./header/adminHeader";

const AdminPage = () => {
  const [students, setStudents] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  useEffect(() => {
    axios
      .get(API_URL + "student")
      .then((res) => {
        setStudents(res.data.userList);
        setIsLoading(false);
      })
      .catch((err) => {
        alert("학생 정보를 불러오지 못했습니다.");
      });
  }, []);
  return !isLoading ? (
    <div>
      <AdminHeader />
    </div>
  ) : (
    <div>
      <AdminHeader />
    </div>
  );
};

export default AdminPage;
