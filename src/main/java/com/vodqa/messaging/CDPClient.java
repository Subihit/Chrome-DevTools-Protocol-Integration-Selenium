package com.vodqa.messaging;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public class CDPClient {

    private String wsURL;
    private WebSocket webSocket;
    private WebSocketFactory webSocketFactory;
    private BlockingQueue<String> blockingQueue = new LinkedBlockingDeque<>(10000);

    public CDPClient(String wsURL) {
        webSocketFactory = new WebSocketFactory();
        this.wsURL = wsURL;
    }

    private void connect() throws IOException, WebSocketException {
        if (Objects.isNull(webSocket)) {
            System.out.println("Initiating new websocket connection :" + wsURL);
            webSocket = webSocketFactory.createSocket(wsURL).addListener(new WebSocketAdapter() {
                @Override
                public void onTextMessage(WebSocket webSocket, String message) {
                    System.out.println("Received websocket message :" + message);
                    blockingQueue.add(message);
                }
            }).connect();
        }
    }

    public void sendMessage(String message) throws IOException, WebSocketException {
        if (Objects.isNull(webSocket))
            this.connect();
        System.out.println("Sending message : " + message);
        webSocket.sendText(message);
    }

    public String getResponseMessage(String methodName, int timeoutInSecs) throws InterruptedException {
        try {
            while (true) {
                String message = blockingQueue.poll(timeoutInSecs, TimeUnit.SECONDS);
                if (Objects.isNull(message))
                    throw new RuntimeException(String.format("No message received with this method name : '%s'", methodName));
                JSONObject jsonObject = new JSONObject(message);
                try {
                    String method = jsonObject.getString("method");
                    if (method.equalsIgnoreCase(methodName)) {
                        return message;
                    }
                } catch (JSONException e) {
                    //do nothing
                }
            }
        } catch (Exception e1) {
            throw e1;
        }
    }

    public String getResponseBodyMessage(int id) throws InterruptedException {
        try {
            while (true) {
                String message = blockingQueue.poll(5, TimeUnit.SECONDS);
                if (Objects.isNull(message))
                    throw new RuntimeException(String.format("No message received with this id : '%s'", id));
                JSONObject jsonObject = new JSONObject(message);
                try {
                    int methodId = jsonObject.getInt("id");
                    if (id == methodId) {
                        return jsonObject.getJSONObject("result").getString("body");
                    }
                } catch (JSONException e) {
                    //do nothing
                }
            }
        } catch (Exception e1) {
            throw e1;
        }
    }

    public void mockResponse(String mockMessage) {
        new Thread(() -> {
            try {
                String message = this.getResponseMessage("Network.requestIntercepted", 5);
                JSONObject jsonObject = new JSONObject(message);
                String interceptionId = jsonObject.getJSONObject("params").getString("interceptionId");
                this.sendMessage(MessageBuilder.buildGetContinueInterceptedRequestMessage(2000, interceptionId, mockMessage));
                return;
            } catch (Exception e) {
                //do nothing
            }
        }).start();
    }


}
