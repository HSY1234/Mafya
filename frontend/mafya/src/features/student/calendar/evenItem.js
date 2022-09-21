const EvenItem = ({ info }) => {
  const { event } = info;
  console.log(event._def.extendedProps.type);
  const type = event._def.extendedProps.type;
  // 이걸로 조건부 랜더링 하면 될듯함.
  // 입실, 퇴실, 현황

  return (
    <div>
      <p>{event.title}</p>
    </div>
  );
};

export default EvenItem;
