package com.example.onlinecodecompilerapi.gdbThreads;

import org.springframework.web.socket.WebSocketSession;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.concurrent.BlockingQueue;

public class DebugInputSenderThread extends Thread {

    private final WebSocketSession session;

    private BlockingQueue<String> incomingMessageQueue;
    private final Process process;
    private final BufferedWriter writer;

    public DebugInputSenderThread(WebSocketSession session, BlockingQueue<String> incomingMessageQueue, Process process) {

        this.session = session;
        this.process = process;
        this.writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));

    }


    @Override
    public void run() {

        if (!incomingMessageQueue.isEmpty()) {

            try {

                String response = incomingMessageQueue.take();

                writer.write(response);

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        }

    }
}
