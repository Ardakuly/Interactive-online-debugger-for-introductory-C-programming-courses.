package com.example.onlinecodecompilerapi.gdbThreads;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class DebugOutputMonitorThread extends Thread{

    private final WebSocketSession webSocketSession;
    private final Process process;
    private final BufferedReader reader;

    public DebugOutputMonitorThread(WebSocketSession webSocketSession, Process process) {

        this.webSocketSession = webSocketSession;
        this.process = process;
        this.reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

    }

    @Override
    public void run() {

        try {

            while (process.isAlive()) {

                // check


            }





        }

    }


}

