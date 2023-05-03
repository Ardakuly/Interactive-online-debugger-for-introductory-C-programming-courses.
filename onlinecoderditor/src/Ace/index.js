import React, {useState, useEffect, useRef} from 'react';
import AceEditor from 'react-ace';
import {Debugger} from '../Debugger/index'

import 'ace-builds/src-noconflict/mode-c_cpp';
import 'ace-builds/src-noconflict/theme-monokai';

import "ace-builds/src-noconflict/theme-github";
import "ace-builds/src-noconflict/ext-language_tools";

import "./index.css";

export function Ace() {

    const editorRef = useRef(null);

    const [socketCompile, setSocketCompile] = useState(null);

    const [socketDebug, setSocketDebug] = useState(null);

    const [code, setCode] = useState("");

    const [userInput, setUserInput] = useState("");

    const [output, setOutput] = useState("");

    const [isDisabled, setIsDisabled] = useState(true);

    const [activeProcess, setActiveProcess] = useState("");

    const [debugData, setDebugData] = useState({
        
      scope: "int main()",
      line: "",
      variables : []

});


    useEffect(() => {

      const initialCode = `#include <iostream>
using namespace std;

int main() {
  cout << "Hello World";
  return 0;
}`;

    if (editorRef.current) {
      const editor = editorRef.current.editor;
    }

     setCode(initialCode);

     const connectionCompile = new WebSocket("ws://127.0.0.1:8080/nu/editor/compile");

     const connectionDebug = new WebSocket("ws://127.0.0.1:8080/nu/editor/debug");

      connectionCompile.onopen = () => {

        console.log("Web Socket Connection for compile is established");

      }

      connectionCompile.onmessage = (event) => {

        const data = event.data;

        if (data.startsWith("(User Input): ")) {
          //make button active
          setIsDisabled(false);
          setActiveProcess("R")
        } 

        setOutput(prevOutput => prevOutput.length > 0 ? `${prevOutput}\n${data}` : data);

        console.log(output);

      }

      connectionCompile.onerror = (event) => {

        console.log(event.target);

      }

      /* HEEEEREEEE WEEEE HAVEEEE DEBUUUUUUUUGGGG SOCKET */

      connectionDebug.onopen = () => {

        console.log("Web Socket Connection for debug is established");

      }

      connectionDebug.onmessage = (event) => {

        const data = event.data.trim();

        if (data == "Debugging is finished!") {
          alert(data);
        }

        if (data == "No locals.") {
          setDebugData(prevDebugData => ({
            ...prevDebugData,
            variables: []
          }));

          alert(data);
        }
        
        if (data.includes("(User Input): ")) {
          //make button active
          setIsDisabled(false);
          setActiveProcess("D")

          setDebugData(prevDebugData => ({
            ...prevDebugData,
            line: data
          }));

        } 

        console.log("Data came");
        console.log("Starts with: " + data.charAt(0) + " Ends with: " + data.charAt(data.length - 1));

        // setOutput(prevOutput => prevOutput.length > 0 ? `${prevOutput}\n${data}` : data);
        if (/^\d/.test(data)) {

          setDebugData(prevDebugData => ({
            ...prevDebugData,
            line: data
          }));
          
        }

        if (data.startsWith("[") && data.endsWith("]")) {

          console.log("Values came for rendering");
          const perVariable = data.split("\n");

          let variables = [];

          for (let variable of perVariable) {

            console.log("Per Unit Variable: "  + variable);

            variable = variable.substring(1, variable.length - 1);

            console.log(variable);

            const perUnitVariable = variable.split("|");

            console.log(perUnitVariable);

            let variableObject = {
              name: perUnitVariable[0],
              type: perUnitVariable[1],
              value: perUnitVariable[2]
            }

            console.log(variableObject);

            variables.push(variableObject);

          }

          setDebugData(prevDebugData => ({
            ...prevDebugData,
            variables: variables
          }))

        }
        // console.log(output);

      }

      connectionDebug.onerror = (event) => {

        console.log(event.target);

      }

      setSocketCompile(connectionCompile);

      setSocketDebug(connectionDebug);

      return () => {

        connectionCompile.close();
        connectionDebug.close();
        setSocketCompile(null);
        setSocketDebug(null);

      }

    }, []);

    const handleCompileCode = () => {

      if (socketCompile && socketCompile.readyState === WebSocket.OPEN) {
        socketCompile.send(code);
        setOutput("");
      }

    }

    const handleDebugCode = () => {

      console.log("Debug button is triggered!");

      if (socketDebug && socketDebug.readyState === WebSocket.OPEN) {
        socketDebug.send(code);
        setOutput("");
      }

    }

    const handleSendUserInput = () => {

      if (socketCompile && socketCompile.readyState === WebSocket.OPEN && activeProcess == "R") {

        console.log("HERE WE ARE SENDING DATA to Compiler");
        socketCompile.send(userInput);
        setIsDisabled(true);
      } else if (socketDebug && socketDebug.readyState === WebSocket.OPEN && activeProcess == "D") {
        console.log("HERE WE ARE SENDING DATA to Debugger");
        socketDebug.send(userInput);
        setIsDisabled(true);
      }

    }

    const handleDebugStepFoward = () => {

      if (socketDebug && socketDebug.readyState === WebSocket.OPEN) {
        socketDebug.send("[*]");
        setOutput("");
      }

    }

    const hanldeChangeUserInput = ({target}) => {

      setUserInput(target.value);

    }
    


    return (
      <div className="page">
        <Debugger scope= {debugData.scope} line={debugData.line} variables={debugData.variables}/>
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
                width="100%"
                height="65%"
            />
            <div className="buttons">
              <button className="run" onClick={handleCompileCode}>Run</button>
              <button className="run" onClick={handleDebugCode}>Debug</button>
              <button className="run" onClick={handleDebugStepFoward}>Step</button>
            </div>
            <AceEditor
                mode="c_cpp"
                theme="monokai"
                value={output}
                name="output-editor"
                width="100%"
                height="25%"
            />
            <div className="userInput">
                <input type="text" name="input" placeholder="Enter User Input, If code required to do so:" onChange={hanldeChangeUserInput}/>
                <button id="submit-button" type="submit" onClick={handleSendUserInput} disabled={isDisabled}>Enter</button>
            </div>
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


