package com.project;

import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;

import com.project.SocketsClient.OnCloseObject;

import javafx.animation.PauseTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class AppData {

    private static final AppData INSTANCE = new AppData();
    private SocketsClient socketClient;
    private String ip = "localhost";
    private String port = "8888";
    private String name = "";
    
    String rival_name = "";
    String rival_id = "";
    
    private ConnectionStatus connectionStatus = ConnectionStatus.DISCONNECTED;

    private String mySocketId;
    private List<String> clients = new ArrayList<>();
    private String selectedClient = "";
    private Integer selectedClientIndex;
    private StringBuilder messages = new StringBuilder();

    public enum ConnectionStatus {
        DISCONNECTED, DISCONNECTING, CONNECTING, CONNECTED
    }

    // Boolean per saber si és el meu torn o no
    boolean isMyTurn = false;
    boolean isWinner = false;

    // Enter per saber els meus punts i els del meu rival
    int myPoints = 0;
    int rivalPoints = 0;
    List<List<Integer>> board;
    ArrayList<List<Integer>> currentBoard = new ArrayList<>();

    // Array para mostrar si las imagenes son visible o no
    ArrayList<Boolean> imagesVisibility = new ArrayList<>(Collections.nCopies(16, false));

    
   

    
    private AppData() {}

    public static AppData getInstance() {
        return INSTANCE;
    }
    
    public String getLocalIPAddress() throws SocketException, UnknownHostException {
        
        String localIp = "";
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface ni = networkInterfaces.nextElement();
            Enumeration<InetAddress> inetAddresses = ni.getInetAddresses();
            while (inetAddresses.hasMoreElements()) {
                InetAddress ia = inetAddresses.nextElement();
                if (!ia.isLinkLocalAddress() && !ia.isLoopbackAddress() && ia.isSiteLocalAddress()) {
                    System.out.println(ni.getDisplayName() + ": " + ia.getHostAddress());
                    localIp = ia.getHostAddress();
                    // Si hi ha múltiples direccions IP, es queda amb la última
                }
            }
        }

        // Si no troba cap direcció IP torna la loopback
        if (localIp.compareToIgnoreCase("") == 0) {
            localIp = InetAddress.getLocalHost().getHostAddress();
        }
        return localIp;
    }

    public void connectToServer() {
        try {
            URI location = new URI("ws://" + ip + ":" + port);
            socketClient = new SocketsClient(
                    location,
                    (ServerHandshake handshake) ->  { this.onOpen(handshake);},
                    (String message) ->             { this.onMessage(message); },
                    (OnCloseObject closeInfo) ->    { this.onClose(closeInfo); },
                    (Exception ex) ->               { this.onError(ex); }
            );
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        connectionStatus = ConnectionStatus.CONNECTING;
        socketClient.connect();
        UtilsViews.setViewAnimating("Connecting");
        
      
        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(event -> {
            if (connectionStatus == ConnectionStatus.CONNECTED) {
                CtrlLayoutConnected ctrlConnected = (CtrlLayoutConnected) UtilsViews.getController("Connected");
                // ctrlConnected.updateInfo();
                UtilsViews.setViewAnimating("Connected");
            } else {
                UtilsViews.setViewAnimating("Disconnected");
            }
        });
        pause.play();
    }

    public void disconnectFromServer() {
        connectionStatus = ConnectionStatus.DISCONNECTING;
        UtilsViews.setViewAnimating("Disconnecting");
        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(event -> {
            socketClient.close();
        });
        pause.play();
    }

    private void onOpen (ServerHandshake handshake) {
        System.out.println("Handshake: " + handshake.getHttpStatusMessage());
        connectionStatus = ConnectionStatus.CONNECTED; 
    }

    private void onMessage(String message) {
        JSONObject data = new JSONObject(message);

        if (connectionStatus != ConnectionStatus.CONNECTED) {
            connectionStatus = ConnectionStatus.CONNECTED;
        }

        String type = data.getString("type");
        System.out.println(data);
        switch (type) {
            case "start_game":
                System.out.println("Mi rival es " + data.getString("rival_name"));
                rival_id=data.getString("rival_id");
               
                System.out.println("Comienza mi rival " + data.getString("isRivalFirst"));
                isMyTurn=false;
                break;
            case "new_board":
                List<List<Integer>> bord=jsonArrayToList(data.getJSONArray("board"));
                setBoard(bord);
            
                break;
            case "cards_flip":
                showImageAtPosition(Integer.valueOf(data.getString("row")),Integer.valueOf(data.getString("row")));
                break;
            case "canvi_torn":
                    isMyTurn=true;
                    rivalPoints=Integer.valueOf(data.getString("points"));    
                break;
               
            case "list":
                clients.clear();
                data.getJSONArray("list").forEach(item -> clients.add(item.toString()));
                clients.remove(mySocketId);
                messages.append("List of clients: ").append(data.getJSONArray("list")).append("\n");
                updateClientList();
                break;
            case "id":
                JSONObject msg = new JSONObject();
                msg.put("type", "username");
                msg.put("id", data.getString("value"));
                msg.put("name", getName());
                socketClient.send(msg.toString());
              
                break;
            case "connected":
                clients.add(data.getString("id"));
                clients.remove(mySocketId);
                messages.append("Connected client: ").append(data.getString("id")).append("\n");
                updateClientList();
                break;
            case "disconnected":
                String removeId = data.getString("id");
                if (selectedClient.equals(removeId)) {
                    selectedClient = "";
                }
                clients.remove(data.getString("id"));
                messages.append("Disconnected client: ").append(data.getString("id")).append("\n");
                updateClientList();
                break;
            case "private":
                messages.append("Private message from '")
                        .append(data.getString("from"))
                        .append("': ")
                        .append(data.getString("value"))
                        .append("\n");
                break;
            default:
                messages.append("Message from '")
                        .append(data.getString("from"))
                        .append("': ")
                        .append(data.getString("value"))
                        .append("\n");
                break;
        }
        if (connectionStatus == ConnectionStatus.CONNECTED) {
            CtrlLayoutConnected ctrlConnected = (CtrlLayoutConnected) UtilsViews.getController("Connected");
            // ctrlConnected.updateMessages(messages.toString());        
        }
    }

    public void onClose(OnCloseObject closeInfo) {
        connectionStatus = ConnectionStatus.DISCONNECTED;
        UtilsViews.setViewAnimating("Disconnected");
    }

    public void onError(Exception ex) {
        System.out.println("Error: " + ex.getMessage());
    }

    public void refreshClientsList() {
        JSONObject message = new JSONObject();
        message.put("type", "list");
        socketClient.send(message.toString());
    }

    public void updateClientList() {
        if (connectionStatus == ConnectionStatus.CONNECTED) {
            CtrlLayoutConnected ctrlConnected = (CtrlLayoutConnected) UtilsViews.getController("Connected");
          
        }
    }

    public void selectClient(int index) {
        if (selectedClientIndex == null || selectedClientIndex != index) {
            selectedClientIndex = index;
            selectedClient = clients.get(index);
        } else {
            selectedClientIndex = null;
            selectedClient = "";
        }
    }

    public Integer getSelectedClientIndex() {
        return selectedClientIndex;
    }

    public void send(String msg) {
        if (selectedClientIndex == null) {
            broadcastMessage(msg);
        } else {
            privateMessage(msg);
        }
    }

    public void broadcastMessage(String msg) {
        JSONObject message = new JSONObject();
        message.put("type", "broadcast");
        message.put("value", msg);
        socketClient.send(message.toString());
    }

    public void privateMessage(String msg) {
        if (selectedClient.isEmpty()) return;
        JSONObject message = new JSONObject();
        message.put("type", "private");
        message.put("value", msg);
        message.put("destination", selectedClient);
        socketClient.send(message.toString());
    }
    public void showImageAtPosition(int row, int col) {
        int position = row * board.get(0).size() + col;
        // Verifica si la posición está dentro de los límites
        if (position >= 0 && position < imagesVisibility.size()) {
            // Verifica si la imagen en esa posición aún no ha sido volteada
            if (!imagesVisibility.get(position)) {
                // Muestra la imagen en la posición especificada
                imagesVisibility.set(position, true);

                //  llamar a un método en el controlador de la interfaz gráfica
                CtrlLayoutConnected ctrlConnected = (CtrlLayoutConnected) UtilsViews.getController("Connected");
                ctrlConnected.showImageAtPosition(row, col);
            }
        }
    }
    
    public List<List<Integer>> Board() {
        
        // Especifiquem les dimensions de la matriu
        int rows = 4;
        int cols = 4;

        // Creem un Array en 2D per representar la matriu
        int[][] matrix = new int[rows][cols];
        
        // Creem un ArrayList per mantenir constància sobre les ocurrències
        ArrayList<Integer> occurrences = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            occurrences.add(2);
        }

        // Inicialitzem la matriu amb números entre l'1 i el 8
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int number = generateUniqueNumber(occurrences);
                matrix[i][j] = number;
                occurrences.set(number - 1, occurrences.get(number - 1) - 1);
            }
        }

        // Creem una llista per a cadascuna de les files
        List<List<Integer>> rowsList = new ArrayList<>();

        // Populem la llista amb els números de cadascuna de les files
        for (int i = 0; i < rows; i++) {
            List<Integer> rowList = new ArrayList<>();
            for (int j = 0; j < cols; j++) {
                rowList.add(matrix[i][j]);
            }
            rowsList.add(rowList);
        }

        // Imprimim les llistes per cada fila per pantalla
        for (int i = 0; i < rows; i++) {
            System.out.println(rowsList.get(i));
        }

        // Retornem el taulell del joc
        return rowsList;
    }
    private static List<List<Integer>> jsonArrayToList(JSONArray jsonArray) {
        List<List<Integer>> result = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONArray innerArray = jsonArray.getJSONArray(i);
            List<Integer> innerList = new ArrayList<>();

            for (int j = 0; j < innerArray.length(); j++) {
                innerList.add(innerArray.getInt(j));
            }

            result.add(innerList);
        }

        return result;
    }
    
    private int generateUniqueNumber(List<Integer> occurrences) {
        int number;
        do {
            number = new Random().nextInt(8) + 1;
        } while (occurrences.get(number - 1) == 0);
        return number;
    }

    public String getIp() {
        return ip;
    }

    public String setIp (String ip) {
        return this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public String setPort (String port) {
        return this.port = port;
    }

    public String getName() {
        return name;
    }

    public String setName(String name) {
        return this.name = name;
    }

    public String getMySocketId () {
        return mySocketId;
    }
    public List<List<Integer>>  setBoard(List<List<Integer>> board) {
        return this.board = board;
    }
    public List<List<Integer>>  getBoard() {
        return board;
    }
}