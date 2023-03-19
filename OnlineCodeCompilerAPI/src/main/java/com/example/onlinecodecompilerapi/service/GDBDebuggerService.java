package com.example.onlinecodecompilerapi.service;

import com.example.onlinecodecompilerapi.gdbThreads.DebugOutputMonitorThread;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class GDBDebuggerService {


    public void debug (WebSocketSession session, String code) {

        try {

            File main = new File("main.cpp");

            BufferedWriter writeCodeToFile = new BufferedWriter(new FileWriter(main));

            writeCodeToFile.write(code);

            writeCodeToFile.close();

            //------------------- Compile Code -------------------//

            Process process = Runtime.getRuntime().exec("g++ -g main.cpp -o compiled ");

            int execCode = process.waitFor();


            //------------------- Run Code -------------------//

            if (execCode == 0) {

                process = Runtime.getRuntime().exec("gdb compiled");

                // put breakpoints from users

                ExecutorService executorService = Executors.newFixedThreadPool(2);

                executorService.execute(new DebugOutputMonitorThread(session, process));




            }

        } catch (Exception error) {

            error.printStackTrace();

        }

    }

}
