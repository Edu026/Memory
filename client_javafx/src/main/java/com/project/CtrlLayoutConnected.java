package com.project;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import org.java_websocket.WebSocket;
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
    List<List<Integer>> board= appData.Board() ;
     //private JSONObject board = appData.getNewBoard();
     private List<String> matchedImageNames = new ArrayList<>();

    @FXML
    private void initialize() {
        pointsLabel1.setText("0");
        pointsLabel2.setText("0");
        imgs.add(img1); imgs.add(img2); imgs.add(img3); imgs.add(img4); imgs.add(img5); imgs.add(img6); imgs.add(img7); imgs.add(img8);
        imgs.add(img9); imgs.add(img10); imgs.add(img11); imgs.add(img12); imgs.add(img13); imgs.add(img14); imgs.add(img15); imgs.add(img16);
        
        double imgWidth = 100.0;
        double imgHeight = 100.0;
        for (ImageView img : imgs) {
            img.setFitWidth(imgWidth);
            img.setFitHeight(imgHeight);
            
        }

        
       if (board != null) {
        loadImages(board);
    }
          
    
       
    }
     // Manegem l'event de l'ImageView
    private void handleImageViewClick(MouseEvent event) {
        ImageView clickedImageView = (ImageView) event.getSource();
        int position = imgs.indexOf(clickedImageView);

        // Obtener la fila y columna de la posición
        int row = position / board.get(0).size();
        int col = position % board.get(0).size();
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
        showImageAtPosition(row, col);
    }

    
    private void loadImages(List<List<Integer>> rowsList) {
        for (int i = 0; i < imgs.size(); i++) {
            int row = i / rowsList.get(0).size(); 
            int col = i % rowsList.get(0).size();  
    
            // Establecer todas las imágenes con el ícono de prueba
            imgs.get(i).setImage(new Image(getClass().getResource("/assets/icon.png").toString())); 
            imgs.get(i).setId("icon");
    
            // Configurar evento de clic solo si es el turno del jugador
            
                imgs.get(i).setOnMouseClicked(this::handleImageViewClick);  
        
        
            if (appData.myPoints==8 ||appData.rivalPoints==8  ) {
                 JSONObject objResponse = new JSONObject("{}");
                objResponse.put("type", "end_game");
               objResponse.put("sender_points", appData.myPoints);
                objResponse.put("rival_points", appData.rivalPoints);
                objResponse.put("destination", appData.rival_id);
                appData.send(objResponse.toString());
            } 
            
        }
    }

   

    public void showImageAtPosition(int row, int col) {
        int position = row * board.get(0).size() + col;
        ImageView targetImageView = imgs.get(position);
        String imageName = "image" + board.get(row).get(col) + ".png";
        targetImageView.setImage(new Image(getClass().getResource("/assets/" + imageName).toString()));
        targetImageView.setFitWidth(100.0);
        targetImageView.setFitHeight(100.0);
    }

    private boolean doImagesMatch(ImageView img1, ImageView img2) {
        String imageName1 = img1.getImage().getUrl();
        String imageName2 = img2.getImage().getUrl();
    
        // Extraer el nombre del archivo de la URL
        imageName1 = imageName1.substring(imageName1.lastIndexOf("/") + 1);
        imageName2 = imageName2.substring(imageName2.lastIndexOf("/") + 1);
    
        return imageName1.equals(imageName2);
    }

    private void handleMatchedImages(ImageView img1, ImageView img2) {
        // Obtén los nombres de las imágenes
        String imageName1 = getImageName(img1);
        String imageName2 = getImageName(img2);
    
        // Verifica si ambas imágenes ya han coincidido
        if (!matchedImageNames.contains(imageName1) && !matchedImageNames.contains(imageName2)) {
            // Realiza las acciones necesarias ya que es un nuevo par coincidente
            appData.myPoints++;
            pointsLabel1.setText(Integer.toString(appData.myPoints));
            System.out.println("¡Imágenes coinciden!");
    
            // Agrega los nombres de las imágenes a la lista de coincidencias
            matchedImageNames.add(imageName1);
            matchedImageNames.add(imageName2);
        }
    }

    private void handleMismatchedImages(ImageView img1, ImageView img2) {
        resetImageToIcon(img1);
        resetImageToIcon(img2);
         JSONObject objResponse = new JSONObject();
        objResponse.put("type", "canvi_torn");
        objResponse.put("points", appData.myPoints);
        objResponse.put("destination", appData.rival_id);
        appData.send(objResponse.toString());
        
        System.out.println("¡Imágenes no coinciden!");
       
     }
    
 
    @FXML
    private void handleDisconnect(ActionEvent event) {
        AppData appData = AppData.getInstance();
        appData.disconnectFromServer();
    }
    private void resetImageToIcon(ImageView img) {
        img.setImage(new Image(getClass().getResource("/assets/icon.png").toString()));
        // Asegúrate de aplicar las configuraciones de tamaño nuevamente
        img.setFitWidth(100.0);
        img.setFitHeight(100.0);
        // Establece el manejador de clic nuevamente
        img.setOnMouseClicked(this::handleImageViewClick);
    }
  // Método para obtener el nombre de la imagen de una ImageView
private String getImageName(ImageView imageView) {
    String imageUrl = imageView.getImage().getUrl();
    return imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
}
}
