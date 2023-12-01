package com.project;
import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;


import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ControllerGame {

    @FXML
    private Rectangle card00, card01, card02, card03, card10, card11, card12, card13, card20, card21, card22, card23, card30, card31, card32, card33;
    // Create a map to keep track of color assignments
    HashMap<Color, Integer> colorCounter = new HashMap<>();
    HashMap<Rectangle, Color> cardsColors = new HashMap<>();

    public void initialize() {
        ArrayList<Color> colors = new ArrayList<>() {{{
            add(Color.RED);
            add(Color.BLUE);
            add(Color.GREEN);
            add(Color.YELLOW);
            add(Color.PURPLE);
            add(Color.ORANGE);
            add(Color.PINK);
            add(Color.BROWN);
        }}};
        ArrayList<Rectangle> cards = new ArrayList<>() {{{
            add(card00);
            add(card01);
            add(card02);
            add(card03);
            add(card10);
            add(card11);
            add(card12);
            add(card13);
            add(card20);
            add(card21);
            add(card22);
            add(card23);
            add(card30);
            add(card31);
            add(card32);
            add(card33);
        }}};

        for (Rectangle card : cards) {
            card.setFill(Color.CYAN);
            card.setOnMouseClicked(e -> clicked(card));
            Color color = colors.get((int) (Math.random() * colors.size()));
            if (colorCounter.containsKey(color)) {
                int count = colorCounter.get(color);
                if (count < 2) {
                    colorCounter.put(color, count + 1);
                    card.setFill(color);
                    cardsColors.put(card, color);
                } else {
                    colors.remove(color);
                }
            } else {
                colorCounter.put(color, 1);
                card.setFill(color);
                cardsColors.put(card, color);
            }
        }
    }
    @FXML
    public void clicked(Rectangle card) {
        RotateTransition rotateTransition = new RotateTransition(Duration.seconds(1), card);
        rotateTransition.setByAngle(180); // Rotate by 180 degrees
        rotateTransition.setAxis(Rotate.Y_AXIS);
        rotateTransition.setAutoReverse(true);

        if (card.getFill() == Color.RED) {
            card.setFill(Color.BLUE); // Back side color
        } else {
            card.setFill(Color.RED);
        }

        // Play the animation
        rotateTransition.play();    }

}
