package com.biblioteca_1.ui;

import com.biblioteca_1.core.AppContext;
import com.biblioteca_1.model.Usuario;
import com.biblioteca_1.model.usuarioAdministrador;
import com.biblioteca_1.service.UsuarioService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class BibliotecaController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    // Usamos el servicio correcto y lo obtenemos de AppContext
    private UsuarioService servicio = AppContext.usuarioService;

    @FXML
    private void handleLogin() {
        try {
            Usuario usuario = servicio.login(
                    usernameField.getText(),
                    passwordField.getText()
            );

            if (usuario == null) {
                errorLabel.setText("Credenciales incorrectas");
                return;
            }

            Stage stage = (Stage) usernameField.getScene().getWindow();
            String fxmlPath;

            // En lugar de getRol(), usamos instanceof o el método esAdministrador() que ya tienes
            if (usuario instanceof usuarioAdministrador || usuario.esAdministrador()) {
                fxmlPath = "/view/admin.fxml";
            } else {
                fxmlPath = "/view/lector.fxml";
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(loader.load());
            
            // Ajuste de ruta de CSS según estructura
            scene.getStylesheets().add(
                    getClass().getResource("/css/style.css").toExternalForm()
            );

            stage.setScene(scene);

        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
        }
    }
}