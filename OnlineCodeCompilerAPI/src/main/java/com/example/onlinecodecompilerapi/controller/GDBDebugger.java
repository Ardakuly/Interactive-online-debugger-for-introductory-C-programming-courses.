package com.example.onlinecodecompilerapi.controller;

import com.example.onlinecodecompilerapi.service.GDBDebuggerService;
import org.springframework.stereotype.Component;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class GDBDebugger extends TextWebSocketHandler {

    private GDBDebuggerService gdbDebuggerService;
    private BlockingQueue<String> incomingMessageQueue;

    public GDBDebugger(GDBDebuggerService gdbDebuggerService) {
        this.gdbDebuggerService = gdbDebuggerService;
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

            this.gdbDebuggerService.debug(session, incomingMessageQueue, message.toString());

        } else {
            
            incomingMessageQueue.add(message.getPayload());

        }



    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    }

}
