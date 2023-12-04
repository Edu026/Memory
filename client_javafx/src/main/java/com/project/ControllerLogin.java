package com.project;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URI;
import java.net.URISyntaxException;

public class ControllerLogin {
    @FXML
    private Button buttonConectar;

    @FXML
    private TextField inputServer, inputPort, inputName;

    private Client webSocketClient;

    public void initialize() {
        buttonConectar.setOnAction(e -> loginServer());
    }

    private void loginServer() {
        // Obtén la información del servidor y el puerto desde los campos de texto
        String server = inputServer.getText();
        String port = inputPort.getText();

        // Construye la URI del servidor WebSocket
        try {
            URI uri = new URI("ws://" + server + ":" + port);
            
            // Instancia la clase Client y establece la conexión
            webSocketClient = new Client(uri);
            webSocketClient.connect();
            
            UtilsViews.setView("Game");
            webSocketClient.setName(inputName.toString());
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
            // Maneja la excepción según tus necesidades
        }
    }
}
