package com.vodqa.messaging;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class CDPClient {

    private String wsURL;
    private WebSocket webSocket;
    private WebSocketFactory webSocketFactory;
    private BlockingQueue<String> blockingDeque = new LinkedBlockingDeque<>(10000);

    public CDPClient(String wsURL){
        webSocketFactory = new WebSocketFactory();
        this.wsURL = wsURL;
    }

    private void connect() throws IOException, WebSocketException {
        if(Objects.isNull(webSocket)){
            System.out.println("Initiating new websocket connection :" + wsURL);
            webSocket = webSocketFactory.createSocket(wsURL).addListener(new WebSocketAdapter() {
                @Override
                public void onTextMessage(WebSocket webSocket, String message){
                    System.out.println("Received websocket message :" + message);
                    blockingDeque.add(message);
                }
            }).connect();
        }
    }

    public void sendMessage(String message) throws  IOException, WebSocketException{
        if(Objects.isNull(webSocket))
            this.connect();
        System.out.println("Sending message : " + message);
        webSocket.sendText(message);
    }


}
