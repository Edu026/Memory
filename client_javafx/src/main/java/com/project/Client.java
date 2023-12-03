package com.project;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;

import javafx.scene.paint.Color;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class Client extends WebSocketClient {
    
    public Client(URI serverUri) {
        super(serverUri);
        
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        
        JSONObject objCln = new JSONObject("{}");
                objCln.put("type", "creategame");
                send(objCln.toString());
    }

    @Override
    public void onMessage(String message) {
        JSONObject objma = new JSONObject(message);
        ControllerGame controllerGame = new ControllerGame();
        if ("matriz".equals(objma.getString("type"))) {
            JSONArray matrizArray = objma.getJSONArray("matriz");
            List<List<String>> colorMatrix = jsonArrayToMatrix(matrizArray);
    
            
            // Luego, pasas la matriz de colores a la clase ControllerGame para actualizar las cartas
            controllerGame.updateMatrix(colorMatrix);
        }
         if ("card_info".equals(objma.getString("type"))) {
            int row = objma.getInt("row");
            int col = objma.getInt("col");
            controllerGame.rotateCard(row, col);
            
        }
        
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        // Implementar según sea necesario
    }

    @Override
    public void onError(Exception ex) {
        // Implementar según sea necesario
    }

    public static ArrayList<String> jsonArrayToList(JSONArray jsonArray) {
        ArrayList<String> list = new ArrayList<>();

        for (Object element : jsonArray) {
            // Asegúrate de que los elementos en el JSONArray sean del tipo String
            if (element instanceof String) {
                list.add((String) element);
            }
            // Si los elementos pueden ser de varios tipos, ajusta la lógica según tus necesidades
        }

        return list;
    }

    private List<List<String>> jsonArrayToMatrix(JSONArray jsonArray) {
        List<List<String>> matrix = new ArrayList<>();
    
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONArray rowArray = jsonArray.getJSONArray(i);
            List<String> row = new ArrayList<>();
    
            for (int j = 0; j < rowArray.length(); j++) {
                row.add(rowArray.getString(j));
            }
    
            matrix.add(row);
        }
    
        return matrix;
    }
    public void sendCardInfo(CardInfo cardInfo) {
        // Crear un objeto JSON con la información de la carta
        JSONObject cardJson = new JSONObject();
        cardJson.put("type", "card_info");
        cardJson.put("row", cardInfo.getRow());
        cardJson.put("col", cardInfo.getCol());

        // Enviar el JSON al servidor
        send(cardJson.toString());
    }
}