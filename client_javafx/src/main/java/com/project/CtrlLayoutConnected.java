package com.project;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import org.json.JSONObject;

import com.project.AppData.ConnectionStatus;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class CtrlLayoutConnected {

    @FXML
    private Label nameLabel1, nameLabel2;

    @FXML
    private Label pointsLabel1, pointsLabel2;

    @FXML
    private ImageView img1, img2, img3, img4, img5, img6, img7, img8, img9, img10, img11, img12, img13, img14, img15, img16;

    private ArrayList<ImageView> imgs= new ArrayList<ImageView>();
    private ImageView firstClickedImageView = null;
    private AppData appData = AppData.getInstance();
    List<List<Integer>> rowsList = appData.getBoard();
     //private JSONObject board = appData.getNewBoard();

    @FXML
    private void initialize() {
        pointsLabel1.setText("0");
        pointsLabel2.setText("0");
        imgs.add(img1); imgs.add(img2); imgs.add(img3); imgs.add(img4); imgs.add(img5); imgs.add(img6); imgs.add(img7); imgs.add(img8);
        imgs.add(img9); imgs.add(img10); imgs.add(img11); imgs.add(img12); imgs.add(img13); imgs.add(img14); imgs.add(img15); imgs.add(img16);
        
       
        for (ImageView img : imgs) {
            img.setImage(new Image(getClass().getResource("/assets/icon.png").toString()));
            img.setOnMouseClicked(this::handleImageViewClick);
        }
        loadImages(rowsList);
    }

    
    private void loadImages(List<List<Integer>> rowsList) {
        for (int i = 0; i < imgs.size(); i++) {
            int row = i / rowsList.get(0).size(); 
            int col = i % rowsList.get(0).size();  
            int imageNumber = rowsList.get(row).get(col);  
            String imageName = "image" + imageNumber + ".png"; 
            imgs.get(i).setImage(new Image(getClass().getResource("/assets/" + imageName).toString())); 
            imgs.get(i).setId(imageName);  // 
            imgs.get(i).setOnMouseClicked(this::handleImageViewClick);  
        }
    }

    // Manegem l'event de l'ImageView
    private void handleImageViewClick(MouseEvent event) {
        ImageView clickedImageView = (ImageView) event.getSource();
        int position = imgs.indexOf(clickedImageView);

        // Obtener la fila y columna de la posición
        int row = position / rowsList.get(0).size();
        int col = position % rowsList.get(0).size();
        JSONObject objResponse = new JSONObject("{}");
        objResponse.put("type", "cards_flip");
         objResponse.put("row", row);
          objResponse.put("col", col);
          appData.send(objResponse.toString());

        if (firstClickedImageView == null) {
            // Primer clic, simplemente almacena la imagen clicada
            firstClickedImageView = clickedImageView;
        } else {
            // Segundo clic, verifica si las imágenes coinciden
            if (doImagesMatch(firstClickedImageView, clickedImageView)) {
                // Las imágenes coinciden, realiza las acciones necesarias
                handleMatchedImages(firstClickedImageView, clickedImageView);
            } else {
                // Las imágenes no coinciden, realiza las acciones necesarias
                handleMismatchedImages(firstClickedImageView, clickedImageView);
            }

            // Restablece la variable para el siguiente par de clics
            firstClickedImageView = null;
        }
    }

    private boolean doImagesMatch(ImageView img1, ImageView img2) {
        // Comparar las imágenes por sus identificadores o cualquier otra lógica que prefieras
        return img1.getId().equals(img2.getId());
    }

    private void handleMatchedImages(ImageView img1, ImageView img2) {
        // Las imágenes coinciden, realiza las acciones necesarias
        // Puedes incrementar los puntos, ocultar las imágenes, etc.
        System.out.println("¡Imágenes coinciden!");
    }

    private void handleMismatchedImages(ImageView img1, ImageView img2) {
        // Las imágenes no coinciden, realiza las acciones necesarias
        // Puedes ocultar las imágenes, restablecer el juego, etc.
        System.out.println("¡Imágenes no coinciden!");
     }
    
 
    @FXML
    private void handleDisconnect(ActionEvent event) {
        AppData appData = AppData.getInstance();
        appData.disconnectFromServer();
    }
  
}
