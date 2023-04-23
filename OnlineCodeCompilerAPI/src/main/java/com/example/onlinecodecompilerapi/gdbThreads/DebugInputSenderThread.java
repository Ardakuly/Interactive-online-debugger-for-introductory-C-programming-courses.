package com.example.onlinecodecompilerapi.gdbThreads;

import org.springframework.web.socket.WebSocketSession;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;

public class DebugInputSenderThread extends Thread {

    private final WebSocketSession session;
    private final Process process;
    private final BufferedWriter reader;

    public DebugInputSenderThread(WebSocketSession session, Process process) {

        this.session = session;
        this.process = process;
        this.reader = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));

    }


    @Override
    public void run() {



    }
}
