// Import dependencies
import React, { useRef, useState, useEffect } from "react";
import * as tf from "@tensorflow/tfjs";
import * as cocossd from "@tensorflow-models/coco-ssd";
import Webcam from "react-webcam";

import { drawRect } from "./utilities";
import axios from "axios";

function App() {
  const webcamRef = useRef(null);
  const canvasRef = useRef(null);
 
  const [maskLoading, setMaskLoading] = useState(false)

  // Main function
  const runCocoSsd = async () => {
    const net = await cocossd.load();
    console.log("모델 업로드 끝");
    //  Loop and detect hands
    if (!maskLoading){
      setInterval(() => {
        detect(net);
      }, 500);

    }
  };
  const dataURLtoFile = (dataurl, fileName) => {
 
    var arr = dataurl.split(','),
        mime = arr[0].match(/:(.*?);/)[1],
        bstr = atob(arr[1]), 
        n = bstr.length, 
        u8arr = new Uint8Array(n);
        
    while(n--){
        u8arr[n] = bstr.charCodeAt(n);
    }
    
    return new File([u8arr], fileName, {type:mime});
}

  const detect = async (net) => {
    // Check data is available
    if (
      typeof webcamRef.current !== "undefined" &&
      webcamRef.current !== null &&
      webcamRef.current.video.readyState === 4
    ) {
      // Get Video Properties
      const video = webcamRef.current.video;
      const videoWidth = webcamRef.current.video.videoWidth;
      const videoHeight = webcamRef.current.video.videoHeight;

      // Set video width
      webcamRef.current.video.width = videoWidth;
      webcamRef.current.video.height = videoHeight;

      // Set canvas height and width
      canvasRef.current.width = videoWidth;
      canvasRef.current.height = videoHeight;

      
      // Make Detections
      const obj = await net.detect(video);

      // Draw mesh

      const ctx = canvasRef.current.getContext("2d");

    
      
    
      const findHuman = obj ? obj.filter((box)=>{
        return box.class === "person" && box.score >= 0.8  
      }) : null
     
      let humanDetact = findHuman ?  findHuman.filter((box)=>{
        return 15<=box.bbox[0]<=160 && 15<=box.bbox[1]<=160 && box.bbox[2] >= 400 && box.bbox[3] >= 400
      }) : []
      console.log(humanDetact)
      if (humanDetact.length>0){
        drawRect(humanDetact, ctx)
        const imageUrl = webcamRef.current.getScreenshot();
        console.log(imageUrl)
        let imageFile = dataURLtoFile(imageUrl,'test.jpeg');
        console.log(imageFile)
        let formData = new FormData();
        formData.set("file", imageFile);
        // for (let key of formData.keys()) {
        //   console.log(key);
        // }
        // for (let value of formData.values()) {
        //   console.log(value);
        // }
        axios
          .post("http://localhost:8080/img/", formData, {
            headers: {
              "Content-Type": "multipart/form-data",
              // "Access-Control-Allow-Origin": "*",
            },
          })
          .then((res) => {
          console.log("성공")
          setMaskLoading(true)
        })
          .catch((err) => {
            console.log(err.message);
          });
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
  };

  useEffect(() => {
    runCocoSsd();
  }, []);

  return (
    <div>
      <Webcam
        ref={webcamRef}
        muted={true}
        screenshotFormat="image/jpeg"
        style={{
          position: "absolute",
          marginLeft: "auto",
          marginRight: "auto",
          left: 0,
          right: 0,
          textAlign: "center",
          zindex: 9,
          width: 640,
          height: 480,
        }}
      />

      <canvas
        ref={canvasRef}
        style={{
          position: "absolute",
          marginLeft: "auto",
          marginRight: "auto",
          left: 0,
          right: 0,
          textAlign: "center",
          zindex: 8,
          width: 640,
          height: 480,
        }}
      />
    </div>
  );
}

export default App;
