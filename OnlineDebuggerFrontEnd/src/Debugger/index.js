import React, {useState, useEffect} from 'react';

import './index.css';

export function Debugger(props) {

    const [scope, setScope] = useState("");
    const [line, setLine] = useState("");
    const [variables, setVariables] = useState([]);

    useEffect(() => {

        setScope(props.scope);

    }, [props.scope]) 

    useEffect(() => {

        setLine(props.line);

    }, [props.line]) 

    useEffect(() => {

        setVariables(props.variables);

    }, [props.variables]) 


    return (

        <div className='debugger-area'>
            
            
            <div className='debugger-area-scope'>
                <p><span>Scope: </span>{scope}</p>
            </div>

            <div className='debugger-area-line'>
                <p><span>Current Line: </span>{line}</p>
            </div>

            <div className='debugger-area-variables'>

                {
                    variables.map((variable) => {
                        
                        const type = variable.type.includes("[") ? "arr" : "int";

                        console.log(type);

                        return (

                        <div className={`variable-${type}`}>
                            <div className='variable-name-type'>
                                <div className='name'>
                                    <p>{variable.name}</p>                                    
                                </div>
                                <div className='type'>
                                    <p>{variable.type}</p>                                    
                                </div>
                            </div>
                            <div className='variable-value'>
                                <p>{variable.value}</p>
                            </div>
                        </div>
                        
                        )
                    })
                }
                
            </div>

        </div>

    );


}