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


    public void debug (WebSocketSession session, BlockingQueue<String> incomingMessageQueue, String code) {

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

                process = Runtime.getRuntime().exec("gdb -q compiled");

                BufferedWriter gdbInput = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
                BufferedReader gdbReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                //how to execute "start" command on GDB
                executeAdditionalCommands(process, gdbInput, gdbReader);

                ExecutorService executorService = Executors.newFixedThreadPool(2);

                DebugOutputMonitorThread outputThread = new DebugOutputMonitorThread(session, process);

                DebugInputSenderThread inputThread = new DebugInputSenderThread(session, incomingMessageQueue, process);

                executorService.execute(outputThread);

                executorService.execute(inputThread);

                executorService.shutdown();

                gdbInput.write("q\n");

                gdbInput.write("y\n");

            }

        } catch (Exception error) {

            error.printStackTrace();

        }

    }

    private static void executeAdditionalCommands(Process process,
                                                  BufferedWriter gdbInput,
                                                  BufferedReader gdbReader) throws IOException {

        gdbReader.readLine(); // skip "Reading symbols from compiled...done." output from GDB

        gdbInput.write("define n\n" +
                "    set logging file /dev/null\n" +
                "    set logging redirect on\n" +
                "    set logging on\n" +
                "    next\n" +
                "    set logging off\n" +
                "    display\n" +
                "end\n");

        gdbInput.flush();

        for (int i = 1; i <= 2; i++) gdbReader.readLine(); // skip 2 lines of output from GDB

        gdbInput.write("start\n");

        gdbInput.flush();

        for (int i = 1; i <= 4; i++) gdbReader.readLine(); // to skip 4 lines of output

    }

}

