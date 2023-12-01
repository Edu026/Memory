package com.project;

import java.net.URI;
import java.net.URISyntaxException;



public class Main {
    public static void main(String[] args) {
        int port = 8888;
        String host = "localhost";
        String location = "ws://" + host + ":" + port;

        ChatClient chatClient;
        try {
            chatClient = new ChatClient(new URI(location));
            chatClient.runClientBucle();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}