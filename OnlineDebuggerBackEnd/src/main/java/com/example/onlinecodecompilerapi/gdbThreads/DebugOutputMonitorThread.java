package com.example.onlinecodecompilerapi.gdbThreads;

import com.example.onlinecodecompilerapi.helpers.Parser;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

public class DebugOutputMonitorThread extends Thread{

	private final DebugInputSenderThread inputThread;
    private final WebSocketSession session;
    private final Process process;
    private final BufferedReader reader;
    private final BufferedWriter writer;
    private final int lastLine;

    public DebugOutputMonitorThread(DebugInputSenderThread inputThread, WebSocketSession session, Process process, BufferedReader gdbReader, BufferedWriter gdbInput, int lastLine) {

    	this.inputThread = inputThread;
        this.session = session;
        this.process = process;
        this.reader = gdbReader;
        this.writer = gdbInput;
        this.lastLine = lastLine;
    }

	
	@Override
    public void run() {
        
        try {
        	  while (process.isAlive()) {   
        		  
        		 Map<String, String> rawValues = new HashMap<>();
        	     Map<String[], String> typedValues = new HashMap<>();
        		  
                boolean infoLocalsExist = true;

                StringBuilder builder = new StringBuilder();
                
                boolean isLastLine = false;
                
                int ch;
                
                while ((ch = reader.read()) != -1) {
                	
                	if (ch == '\n' || ch == '\r') break;
                	
                	builder.append((char) ch);

                	if (builder.toString().startsWith("(gdb) @(gdb)")) {

                		String newBuilder = builder.toString().substring(12, builder.length()).trim();
                		
                		builder.setLength(0);
                		
                		builder.append(newBuilder);
                	}
                                	
                	if (ch == '}') {

                		String temp = builder.toString().trim();
                		
                		builder.setLength(0);
                		
                		builder.append(temp);
                	
                		StringBuilder lineNumber = new StringBuilder();
                	
                		int index = 6;
                		
                		while (Character.isDigit(builder.charAt(index))) {
                			lineNumber.append(builder.charAt(index));
                			index++;
                		}

                		if (lineNumber.toString().equals(Integer.toString(lastLine))) {
                			isLastLine = true;
                			break;
                		}
                		
                	}
                } //reads outputs from GDB
                                
                String line = builder.toString();
                
                if (line.contains("in _Jv_RegisterClasses ()")) {
                	 session.sendMessage(new TextMessage("Debugging is finished!"));
                	 break;
                }
                
                System.out.println(line);
                
                if (line.startsWith("(gdb)")) {
                	line = line.substring(5, line.length()).trim();
                }  
                
                if (line.startsWith("@(gdb)")) {
                	line = line.substring(6, line.length()).trim();
                }

                if (line.length() > 0) {
                  
                    	
                	if (line.contains("cin >> ") || line.contains("cin>>")) {
                		this.inputThread.cinTriggered();
                	}

                    session.sendMessage(new TextMessage(line));
                }
                
                
                if (line.length() > 0 && !isLastLine) {

                	writer.write("info locals\n");
                    
                    writer.flush();
                    
                    writer.write("printf \"@\"\n");
                    
                    writer.flush();
                    
                    reader.read();
                    
                   // int iteration = 0;
                                                                                     
                    while (true) {
                    	
                    	 StringBuilder lineLocalVariable = new StringBuilder();
                    	
	                	 int character;
	                	 
	                	 while ((character = reader.read()) != -1) {
	                		 
	                		 if (character == '\r') continue;
	                		 
	                		 if (character == '@'|| character == '\n' || character == '\t') break;
	                		 
	                		 lineLocalVariable.append((char) character);
	                		 
	                	 }
	                	 
	                	 String infoLocal = lineLocalVariable.toString();

	                	 if (infoLocal.startsWith("(gdb)")) {
	                		 infoLocal = infoLocal.substring(5, infoLocal.length()).trim();
	                     } 
	                	 
	                	 if (infoLocal.length() == 0) break;
	                	 
	                	 if (infoLocal.equals("No locals.")) {
	                		 	
	                		 	session.sendMessage(new TextMessage(infoLocal));
	                    		infoLocalsExist = false;
	                    		break;
	                    		
	                     } else {
	                    	 
	                    	 String[] infoLocalArray = infoLocal.split("=");
	                     	
	                         rawValues.put(infoLocalArray[0].trim(), infoLocalArray[1].trim());	                     	
	                    	 
	                     }

                    }
                    
                	
                }

                if (line.length() > 0 && infoLocalsExist && !isLastLine) {

                    for (Map.Entry<String, String> rawValue : rawValues.entrySet()) {

                        writer.write("ptype " + rawValue.getKey() + "\n");
                        
                        writer.flush();
                        
                        String typedValue = reader.readLine();
                        
                        if (typedValue.startsWith("(gdb)")) {
                        	typedValue = typedValue.substring(5, typedValue.length()).trim();
                        }

                        String[] typedValueArray = typedValue.split("=");

                        typedValues.put(new String[]{rawValue.getKey(), typedValueArray[1].trim()}, rawValue.getValue());

                    }

                    Parser parser = new Parser(typedValues);

                    session.sendMessage(new TextMessage(parser.getParseValues()));
                    
                }
                
        	  }

        } catch (Exception e) {

            System.out.println("At line: 92 " + e );

        }
        
    }


}

