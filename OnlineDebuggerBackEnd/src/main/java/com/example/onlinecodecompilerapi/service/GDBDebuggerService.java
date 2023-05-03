package com.example.onlinecodecompilerapi.service;

import com.example.onlinecodecompilerapi.gdbThreads.DebugInputSenderThread;
import com.example.onlinecodecompilerapi.gdbThreads.DebugOutputMonitorThread;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.io.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class GDBDebuggerService {


    public void debug (WebSocketSession session, BlockingQueue<String> userInputMessage, BlockingQueue<String> stepForwardMessage, String code, int lastLine) {

        try {

            File main = new File("main.cpp");

            BufferedWriter writeCodeToFile = new BufferedWriter(new FileWriter(main));

            writeCodeToFile.write(code);

            writeCodeToFile.close();

            //------------------- Compile Code -------------------//

            Process process = Runtime.getRuntime().exec("g++ -g C:\\Users\\Professional\\Desktop\\main.cpp -o  C:\\Users\\Professional\\Desktop\\compiledDebug");

            int execCode = process.waitFor();

            //------------------- Run Code -------------------//

            if (execCode == 0) {

                process = Runtime.getRuntime().exec("gdb -q C:\\Users\\Professional\\Desktop\\compiledDebug");
                
                System.out.println("Process started Debugging!!!");

                BufferedWriter gdbInput = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
                BufferedReader gdbReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                //how to execute "start" command on GDB
                executeAdditionalCommands(process, gdbInput, gdbReader);

                ExecutorService executorService = Executors.newFixedThreadPool(2);

                DebugInputSenderThread inputThread = new DebugInputSenderThread(session, userInputMessage, stepForwardMessage, process);

                DebugOutputMonitorThread outputThread = new DebugOutputMonitorThread(inputThread, session, process, gdbReader, gdbInput, lastLine);
                
                executorService.execute(outputThread);

                executorService.execute(inputThread);

                executorService.shutdown();

            }

        } catch (Exception error) {

            error.printStackTrace();

        }

    }

    private static void executeAdditionalCommands(Process process,
                                                  BufferedWriter gdbInput,
                                                  BufferedReader gdbReader) throws IOException {
    	
    	
        gdbReader.readLine(); // skip "Reading symbols from compiled...done." output from GDB

        gdbInput.write("set print thread-events off\n");
        
        gdbInput.flush();

        gdbInput.write("start\n");

        gdbInput.flush(); 
        
        
                
        for (int i = 1; i <= 4; i++)  {
        	System.out.println("At line: 113 " + gdbReader.readLine());
        }
        

    }

}

