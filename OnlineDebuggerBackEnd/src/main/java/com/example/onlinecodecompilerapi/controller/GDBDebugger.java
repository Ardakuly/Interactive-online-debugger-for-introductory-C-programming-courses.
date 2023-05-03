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
    private BlockingQueue<String> userInputMessage;
    private BlockingQueue<String> stepForwardMessage;

    public GDBDebugger(GDBDebuggerService gdbDebuggerService) {
        this.gdbDebuggerService = gdbDebuggerService;
        this.userInputMessage = new LinkedBlockingQueue<>();
        this.stepForwardMessage = new LinkedBlockingQueue<>();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        System.out.println("Web Socket for Debug is established .......");

    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        String[] lines = message.getPayload().split("\r\n|\r|\n");
        
        System.out.println("Line: " + lines[0]);
        
        if (lines.length > 1) {

        	System.out.println("Code for debugging has come");
        	
        	int lastLine = lines.length;
        	
        	System.out.println("Last Line: " + lastLine);
        	
            this.gdbDebuggerService.debug(session, userInputMessage, stepForwardMessage, message.toString(),  lastLine);

        } else {
            
            if (lines[0].equals("[*]")) {
            	
            	stepForwardMessage.add(message.getPayload());
            	
            	System.out.println("Size: " + stepForwardMessage.size());
            	
            } else {
            	
            	 userInputMessage.add(message.getPayload());
            	
            }

        }



    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    	
    	this.userInputMessage = null;
    	this.stepForwardMessage = null;
    }

}
