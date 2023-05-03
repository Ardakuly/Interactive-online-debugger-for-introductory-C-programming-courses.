package com.example.onlinecodecompilerapi.gdbThreads;

import org.springframework.web.socket.WebSocketSession;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.concurrent.BlockingQueue;

public class DebugInputSenderThread extends Thread {

    private final WebSocketSession session;
    private final BlockingQueue<String> userInputMessage;
    private final BlockingQueue<String> stepForwardMessage;
    private final Process process;
    private final BufferedWriter writer;


	public DebugInputSenderThread(WebSocketSession session, BlockingQueue<String> userInputMessage, BlockingQueue<String> stepForwardMessage, Process process) {

        this.session = session;
        this.userInputMessage = userInputMessage;
        this.stepForwardMessage = stepForwardMessage;
        this.process = process;
        this.writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        
    }


    @Override
    public void run() {
    		
            try {

            	while(process.isAlive()) {
            		
            		String temp = stepForwardMessage.take();

            		writer.write("step\n");
                    
                    writer.flush();
            		
            	}
                

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }




    }
    
    public void cinTriggered() {
    	
    	System.out.println("Entering value to GDB ------>");
    	
    	if (!userInputMessage.isEmpty()) {
        	
       	 	
			try {
	                        
	            String response = userInputMessage.take();
	            
	            System.out.println("Value is ----->" + response);
	            
	            writer.write("step\n");
	            
	            writer.flush();

	            writer.write(response);
	            
	            writer.flush();
	            
	            System.out.println("Entered value to GDB ------>");
				
			} catch (InterruptedException | IOException e) {
				// TODO Auto-generated catch block
				System.out.println("At line 83:" + e.getMessage());
			}     	 	
       }
	}

}
