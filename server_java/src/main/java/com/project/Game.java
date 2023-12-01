package com.project;

import java.util.Random;

public class Game {

    private String[][] cards; // Matrix of cards
    private Player[] players; // Array with players
    private int[][] flipedCards; // Array with the index of the cards taht are fliped
    private String id; // ID of the game

    public Game(String id) {
        this.id = id;
        cards = createCartsMatrix();
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

    private String[][] createCartsMatrix() {
        String[][] cards = new String[4][4];

        String[] options = { "blue", "blue", "green", "green", "yellow", "yellow", "pink", "pink", "orange", "orange",
                "red", "red", "brown", "brown", "purple", "purple" };

        boolean stop;

        Random rnd = new Random();

        int colIndex = 0;
        int rowIndex = 0;

        for (String opt : options) {

            stop = false;

            while (!stop) {

                colIndex = rnd.nextInt(4);
                rowIndex = rnd.nextInt(4);

                if (cards[rowIndex][colIndex] == null)
                    cards[rowIndex][colIndex] = opt;

            }

        }

        return cards;
    }
}