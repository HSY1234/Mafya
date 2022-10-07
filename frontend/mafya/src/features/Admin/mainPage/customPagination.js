import styles from "./mainPage.module.css"

const CustomPagination = ({ total, limit, page, setPage }) => {
  // 올림 차수로 계산
  const numPages = Math.ceil(total / limit)

  return (
    <div className={styles.paigBox}>
      <button onClick={() => setPage(page - 1)} disabled={page === 1}>
        &lt;
      </button>
      {Array(numPages)
        .fill()
        .map((v, i) => (
          <button
            key={i + 1}
            onClick={() => setPage(i + 1)}
            aria-current={page === i + 1 ? "page" : null}
            className={page === i + 1 ? styles.onPage : styles.notOnPage}
          >
            <span>{i + 1}</span>
          </button>
        ))}
      <button onClick={() => setPage(page + 1)} disabled={page === numPages}>
        &gt;
      </button>
    </div>
  )
}

export default CustomPagination
