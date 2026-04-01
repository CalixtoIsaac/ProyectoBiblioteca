package com.biblioteca_1.ui;

import com.biblioteca_1.core.AppContext;
import com.biblioteca_1.model.usuarioAdministrador;
import com.biblioteca_1.model.usuarioLector;
import com.biblioteca_1.model.Usuario;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class RegistroController implements Initializable {

    private static final String CLAVE_ADMIN = "BIBLIOTECA123";

    @FXML private TextField        nombreField;
    @FXML private TextField        apellidosField;
    @FXML private DatePicker       fechaNacimientoPicker;
    @FXML private TextField        usernameField;
    @FXML private PasswordField    passwordField;
    @FXML private ComboBox<String> tipoUsuarioBox;
    @FXML private PasswordField    claveAdminField;
    @FXML private VBox             panelClaveAdmin;
    @FXML private Label            mensajeLabel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tipoUsuarioBox.setItems(FXCollections.observableArrayList("Lector", "Administrador"));
        tipoUsuarioBox.getSelectionModel().selectFirst();
        mensajeLabel.setText("");

        // Mostrar/ocultar el campo de clave admin según la selección
        tipoUsuarioBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean esAdmin = "Administrador".equals(newVal);
            panelClaveAdmin.setVisible(esAdmin);
            panelClaveAdmin.setManaged(esAdmin);
            if (!esAdmin) claveAdminField.clear();
        });
    }

    @FXML
    private void handleRegistrar() {
        mensajeLabel.setText("");

        String nombre    = nombreField.getText().trim();
        String apellidos = apellidosField.getText().trim();
        String username  = usernameField.getText().trim();
        String password  = passwordField.getText();
        LocalDate fecha  = fechaNacimientoPicker.getValue();
        String tipo      = tipoUsuarioBox.getValue();

        if (nombre.isEmpty() || apellidos.isEmpty() || username.isEmpty()
                || password.isEmpty() || fecha == null || tipo == null) {
            mostrarError("Por favor completa todos los campos.");
            return;
        }
        if (password.length() < 6) {
            mostrarError("La contrasena debe tener al menos 6 caracteres.");
            return;
        }

        // Verificar clave si es administrador
        if ("Administrador".equals(tipo)) {
            String claveIngresada = claveAdminField.getText();
            if (!CLAVE_ADMIN.equals(claveIngresada)) {
                mostrarError("Clave de administrador incorrecta.");
                claveAdminField.clear();
                return;
            }
        }

        try {
            Usuario usuario;
            if ("Administrador".equals(tipo)) {
                usuario = new usuarioAdministrador(nombre, apellidos, fecha, username, password);
            } else {
                usuario = new usuarioLector(nombre, apellidos, fecha, username, password);
            }

            AppContext.usuarioService.registrarUsuario(usuario);
            mostrarExito("Cuenta creada exitosamente. Ya puedes iniciar sesion.");

            nombreField.clear(); apellidosField.clear();
            usernameField.clear(); passwordField.clear();
            claveAdminField.clear();
            fechaNacimientoPicker.setValue(null);
            tipoUsuarioBox.getSelectionModel().selectFirst();

        } catch (Exception e) {
            mostrarError("Error al registrar: " + e.getMessage());
        }
    }

    @FXML
    private void handleVolver() throws Exception {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
        Scene scene = new Scene(loader.load());
        aplicarCSS(scene);
        stage.setScene(scene);
        stage.show();
    }

    private void mostrarError(String msg) {
        mensajeLabel.setStyle("-fx-text-fill: #8B1A1A; -fx-font-size: 12px; " +
                "-fx-background-color: #F8E8E8; -fx-padding: 6 10 6 10; " +
                "-fx-background-radius: 4px;");
        mensajeLabel.setText(msg);
    }

    private void mostrarExito(String msg) {
        mensajeLabel.setStyle("-fx-text-fill: #2E5E2E; -fx-font-size: 12px; " +
                "-fx-background-color: #E8F5E8; -fx-padding: 6 10 6 10; " +
                "-fx-background-radius: 4px;");
        mensajeLabel.setText(msg);
    }

    private void aplicarCSS(Scene scene) {
        var css = getClass().getResource("/css/style.css");
        if (css != null) scene.getStylesheets().add(css.toExternalForm());
    }
}