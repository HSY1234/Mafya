import { useState } from "react"
import "./modal.css"

const CustomModal = (props) => {
  // 열기, 닫기, 모달 헤더 텍스트를 부모로부터 받아옴
  const { open, close, header } = props
  const handleChildElementClick = (e) => {
    e.stopPropagation()
    // Do other stuff here
  }

  return (
    // 모달이 열릴때 openModal 클래스가 생성된다.
    <div className={open ? "openModal modal" : "modal"} onClick={close}>
      {open ? (
        <section onClick={(e) => handleChildElementClick(e)}>
          <header>
            {header}
            <button className="close" onClick={close}>
              &times;
            </button>
          </header>
          <div className="modalIcon">
            <div className="modalIconBox">
              <span className="material-symbols-outlined">contact_mail</span>
            </div>
          </div>
          <main>{props.children}</main>
        </section>
      ) : null}
    </div>
  )
}

export default CustomModal
