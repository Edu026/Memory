package com.project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javafx.scene.paint.Color;

public class Board {

    static private String[][] cards; // Matrix of cards
    private Player[] players; // Array with players
    private int[][] flipedCards; // Array with the index of the cards taht are fliped
    private String id; // ID of the game
    private int currentPlayerIndex;
    public Board(String id) {
        this.id = id;
        currentPlayerIndex = 0; 
        players = new Player[2];
        flipedCards = new int[2][2];
        flipedCards[0][0] = -1;
        flipedCards[0][1] = -1;
        flipedCards[1][0] = -1;
        flipedCards[1][1] = -1;

    }
   
    public String getId() {
        return id;
    }

    public Player[] getPlayers() {
        return players;
    } 

    

    public void switchTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
    }

    // Get a single cards of the matrix
    public String getCard(int row, int col) {
        return cards[row][col];
    }

    public int[][] getFlipedCards() {
        return flipedCards;
    }

    public int getFlipedCardsCount() {
        int count = 0;

        if (flipedCards[0][0] == -1)
            count++;
        if (flipedCards[1][0] == -1)
            count++;

        return count;
    }

    public int getPlayersNumber() {
        int count = 0;

        for (Player p : players) {
            if (p != null)
                count++;
        }

        return count;
    }

    public Player getEnemy(String id) {
        for (Player p : players) {
            if (!p.getId().equals(id)) {
                return p;
            }
        }

        return null;
    }

    public void addPlayer(Player p, int index) {
        
        players[index] = p;
    }

    public void addFlipedCards(int row, int col, int index) {
        flipedCards[index][0] = row;
        flipedCards[index][1] = col;
    }

    public void clearFlipedCards() {
        flipedCards[0][0] = -1;
        flipedCards[0][1] = -1;
        flipedCards[1][0] = -1;
        flipedCards[1][1] = -1;
    }

     public static Color[][] createMemoryMatrix() {
        // Lista de colores básicos en formato hexadecimal
        String[] basicColors = {
                "#FF0000", "#00FF00", "#0000FF", "#FFFF00",
                "#FF00FF", "#FFA500", "#800080", "#008080"
        };

        // Duplicar los colores para tener pares en la matriz
        List<String> allColors = new ArrayList<>();
        for (String color : basicColors) {
            allColors.add(color);
        }
        allColors.addAll(allColors);

        // Barajar el array de colores para obtener un orden aleatorio
        Collections.shuffle(allColors);

        // Crear la matriz de colores
        Color[][] colorMatrix = new Color[4][4];
        int colorIndex = 0;

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                // Convertir la cadena hexadecimal del color a un objeto Color
                cards[i][j]=allColors.get(colorIndex);
                colorMatrix[i][j] = Color.web(allColors.get(colorIndex));
                colorIndex++;
            }
        }

        return colorMatrix;
    }

}