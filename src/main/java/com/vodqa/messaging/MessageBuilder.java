package com.vodqa.messaging;

public class MessageBuilder {

    public static String geoLocationMessage(int id, double latitude, double longitude){
        Message message = new Message(id,"Emulation.setGeolocationOverride");
        message.addParameter("latitude",latitude);
        message.addParameter("longitude",longitude);
        message.addParameter("accuracy",100);
        return message.toJSON();
    }




}
