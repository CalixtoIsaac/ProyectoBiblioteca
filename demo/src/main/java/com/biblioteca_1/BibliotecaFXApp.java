package com.biblioteca_1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class BibliotecaFXApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/view/login.fxml")
        );

        Scene scene = new Scene(loader.load());
        if (getClass().getResource("/css/style.css") != null)
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

        stage.setTitle("Sistema Biblioteca");
        stage.setScene(scene);

        // Tamaño mínimo para que la app no se vea rota al redimensionar
        stage.setMinWidth(800);
        stage.setMinHeight(600);

        // Inicia maximizada como ventana normal (no pantalla completa)
        stage.setMaximized(true);

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
