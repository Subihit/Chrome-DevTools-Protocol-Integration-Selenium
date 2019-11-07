package com.vodqa.messaging;

import org.apache.commons.codec.binary.Base64;

public class MessageBuilder {

    public static String geoLocationMessage(int id, String domain, String methodName, double latitude, double longitude) {
        Message message = new Message(id, domain, methodName);
        message.addParameter("latitude", latitude);
        message.addParameter("longitude", longitude);
        message.addParameter("accuracy", 100);
        return message.toJSON();
    }

    public static String enableNetworkCallMonitoringMessage(int id, String domain, String methodName) {
        String message = String.format("{\"id\":%s,\"method\":\"%s.%s\",\"params\":{\"maxTotalBufferSize\":10000000,\"maxResourceBufferSize\":5000000}}", id, domain, methodName);
        return message;
    }

    public static String getResponseBodyMessage(int id, String requestID) {
        String message = String.format("{\"id\":%s,\"method\":\"Network.getResponseBody\",\"params\":{\"requestId\":\"%s\"}}", id, requestID);
        return message;
    }

    public static String buildRequestInterceptorPatternMessage(int id, String pattern, String resourceType) {
        String message = String.format("{\"id\":%s,\"method\":\"Network.setRequestInterception\",\"params\":{\"patterns\":[{\"urlPattern\":\"%s\",\"resourceType\":\"%s\",\"interceptionStage\":\"HeadersReceived\"}]}}", id, pattern, resourceType);
        return message;
    }

    public static String buildGetContinueInterceptedRequestMessage(int id, String interceptionId, String response) {
        String encodedResponse = new String(Base64.encodeBase64(response.getBytes()));
        String message = String.format("{\"id\":%s,\"method\":\"Network.continueInterceptedRequest\",\"params\":{\"interceptionId\":\"%s\",\"rawResponse\":\"%s\"}}", id, interceptionId, encodedResponse);
        return message;
    }

    public static String buildGetContinueInterceptedRequestEncodedMessage(int id, String interceptionId, String encodedResponse) {
        String message = String.format("{\"id\":%s,\"method\":\"Network.continueInterceptedRequest\",\"params\":{\"interceptionId\":\"%s\",\"rawResponse\":\"%s\"}}", id, interceptionId, encodedResponse);
        return message;
    }

}
