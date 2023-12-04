package com.project;

public class Player {
    private String id;
    private String name;
    private String enemyID;
    private int points;
    private boolean turn;

    public Player(String id) {
        this.id = id;
        points = 0;
        turn = false;
    }

    public void setTurn() {
        if (turn) {
            turn = false;
        } else {
            turn = true;
        }
    }
public void setName(String name) {
        this.name = name;
    }
    public String getName(String name) {
        return name;
    }
    public void setEnemyID(String enemyID) {
        this.enemyID = enemyID;
    }

    public void sumPoints(int points) {
        this.points += points;
    }

    public String getId() {
        return id;
    }

    public String getEnemyID() {
        return enemyID;
    }

    public boolean getTurn() {
        return turn;
    }

    public int getPoints() {
        return points;
    }
}