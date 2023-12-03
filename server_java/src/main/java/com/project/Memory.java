package com.project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Random;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONObject;

public class Memory extends WebSocketServer {

    static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    private ArrayList<Game> games;

    public Memory(int port) {
        super(new InetSocketAddress(port));
        games = new ArrayList<>();
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
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        // Quan un client es desconnecta
        String clientId = getConnectionId(conn);

        // Informem a tothom que el client s'ha desconnectat
        JSONObject objCln = new JSONObject("{}");
        objCln.put("type", "disconnected");
        objCln.put("from", "server");
        objCln.put("id", clientId);
        conn.send(objCln.toString());

        // Mostrem per pantalla (servidor) la desconnexió
        System.out.println("Client disconnected '" + clientId + "'");
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.err.println("Conectado");
        

       

        // Take the usrID
        String clientId = getConnectionId(conn);

        // Send the usrID to him
        JSONObject objWlc = new JSONObject("{}");
        objWlc.put("type", "conn");
        objWlc.put("usrID", clientId);
        conn.send(objWlc.toString());

        // Show the new conexion onthe server terminal
        String host = conn.getRemoteSocketAddress().getAddress().getHostAddress();
        System.out.println("New client (" + clientId + "): " + host);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        String clientId = getConnectionId(conn);

        try {
            JSONObject objRequest = new JSONObject(message);
            String type = objRequest.getString("type");

            if (type.equalsIgnoreCase("creategame")) {
                boolean stop = false;
                Random rnd = new Random();
                Integer id = 0;

                while (!stop) {
                    stop = true;
                    id = rnd.nextInt(1000);
                    for (Game g : games) {
                        if (g.getId().equals(id.toString())) {
                            stop = false;
                        }
                    }
                }

                Game g = new Game(id.toString());
                Player p = new Player(clientId);

                p.setTurn();
                g.addPlayer(p, g.getPlayersNumber());
                games.add(g);

                // Send the game ID to the usr
                JSONObject objCln = new JSONObject("{}");
                objCln.put("type", "gameCreated");
                objCln.put("gameID", g.getId());
                conn.send(objCln.toString());
                 JSONObject objma = new JSONObject("{}");
                objma.put("type", "matriz");
                objma.put("matriz",  g.createMemoryMatrix().toString());
                conn.send(objma.toString());
                System.out.println("Game " + g.getId() + " created succesfully.");

            } 
             if (type.equalsIgnoreCase("cart_info")) {
                    int row = objRequest.getInt("row");
                    int col = objRequest.getInt("col");
                    conn.send(objRequest.toString());

                }
            else if (type.equalsIgnoreCase("joingame")) {
                String joinID = objRequest.getString("gameID");
                boolean idExist = false;

                // Loock in the games list to found the game
                // Check if the game exist and if its not complete
                for (Game g : games) {
                    if (g.getId().equals(joinID) & g.getPlayersNumber() < 2) {
                        idExist = true;
                        // Add the second player to the game class
                        g.addPlayer(new Player(clientId), g.getPlayersNumber());

                        // Complete the players info and send to him his game status
                        for (Player p : g.getPlayers()) {
                            p.setEnemyID(g.getEnemy(p.getId()).getId());
                            sendGameStatus(p, g.getEnemy(p.getId()), getClientById(p.getId()));
                        }
                    }
                }

                // If the gameId dosn't exist send a error missage to the client
                if (!idExist) {
                    JSONObject objCln = new JSONObject("{}");
                    objCln.put("type", "gameCreated");
                    objCln.put("value", "201");
                    conn.send(objCln.toString());

                    System.out.println("El usuario a mandado un ID erroneo o la sala estaba llena");
                }

            } else if (type.equalsIgnoreCase("markcard")) {

            } else if (type.equalsIgnoreCase("flipcard")) {

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

    public void runServerBucle() {
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

    public void sendGameStatus(Player usr, Player enemy, WebSocket conn) {
        JSONObject objResponse = new JSONObject("{}");
        objResponse.put("type", "gameSatus");
        objResponse.put("enemyID", usr.getEnemyID());
        objResponse.put("turn", usr.getTurn());
        objResponse.put("playerPoints", usr.getPoints());
        objResponse.put("enemyPoints", enemy.getPoints());
        conn.send(objResponse.toString());
    }

    public String getConnectionId(WebSocket connection) {
        String name = connection.toString();
        return name.replaceAll("org.java_websocket.WebSocketImpl@", "").substring(0, 3);
    }

    public WebSocket getClientById(String clientId) {
        for (WebSocket ws : getConnections()) {
            String wsId = getConnectionId(ws);
            if (clientId.compareTo(wsId) == 0) {
                return ws;
            }
        }

        return null;
    }
}