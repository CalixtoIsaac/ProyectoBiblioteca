package com.biblioteca_1.ui;

import com.biblioteca_1.core.AppContext;
import com.biblioteca_1.model.Usuario;
import com.biblioteca_1.model.usuarioAdministrador;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField     usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label         errorLabel;
    @FXML private Button        btnLogin;

    @FXML
    private void handleLogin() {
        String user = usernameField.getText().trim();
        String pass = passwordField.getText();

        if (user.isEmpty() || pass.isEmpty()) {
            mostrarError("Ingresa usuario y contrasena.");
            return;
        }

        if (btnLogin != null) btnLogin.setDisable(true);
        mostrarInfo("Conectando...");

        Task<Usuario> task = new Task<>() {
            @Override protected Usuario call() throws Exception {
                return AppContext.usuarioService.login(user, pass);
            }
        };

        task.setOnSucceeded(e -> Platform.runLater(() -> {
            try {
                Stage stage = (Stage) usernameField.getScene().getWindow();
                String ruta = (task.getValue() instanceof usuarioAdministrador)
                        ? "/view/admin.fxml" : "/view/lector.fxml";
                cargarVista(stage, ruta, task.getValue());
            } catch (Exception ex) {
                mostrarError("Error al abrir vista: " + ex.getMessage());
                if (btnLogin != null) btnLogin.setDisable(false);
            }
        }));

        task.setOnFailed(e -> Platform.runLater(() -> {
            Throwable ex = task.getException();
            // Mostrar mensaje COMPLETO para poder diagnosticar
            String msg = ex.getMessage() != null ? ex.getMessage() : ex.getClass().getName();
            if (msg.contains("Access denied")) {
                mostrarError("Contrasena de MySQL incorrecta.\nRevisa DatabaseManager.java");
            } else if (msg.contains("Unknown database")) {
                mostrarError("La base de datos 'biblioteca_db' no existe.\nEjecuta el schema.sql primero.");
            } else if (msg.contains("Connection refused") || msg.contains("Communications")) {
                mostrarError("MySQL no responde en localhost:3306.\nVerifica que el servicio este corriendo.");
            } else {
                // Mostrar error completo para diagnostico
                mostrarError(msg);
            }
            if (btnLogin != null) btnLogin.setDisable(false);
        }));

        new Thread(task, "login-task").start();
    }

    @FXML
    private void handleRegister() throws Exception {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/registro.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        aplicarCSS(scene);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void handleBack() throws Exception {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/welcome.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        aplicarCSS(scene);
        stage.setScene(scene);
        stage.show();
    }

    private void cargarVista(Stage stage, String ruta, Usuario usuario) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(ruta));
        Parent root = loader.load();
        Object controller = loader.getController();
        if (controller instanceof AdminController)
            ((AdminController) controller).setUsuarioActual(usuario);
        else if (controller instanceof LectorController)
            ((LectorController) controller).setUsuarioActual(usuario);
        Scene scene = new Scene(root);
        aplicarCSS(scene);
        stage.setScene(scene);
        stage.show();
    }

    private void mostrarError(String msg) {
        errorLabel.setStyle("-fx-text-fill:#8B1A1A;-fx-font-size:11px;");
        errorLabel.setText(msg);
    }
    private void mostrarInfo(String msg) {
        errorLabel.setStyle("-fx-text-fill:#7A6652;-fx-font-size:11px;");
        errorLabel.setText(msg);
    }
    private void aplicarCSS(Scene scene) {
        var css = getClass().getResource("/css/style.css");
        if (css != null) scene.getStylesheets().add(css.toExternalForm());
    }
}