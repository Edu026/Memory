package com.project;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {

        // Iniciar app JavaFX   
        launch(args);
    }
    
    @Override
    public void start(Stage stage)  {

        final int windowWidth = 800;
        final int windowHeight = 600;
        try {
            UtilsViews.parentContainer.setStyle("-fx-font: 14 arial;");
            UtilsViews.addView(getClass(), "Game", "/assets/layout_partida.fxml");
            UtilsViews.addView(getClass(), "Game", "/assets/layoutDisconnected.fxml");
        } catch (Exception e) {e.printStackTrace();}

        Scene scene = new Scene(UtilsViews.parentContainer);
        
        stage.setScene(scene);
        //stage.onCloseRequestProperty(); // Call close method when closing window
        stage.setTitle("JavaFX");
        stage.setMinWidth(windowWidth);
        stage.setMinHeight(windowHeight);
        stage.show();

        // Add icon only if not Mac
        if (!System.getProperty("os.name").contains("Mac")) {
            Image icon = new Image("file:/icons/icon.png");
            stage.getIcons().add(icon);
        }
    }

    @Override
    public void stop() { 
        //AppData.getInstance().disconnectFromServer();
        System.exit(1); // Kill all executor services
    }
}