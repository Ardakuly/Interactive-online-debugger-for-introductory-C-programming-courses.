package com.example.onlinecodecompilerapi.service;

import com.example.onlinecodecompilerapi.gccThreads.ErrorStreamMonitorThread;
import com.example.onlinecodecompilerapi.gccThreads.OutputStreamMonitorThread;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.io.*;
import java.util.concurrent.*;

@Service
public class GCCCompilerService {

    public void run(WebSocketSession session, String code, BlockingQueue<String> incomingMessageQueue) {


        String result = "";

        try {

            File main = new File("C:\\Users\\Professional\\Desktop\\main.cpp");

            BufferedWriter writeCodeToFile = new BufferedWriter(new FileWriter(main));

            writeCodeToFile.write(code);

            writeCodeToFile.close();

            //------------------- Compile Code -------------------//

            Process process = Runtime.getRuntime().exec("g++ -o C:\\Users\\Professional\\Desktop\\compiled C:\\Users\\Professional\\Desktop\\main.cpp");

            int execCode = process.waitFor();


            //------------------- Run Code -------------------//

            if (execCode == 0) {

                process = Runtime.getRuntime().exec("C:\\Users\\Professional\\Desktop\\compiled");

                ExecutorService executorService = Executors.newFixedThreadPool(2);
                OutputStreamMonitorThread userInputAndResult = new OutputStreamMonitorThread(session, process, incomingMessageQueue);
                ErrorStreamMonitorThread errors = new ErrorStreamMonitorThread(session, process, incomingMessageQueue);

                executorService.execute(userInputAndResult);
                executorService.execute(errors);

                executorService.shutdown();

            }

        } catch (Exception error) {

            error.printStackTrace();

        }
    }

}


//process = Runtime.getRuntime().exec("./compiled");
// System.out.println("Here we are ----->");
