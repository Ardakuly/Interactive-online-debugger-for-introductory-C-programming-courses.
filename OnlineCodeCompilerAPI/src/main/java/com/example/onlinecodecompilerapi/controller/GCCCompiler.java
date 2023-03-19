package com.example.onlinecodecompilerapi.controller;

import com.example.onlinecodecompilerapi.service.GCCCompilerService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class GCCCompiler extends TextWebSocketHandler {

    private final GCCCompilerService gccCompilerService;
    private BlockingQueue<String> incomingMessageQueue;


    public GCCCompiler(GCCCompilerService gccCompilerService) {
        this.gccCompilerService = gccCompilerService;
        this.incomingMessageQueue = new LinkedBlockingQueue<>();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        System.out.println("Web Socket is established .......");

    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        String[] lines = message.getPayload().split("\r\n|\r|\n");

        if (lines.length > 1) {

            gccCompilerService.run(session, message.getPayload(), incomingMessageQueue);

        } else {

            incomingMessageQueue.add(message.getPayload());

        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    }
}

