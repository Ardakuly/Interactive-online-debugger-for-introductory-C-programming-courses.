package com.example.onlinecodecompilerapi.gccThreads;

import org.springframework.web.socket.WebSocketSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;

public class ErrorStreamMonitorThread extends Thread{

    private final WebSocketSession session;
    private final Process process;
    private final BufferedReader reader;
    private String detected;

    private BlockingQueue<String> incomingMessageQueue;
    public ErrorStreamMonitorThread(WebSocketSession session, Process process,
                                    BlockingQueue<String> incomingMessageQueue) {

        this.session = session;
        this.process = process;
        this.reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        this.detected = "";
        this.incomingMessageQueue = incomingMessageQueue;

    }

    @Override
    public void run() {

        try {

            String line;

            while ((line = reader.readLine()) != null) {

                detected = detected + line + "\n";

            }




        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    protected void finalize() throws Throwable {

        this.reader.close();

    }
}
