package com.kerrishaus.portal.intercomserver;

import java.net.InetSocketAddress;

import com.kerrishaus.portal.intercomserver.websockets.WebSocketServer;

public class IntercomServer
{
    public static void main(String[] args)
    {
        String host = "localhost";
        int port = 27001;

        WebSocketServer chatserver = new WebSocketServer(new InetSocketAddress(host, port));

        chatserver.setConnectionLostTimeout(30);
        chatserver.start();
    }
}