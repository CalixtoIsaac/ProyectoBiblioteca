package com.biblioteca_1.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;

public class WelcomeController {

    @FXML private Button btnLogin;

    @FXML
    private void goToLogin() throws Exception {
        cargarVista("/view/login.fxml");
    }

    @FXML
    private void goToRegister() throws Exception {
        cargarVista("/view/registro.fxml");
    }

    private void cargarVista(String ruta) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(ruta));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        aplicarCSS(scene);
        Stage stage = (Stage) btnLogin.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    private void aplicarCSS(Scene scene) {
        var cssUrl = getClass().getResource("/css/style.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }
    }
}