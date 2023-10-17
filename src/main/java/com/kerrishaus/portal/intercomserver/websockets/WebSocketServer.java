package com.kerrishaus.portal.intercomserver.websockets;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

public class WebSocketServer extends org.java_websocket.server.WebSocketServer
{
    public HashMap<String, WebSocket> clients = new HashMap<String, WebSocket>();

    public WebSocketServer(InetSocketAddress address)
    {
        super(address);
    }

    @Override
    public void onStart()
    {
        System.out.println("Server started on " + this.getPort() + " successfully.");
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake)
    {
        conn.send("connectionAccepted");

        this.clients.put(conn.getRemoteSocketAddress().toString(), conn);

        conn.send("yourAddress;" + conn.getRemoteSocketAddress());

        StringBuilder clientList = new StringBuilder();

        for(Map.Entry<String, WebSocket> entry : this.clients.entrySet())
        {
            String key = entry.getKey();
            WebSocket value = entry.getValue();

            clientList.append(value.getRemoteSocketAddress()).append(";");
        }

        this.broadcast("clientList;" + clientList);

        System.out.println("New connection established: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote)
    {
        clients.remove(conn.getRemoteSocketAddress().toString());

        StringBuilder clientList = new StringBuilder();

        for(Map.Entry<String, WebSocket> entry : this.clients.entrySet())
        {
            String key = entry.getKey();
            WebSocket value = entry.getValue();

            clientList.append(value.getRemoteSocketAddress()).append(";");
        }

        this.broadcast("clientList;" + clientList);

        System.out.println("closed " + conn.getRemoteSocketAddress() + " with exit code " + code + " additional info: " + reason);
    }

    @Override
    public void onMessage(WebSocket conn, String message)
    {
        System.out.println("Received string " + message + " from " + conn.getRemoteSocketAddress());

        if (message.startsWith("{\"type\":\"talkRequest\","))
        {
            conn.send("talkPermitted");
        }
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer message)
    {
        System.out.println("received ByteBuffer from "	+ conn.getRemoteSocketAddress() + ", size: " + message.limit());

        this.broadcast("voiceData;" + conn.getRemoteSocketAddress());
        this.broadcast(message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex)
    {
        System.err.println("an error occurred on connection " + conn.getRemoteSocketAddress()  + ":" + ex);
    }
}