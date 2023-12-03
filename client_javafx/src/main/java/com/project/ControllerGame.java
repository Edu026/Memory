package com.project;
import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.util.List;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ControllerGame {

    @FXML
    private Rectangle card00, card01, card02, card03, card10, card11, card12, card13, card20, card21, card22, card23, card30, card31, card32, card33;
    // Create a map to keep track of color assignments
    private final HashMap<Color, Integer> colorCounter = new HashMap<>();
    private final HashMap<Rectangle, Color> cardsColors = new HashMap<>();
    static List<Color> color;
    // Variables para el seguimiento de las dos cartas seleccionadas
    private Rectangle firstSelectedCard;
    private Rectangle secondSelectedCard;
    private Client webSocketClient;

    private Rectangle[][] cardMatrix = new Rectangle[4][4];

    public void initialize() {

        cardMatrix[0][0] = card00; cardMatrix[0][1] = card01; cardMatrix[0][2] = card02; cardMatrix[0][3] = card03;
        cardMatrix[1][0] = card10; cardMatrix[1][1] = card11; cardMatrix[1][2] = card12; cardMatrix[1][3] = card13;
        cardMatrix[2][0] = card20; cardMatrix[2][1] = card21; cardMatrix[2][2] = card22; cardMatrix[2][3] = card23;
        cardMatrix[3][0] = card30; cardMatrix[3][1] = card31; cardMatrix[3][2] = card32; cardMatrix[3][3] = card33;

        Color initialColor = Color.GREY;

        // Configurar las cartas en un estado inicial sin color
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
            cardMatrix[i][j].setFill(initialColor);
        }
    }
    }
    @FXML
    public void clicked(Rectangle card) {
        RotateTransition rotateTransition = new RotateTransition(Duration.seconds(1), card);
        rotateTransition.setByAngle(180); // Rotar 180 grados
        rotateTransition.setAxis(Rotate.Y_AXIS);
        rotateTransition.setAutoReverse(true);

        if (firstSelectedCard == null) {
            // Si no hay ninguna carta seleccionada, esta es la primera
            firstSelectedCard = card;
            card.setFill(cardsColors.get(card));
            sendCardInfoToServer(firstSelectedCard);
        } else if (secondSelectedCard == null && !card.equals(firstSelectedCard)) {
            // Si es la segunda carta y no es la misma que la primera
            secondSelectedCard = card;
            card.setFill(cardsColors.get(card));
            sendCardInfoToServer(secondSelectedCard);
            // Verificar si las cartas coinciden
            if (!cardsColors.get(firstSelectedCard).equals(cardsColors.get(secondSelectedCard))) {
                // Si las cartas no coinciden, volver al cian después de un breve tiempo
                resetCards();
            } else {
                // Si las cartas coinciden, enviar la información al servidor y dejarlas con el color
                sendCardInfoToServer(firstSelectedCard);
                sendCardInfoToServer(secondSelectedCard);
                firstSelectedCard = null;
                secondSelectedCard = null;
            }
        }

        // Reproducir la animación
        rotateTransition.play();
    }

    // Método para resetear las dos cartas seleccionadas después de un tiempo
    private void resetCards() {
        Duration delay = Duration.seconds(1);
        firstSelectedCard.setFill(Color.CYAN);
        secondSelectedCard.setFill(Color.CYAN);

        // Restablecer las dos cartas seleccionadas después de un breve tiempo
        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(delay);
        pause.setOnFinished(e -> {
            firstSelectedCard.setFill(Color.CYAN);
            secondSelectedCard.setFill(Color.CYAN);
            firstSelectedCard = null;
            secondSelectedCard = null;
        });

        pause.play();
    }

    // Método para enviar la información de la carta al servidor
    private void sendCardInfoToServer(Rectangle card) {
        // Obtener la información necesaria de la carta (puedes ajustar según tus necesidades)
        int row = -1, col = -1;

        // Buscar la posición de la carta en la matriz
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (cardMatrix[i][j].equals(card)) {
                    row = i;
                    col = j;
                    break;
                }
            }
        }

        // Crear un objeto para representar la información de la carta
        CardInfo cardInfo = new CardInfo(row, col);

        // Enviar la información al servidor
        if (webSocketClient != null) {
            webSocketClient.sendCardInfo(cardInfo);
        }
    }

    // Método para recibir la instancia de Client desde el controlador principal
    public void setWebSocketClient(Client webSocketClient) {
        this.webSocketClient = webSocketClient;
    }

    public void updateMatrix(List<List<String>> colorMatrix) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                String colorHex = colorMatrix.get(i).get(j);
                Color color = Color.web(colorHex);
                cardMatrix[i][j].setFill(color);
            }
        }
    }
    public void rotateCard(int row, int col) {
        Rectangle card = cardMatrix[row][col];

        RotateTransition rotateTransition = new RotateTransition(Duration.seconds(1), card);
        rotateTransition.setByAngle(180); // Rotar 180 grados
        rotateTransition.setAxis(Rotate.Y_AXIS);
        rotateTransition.setAutoReverse(true);

        // Play the animation
        rotateTransition.play();
    }
}
