import animation from "./spinner.gif"
import styles from "./spinner.module.css"

const Spinner = () => {
  return (
    <div>
      <div class={styles.load3}>
        <div class={styles.line}></div>
        <div class={styles.line}></div>
        <div class={styles.line}></div>
        <div class={styles.line}></div>
      </div>
    </div>
  )
}

export default Spinner
