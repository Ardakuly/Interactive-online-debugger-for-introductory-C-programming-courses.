package com.example.onlinecodecompilerapi.gccThreads;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.*;
import java.util.concurrent.BlockingQueue;


public class OutputStreamMonitorThread extends Thread {

    private final WebSocketSession session;
    private final Process process;
    private final BlockingQueue<String> incomingMessageQueue;
    private final BufferedReader reader;
    private final BufferedWriter writer;
    private String detected;

    public OutputStreamMonitorThread(WebSocketSession session,
                                     Process process,
                                     BlockingQueue<String> incomingMessageQueue) {

        this.session = session;
        this.process = process;
        this.reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        this.writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        this.incomingMessageQueue = incomingMessageQueue;
        this.detected = "";

    }

    @Override
    public void run() {

        try {

            String line = "";

            System.out.println("Start working");

            while ((line = reader.readLine()) != null) {

                System.out.println(line);

                session.sendMessage(new TextMessage(line));

                if (line.startsWith("(User Input):")) {

                    String response = incomingMessageQueue.take();

                    writer.write(response);

                    writer.newLine();

                    writer.flush();

                }

                System.out.println(line);

            }





        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }



    @Override
    protected void finalize() throws Throwable {

        this.reader.close();

    }
}



//            // Send message asynchronously
//            CompletableFuture.supplyAsync(() -> {
//                try {
//                    session.sendMessage(new TextMessage(detected));
//                    String response = messageFutures.get(session.getId()).get();
//                    return response;
//                } catch (ExecutionException e) {
//                    throw new RuntimeException(e);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }).thenAccept(response -> {
//                // Handle response from message sending operation
//                messageFuture.complete(response);
//                System.out.println(response);
//            });

//            if (detected.length() > 0) {
//
//                System.out.println("Detected message: --> " +  detected);
//
//                session.sendMessage(new TextMessage(detected));
//
//                String response = messageFutures.get(session.getId()).get();
//
//                System.out.println(response);
//
//            }