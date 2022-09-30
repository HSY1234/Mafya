import styles from "./calender.module.css"

const EvenItem = ({ info }) => {
  const { event } = info

  const type = event._def.extendedProps.type
  // 이걸로 조건부 랜더링 하면 될듯함.
  // 입실, 퇴실, 현황
  // console.log(event)
  return (
    <div
      className={
        type === "입실"
          ? styles.eventSpanAttend
          : type === "퇴실"
          ? styles.eventSpanAbscent
          : styles.eventSpanStatus
      }
    >
      <div
        className={
          type === "현황"
            ? event.title === "결석"
              ? styles.abscentColor
              : (event.title === "지각") | (event.title === "조퇴")
              ? styles.tardyColor
              : styles.attendColor
            : styles.none
        }
      >
        {event.title}
      </div>
    </div>
  )
}

export default EvenItem
