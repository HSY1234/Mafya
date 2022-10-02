import animation from "./spinner.gif";

const Spinner = () => {
  return (
    <div>
      Loading....
      <img src={animation} alt="로딩중!"></img>
    </div>
  );
};

export default Spinner;
