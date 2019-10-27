package com.vodqa.messaging;

public class MessageBuilder {

    public static String geoLocationMessage(int id, double latitude, double longitude) {
        Message message = new Message(id, "Emulation.setGeolocationOverride");
        message.addParameter("latitude", latitude);
        message.addParameter("longitude", longitude);
        message.addParameter("accuracy", 100);
        return message.toJSON();
    }

    public static String enableNetworkCallMonitoringMessage(int id) {
        String message = String.format("{\"id\":%s,\"method\":\"Network.enable\",\"params\":{\"maxTotalBufferSize\":10000000,\"maxResourceBufferSize\":5000000}}", id);
        return message;
    }

    public static String getResponseBodyMessage(int id, String requestID) {
        String message = String.format("{\"id\":%s,\"method\":\"Network.getResponseBody\",\"params\":{\"requestId\":\"%s\"}}", id, requestID);
        return message;
    }


}
