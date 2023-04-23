import React, {useState, useEffect, useRef} from 'react';
import AceEditor from 'react-ace';

import 'ace-builds/src-noconflict/mode-c_cpp';
import 'ace-builds/src-noconflict/theme-monokai';

import "ace-builds/src-noconflict/ext-language_tools";

import "./index.css";

export function Ace() {

    const [socket, setSocket] = useState(null);

    const [code, setCode] = useState("");

    const [userInput, setUserInput] = useState("");

    const [output, setOutput] = useState("");

    const [isDisabled, setIsDisabled] = useState(true);


    useEffect(() => {

      const initialCode = `#include <iostream>

int main() {
  std::cout << "Hello, world!" << std::endl;
  return 0;
}`;

     setCode(initialCode);

      const connection = new WebSocket("ws://127.0.0.1:8080/nu/editor/compile");

      connection.onopen = () => {

        console.log("Web Socket Connection is established");

      }

      connection.onmessage = (event) => {

        const data = event.data;

        if (data.startsWith("(User Input): ")) {
          //make button active
          setIsDisabled(false);
        } 

        setOutput(prevOutput => prevOutput.length > 0 ? `${prevOutput}\n${data}` : data);

        console.log(output);

      }

      connection.onerror = (event) => {

        console.log(event.target);

      }

      setSocket(connection);

      return () => {

        connection.close();
        setSocket(null);

      }

    }, []);

    const handleSendCode = () => {

      if (socket && socket.readyState === WebSocket.OPEN) {
        socket.send(code);
        setOutput("");
      }

    }

    const handleSendUserInput = () => {

      if (socket && socket.readyState === WebSocket.OPEN) {

        console.log("HERE WE ARE SENDING DATA");
        socket.send(userInput);
        setIsDisabled(true);
      }

    }

    const hanldeChangeUserInput = ({target}) => {

      setUserInput(target.value);

    }

    



    return (
        <div className="main">
          <div className="editor"> 
              <AceEditor
                  mode="c_cpp"
                  theme="monokai"
                  enableSnippets={true}
                  editorProps={{ $blockScrolling: Infinity, enableDebug: true }}
                  value={code}
                  onChange={setCode}
                  name="code-editor"
                  width="70%"
                  height="60%"
              />
              <div className="buttons">
                <button className="run" onClick={handleSendCode}>Run</button>
              </div>
              <AceEditor
                  mode="c_cpp"
                  theme="monokai"
                  value={output}
                  name="output-editor"
                  width="70%"
                  height="25%"
              />
              <div className="userInput">
                  <input type="text" name="input" placeholder="Enter User Input, If code required to do so:" onChange={hanldeChangeUserInput}/>
                  <button id="submit-button" type="submit" onClick={handleSendUserInput} disabled={isDisabled}>Enter</button>
              </div>
          </div>
        </div>
    );
}


// const handleCompile = async () => {

//   console.log(code);

//   try {
//     const response = await fetch("http://localhost:8080/nu/v1/editor/compile", {
//       method: 'POST',
//       headers: {
//         'Content-Type': 'text/plain'
//       },
//       body: code
//     });

//     const resultText = await response.text();
//     setOutput(resultText);
//   //   setOutput(await response.json());
//   } catch (error) {
//     setOutput(error.message);
//   }
// };


