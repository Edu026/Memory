package com.project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.ArrayList;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONArray;
import org.json.JSONObject;

public class Server extends WebSocketServer {

    static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    AppData appData = AppData.getInstance();

    public Server (int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onStart() {
        // Quan el servidor s'inicia
        String host = getAddress().getAddress().getHostAddress();
        int port = getAddress().getPort();
        System.out.println("WebSockets server running at: ws://" + host + ":" + port);
        System.out.println("Type 'exit' to stop and exit server.");
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        // Quan un client es connecta
        String clientId = getConnectionId(conn);

        
        JSONObject objWlc = new JSONObject("{}");
        objWlc.put("type", "private");
        objWlc.put("from", "server");
        objWlc.put("value", "Welcome to Memory");
        conn.send(objWlc.toString()); 

        // Li enviem el seu identificador
        JSONObject objId = new JSONObject("{}");
        objId.put("type", "id");
        objId.put("from", "server");
        objId.put("value", clientId);
        conn.send(objId.toString()); 

    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        // Quan un client es desconnecta
        String clientId = getConnectionId(conn);

        // Informem a tothom que el client s'ha desconnectat
        JSONObject objCln = new JSONObject("{}");
        objCln.put("type", "disconnected");
        objCln.put("from", "server");
        objCln.put("id", clientId);
        broadcast(objCln.toString());

        // Mostrem per pantalla (servidor) la desconnexió
        System.out.println("Client disconnected '" + clientId + "'");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        // Quan arriba un missatge
        String clientId = getConnectionId(conn);
        System.out.println(message);
        System.out.println("====================");
        try {
            JSONObject objRequest = new JSONObject(message);
            String type = objRequest.getString("type");

            
            if (type.equalsIgnoreCase("username")) {
                appData.UserNameById.put(objRequest.getString("id"), objRequest.getString("name"));
                handleNewPlayer(clientId);
                

            } else if (type.equalsIgnoreCase("cards_flip")) {
                JSONObject objResponse = new JSONObject("{}");
                        objResponse.put("type", "cards_flip");
                        objResponse.put("row", objRequest.getString("row"));
                        objResponse.put("col",  objRequest.getString("col"));
                        WebSocket desti = getClientById(objRequest.getString("destination"));
                        if (desti != null) {
                            desti.send(objResponse.toString()); 
                        }
            
            } else if (type.equalsIgnoreCase("canvi_torn")) {
                JSONObject objResponse = new JSONObject();
                objResponse.put("type", "canvi_torn");
                objResponse.put("points", objRequest.getInt("points"));
                WebSocket desti = getClientById(objRequest.getString("destination"));
                if (desti != null) {
                    desti.send(objResponse.toString()); 
                }

            } else if (type.equalsIgnoreCase("end_game")) {
                JSONObject objResponse = new JSONObject("{}");
                objResponse.put("type", "end_game");
                objResponse.put("sender_points", objRequest.getInt("sender_points"));
                objResponse.put("rival_points", objRequest.getInt("rival_points"));
                WebSocket desti = getClientById(objRequest.getString("destination"));
                if (desti != null) {
                    desti.send(objResponse.toString()); 
                }

            } else if (type.equalsIgnoreCase("play_again")) {
                handleNewPlayer(clientId);
            
            } else if (type.equalsIgnoreCase("list")) {
                // El client demana la llista de tots els clients
                System.out.println("Client '" + clientId + "'' requests list of clients");
                sendList(conn);

            } 
                
            else if (type.equalsIgnoreCase("broadcast")) {
                // El client envia un missatge a tots els clients
                System.out.println("Client '" + clientId + "'' sends a broadcast message to everyone");

                JSONObject objResponse = new JSONObject("{}");
                objResponse.put("type", "broadcast");
                objResponse.put("from", clientId);
                objResponse.put("value", objRequest.getString("value"));
                broadcast(objResponse.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        // Quan hi ha un error
        ex.printStackTrace();
    }

    public void runServerBucle () {
        boolean running = true;
        try {
            System.out.println("Starting server");
            start();
            while (running) {
                String line;
                line = in.readLine();
                if (line.equals("exit")) {
                    running = false;
                }
            } 
            System.out.println("Stopping server");
            stop(1000);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }  
    }

    public void sendList (WebSocket conn) {
        JSONObject objResponse = new JSONObject("{}");
        objResponse.put("type", "list");
        objResponse.put("from", "server");
        objResponse.put("list", getClients());
        conn.send(objResponse.toString()); 
    }

    public String getConnectionId (WebSocket connection) {
        String name = connection.toString();
        return name.replaceAll("org.java_websocket.WebSocketImpl@", "").substring(0, 3);
    }

    public String[] getClients () {
        int length = getConnections().size();
        String[] clients = new String[length];
        int cnt = 0;

        for (WebSocket ws : getConnections()) {
            clients[cnt] = getConnectionId(ws);               
            cnt++;
        }
        return clients;
    }

    public WebSocket getClientById (String clientId) {
        for (WebSocket ws : getConnections()) {
            String wsId = getConnectionId(ws);
            if (clientId.compareTo(wsId) == 0) {
                return ws;
            }               
        }
        
        return null;
    }

    public void handleNewPlayer(String clientId) {
        
        String[] NewGamePlayers = appData.emparejamiento(clientId);
        if (NewGamePlayers != null) {
            ArrayList<WebSocket> connections = new ArrayList<>();

            // Indicamos a cada jugador su rival y guardamos la conexion en un array
            for (int i = 0; i < NewGamePlayers.length; i++) {
                int indexArray = (i + 1) % 2;
                JSONObject objResponse = new JSONObject("{}");
                objResponse.put("type", "start_game");
                objResponse.put("from", "server");
                objResponse.put("rival_id", NewGamePlayers[indexArray]);
                objResponse.put("rival_name", appData.UserNameById.get(NewGamePlayers[indexArray]));
                objResponse.put("isRivalFirst", (i == 1));

                WebSocket desti = getClientById(NewGamePlayers[i]);

                if (desti != null) {
                    desti.send(objResponse.toString()); 
                    connections.add(desti);
                }
            }
            System.out.println("DESTINOS = "+connections);
            // Generamos tablero y lo enviamos a los jugadores por primera vez
            int[][] newBoard = appData.randomBoard();
            
            JSONObject json = new JSONObject();
            json.put("type", "new_board");
            JSONArray json_board = new JSONArray();
            for (int[] row : newBoard) {
                json_board.put(row);
            }

            json.put("board", json_board);
            appData.sendToUsers(connections, json);
        }
    }

}