import { useRef, useState, useEffect } from "react"
import * as tf from "@tensorflow/tfjs"
import * as cocossd from "@tensorflow-models/coco-ssd"
import Webcam from "react-webcam"

import { drawRect } from "./utilities"
import axios from "axios"
import { API_URL } from "../../common/api"
import styles from "./webcam.module.css"

const Swal = require("sweetalert2")

function Camera() {
  const webcamRef = useRef(null)
  const canvasRef = useRef(null)
  const [timerId, setTimerId] = useState(null)
  const [humanDetacting, setHumanDetacting] = useState(false)
  const [faceDetacting, setFaceDetacting] = useState(false)
  const [model, setModel] = useState(null)
  const [sentence, setSentence] = useState("")
  const [userCode, setUserCode] = useState("")

  // Main function
  // const runCocoSsd = async () => {
  //   const net = await cocossd.load();
  //   console.log("모델 업로드 끝");
  //   //  Loop and detect hands
  //   if (!maskLoading){
  //     setInterval(() => {
  //       detect(net);
  //     }, 500);

  //   }
  // };

  //   const dialog = (humanDetacting, faceDetacting, maskConfirm=false) => {
  //     let announcement = ''
  //     if (humanDetacting){
  //         announcement = '탐지 되었습니다. 마스크를 벗어주세요.'
  //     }
  //     else if(faceDetacting){
  //         announcement = '인식되었습니다. 마스크를 착용해주세요'
  //     }
  //     else if(humanDetacting && faceDetacting && maskConfirm){
  //         announcement = '김무종 님 출석 되었습니다.'
  //     }
  //     setSentence(announcement)
  // }

  const dataURLtoFile = (dataurl, fileName) => {
    var arr = dataurl.split(","),
      mime = arr[0].match(/:(.*?);/)[1],
      bstr = atob(arr[1]),
      n = bstr.length,
      u8arr = new Uint8Array(n)

    while (n--) {
      u8arr[n] = bstr.charCodeAt(n)
    }

    return new File([u8arr], fileName, { type: mime })
  }

  const detectMask = (userCode) => {
    setTimeout(() => {
      const imageUrl = webcamRef.current.getScreenshot()
      console.log(imageUrl)
      let imageFile = dataURLtoFile(imageUrl, "test1.jpeg")
      console.log(imageFile)
      let formData = new FormData()
      formData.set("file", imageFile)
      console.log(imageFile)
      formData.set("userCode", userCode)
      // for (let key of formData.keys()) {
      //   console.log(key);
      // }
      // for (let value of formData.values()) {
      //   console.log(value);
      // }
      axios
        .post(API_URL + "img/mask/", formData, {
          headers: {
            "Content-Type": "multipart/form-data",
            // "Access-Control-Allow-Origin": "*",
          },
        })
        .then((res) => {
          if (res.data.status === "0") {
            setSentence(`${res.data.name}님 출석되었습니다.`)
            Swal.fire({
              icon: "success",
              title: `${res.data.name}님 출석되었습니다.`,
              showConfirmButton: false,
              timer: 1500,
            })
            setUserCode("")

            setTimeout(() => {
              setSentence("")
              setHumanDetacting(false)
              setFaceDetacting(false)
            }, 2000)
          } else if (res.data.status === "1") {
            setHumanDetacting(false)
            setFaceDetacting(false)
            setUserCode("")
            setSentence(
              `${res.data.name}님 출석되었습니다. 마스크를 좀 더 올려주세요`
            )
            // 모달 창
            Swal.fire({
              icon: "error",
              title: "오류",
              text: "마스크를 착용하지 않았습니다.",
              showConfirmButton: false,
              timer: 1500,
            })
            setTimeout(() => {
              setSentence("")
            }, 2000)
          } else if (res.data.status === "2") {
            setHumanDetacting(false)
            setFaceDetacting(false)
            setUserCode("")
            setSentence("마스크를 착용하지 않았습니다. 처음부터 부탁드립니다.")
            // 모달 창
            Swal.fire({
              icon: "error",
              title: "오류",
              text: "마스크를 착용하지 않았습니다.",
              showConfirmButton: false,
              timer: 1500,
            })
            setTimeout(() => {
              setSentence("")
            }, 2000)
          } else {
            setHumanDetacting(false)
            setFaceDetacting(false)
            setSentence("")
            setSentence("서버 에러")
            setUserCode("")

            setTimeout(() => {
              setSentence("")
            }, 2000)
          }
        })
        .catch((err) => {
          console.log(err.message)
          setHumanDetacting(false)
          setFaceDetacting(false)
          setSentence("")
          setUserCode("")
        })
    }, 2000)
  }
  const detect = async (net) => {
    // Check data is available
    if (
      typeof webcamRef.current !== "undefined" &&
      webcamRef.current !== null &&
      webcamRef.current.video.readyState === 4
    ) {
      // Get Video Properties
      const video = webcamRef.current.video
      const videoWidth = webcamRef.current.video.videoWidth
      const videoHeight = webcamRef.current.video.videoHeight

      // Set video width
      webcamRef.current.video.width = videoWidth
      webcamRef.current.video.height = videoHeight

      // Set canvas height and width
      canvasRef.current.width = videoWidth
      canvasRef.current.height = videoHeight

      // Make Detections
      const obj = await net.detect(video)

      // Draw mesh

      const ctx = canvasRef.current.getContext("2d")

      console.log(obj)
      const findHuman = obj
        ? obj.filter((box) => {
            return box.class === "person" && box.score >= 0.8
          })
        : null

      let humanDetact = findHuman
        ? findHuman.filter((box) => {
            return (
              15 <= box.bbox[0] <= 160 &&
              15 <= box.bbox[1] <= 160 &&
              box.bbox[2] >= 350 &&
              box.bbox[3] >= 300
            )
          })
        : []
      console.log(humanDetact)
      if (humanDetact.length > 0) {
        setHumanDetacting(true)
        setSentence("탐지 되었습니다. 마스크를 벗어주세요.")
        const imageUrl = webcamRef.current.getScreenshot()
        let imageFile = dataURLtoFile(imageUrl, "test.jpeg")
        setTimeout(() => {
          drawRect(humanDetact, ctx)
          // console.log(imageUrl)

          let formData = new FormData()
          formData.set("file", imageFile)
          // for (let key of formData.keys()) {
          //   console.log(key);
          // }
          // for (let value of formData.values()) {
          //   console.log(value);
          // }
          axios
            .post(API_URL + "img/face/", formData, {
              headers: {
                "Content-Type": "multipart/form-data",
                // "Access-Control-Allow-Origin": "*",
              },
            })
            .then((res) => {
              if (res.data.status === "0") {
                console.log("성공")
                setFaceDetacting(true)
                setSentence(
                  `인식되었습니다. 마스크를 착용해주세요. ${res.data.userCode}`
                )
                setUserCode(res.data.userCode)
                detectMask(userCode)
              } else if (res.data.status === "1") {
                setHumanDetacting(false)
                // 모달 창
                Swal.fire({
                  icon: "error",
                  title: "오류",
                  text: "DB에 등록된 사용자가 아닙니다.",
                  showConfirmButton: false,
                  timer: 1500,
                })
                setSentence("DB에 등록된 사용자가 아닙니다.")
                setTimeout(() => {
                  setSentence("")
                }, 2000)
              } else if (res.data.status === "2") {
                setHumanDetacting(false)
                setSentence("조금 더 중앙에 있어주세요")
                setTimeout(() => {
                  setSentence("")
                }, 2000)
              }
            })
            .catch((err) => {
              console.log(err.message)
              setHumanDetacting(false)
              setSentence("")
            })
        }, 2000)
      }

      // console.log(findHuman)
      // obj.forEach((box) => {
      //   if(box.bbox[2] >= 520 && box.bbox[3] >= 320 && box.score >= 0.85 && box.class==="person"){
      //     drawRect(obj, ctx);
      //     // console.log(ctx.canvas.toDataURL());
      //     const imageFile = webcamRef.current.getScreenshot();
      //     let formData = new FormData();
      //     formData.set("file", imageFile);
      //     for (let key of formData.keys()) {
      //       console.log(key);
      //     }
      //     for (let value of formData.values()) {
      //       console.log(value);
      //     }
      //     axios
      //       .post("http://localhost:8080/", formData, {
      //         headers: {
      //           "Content-Type": "multipart/form-data",
      //           // "Access-Control-Allow-Origin": "*",
      //         },
      //       })
      //       .then((res) => {
      //       console.log("성공")
      //       setMaskLoading(true)
      //     })
      //       .catch((err) => {
      //         console.log(err.message);
      //       });
      //   }
      // });

      // if(obj.length == 1 ){
      //   if(obj[0].score >= 0.75 && obj[0].class==='person'){
      //     drawRect(obj, ctx);
      //     // console.log(ctx.canvas.toDataURL());
      //     console.log(webcamRef.current.getScreenshot())
      //     const imageFile = webcamRef.current.getScreenshot();
      //     let formData = new FormData();
      //     formData.set("file", imageFile);
      //     axios
      //       .post("http://localhost:8080/", formData, {
      //         headers: {
      //           "Content-Type": "multipart/form-data",
      //           // "Access-Control-Allow-Origin": "*",
      //         },
      //       })
      //       .then((res) => console.log("성공"))
      //       .catch((err) => {
      //         console.log(err.message);
      //       });

      //   }
      // }
    }
  }

  const defineInterval = (net) => {
    if (net) {
      setUserCode("")

      const timeId = setInterval(() => {
        detect(net)
      }, 3000)
      setTimerId(timeId)
    }
    if (humanDetacting) {
      console.log("인간 인식", net)
      clearInterval(timerId)
      setTimerId(null)
    }
  }
  useEffect(() => {
    async function runModel() {
      const net = await cocossd.load()
      console.log("모델 업로드 끝")
      setModel(net)
      // defineInterval(net)
    }
    runModel()
    // runCocoSsd();
  }, [])

  useEffect(() => {
    console.log(model)
    if (model && !humanDetacting) {
      console.log("확인")

      defineInterval(model)
    } else if (model && humanDetacting) {
      defineInterval(null)
    }
  }, [humanDetacting, model])

  return (
    <div className={styles.mainPageContainer}>
      {/* <span>{sentence}</span> */}
      <div className={faceDetacting ? styles.humanNow : styles.noHumanNow}>
        <div
          className={
            humanDetacting
              ? faceDetacting
                ? styles.tmp
                : styles.detectNow
              : styles.nodetectNow
          }
        >
          <Webcam
            ref={webcamRef}
            muted={true}
            mirrored={true}
            screenshotFormat="image/jpeg"
            className={styles.webCamArea}
          />
          <canvas ref={canvasRef} className={styles.webCanvas} />
        </div>
      </div>
    </div>
  )
}

export default Camera
