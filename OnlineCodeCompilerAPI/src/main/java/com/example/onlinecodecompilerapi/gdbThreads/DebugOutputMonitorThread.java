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

    private final WebSocketSession session;
    private final Process process;
    private final BufferedReader reader;

    private final BufferedWriter writer;

    public DebugOutputMonitorThread(WebSocketSession session, Process process) {

        this.session = session;
        this.process = process;
        this.reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        this.writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
    }

    @Override
    public void run() {

        Map<String, String> rawValues = new HashMap<>();
        Map<String[], String> typedValues = new HashMap<>();

        try {

            while (process.isAlive()) {

                boolean isLineOutput = false;

                String line = "";

                System.out.println("Start working");

                while ((line = reader.readLine()) != null) {

                    System.out.println(line);

                    if (Character.isDigit(line.charAt(0))) {

                        session.sendMessage(new TextMessage(line));
                        isLineOutput = true;
                        break;

                    }

                    String[] lineArray = line.split("=");

                    rawValues.put(lineArray[0], lineArray[1]);

                }

                if (!isLineOutput) {

                    for (Map.Entry<String, String> rawValue : rawValues.entrySet()) {

                        writer.write("ptype " + rawValue.getKey());

                        String[] typedValue = reader.readLine().split("=");

                        typedValues.put(new String[]{rawValue.getKey(),typedValue[1]}, rawValue.getValue());

                    }

                    Parser parser = new Parser(typedValues);

                    session.sendMessage(new TextMessage(parser.getParseValues()));

                }

            }

        } catch (Exception e) {

            System.out.println(e);

        }

    }


}

