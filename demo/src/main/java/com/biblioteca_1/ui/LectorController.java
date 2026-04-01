package com.biblioteca_1.ui;

import com.biblioteca_1.model.Usuario;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class LectorController implements Initializable {

    @FXML private Button    logoutButton;
    @FXML private StackPane contentArea;
    @FXML private Label     welcomeLabel;
    @FXML private Label     pageTitle;
    @FXML private Button    btnCatalogo;
    @FXML private Button    btnMisPrestamos;

    private Usuario usuarioActual;

    @Override
    public void initialize(URL url, ResourceBundle rb) {}

    public void setUsuarioActual(Usuario usuario) {
        this.usuarioActual = usuario;
        if (welcomeLabel != null)
            welcomeLabel.setText(usuario.getNombre() + " " + usuario.getApellidos());
        try { handleVerLibros(); } catch (Exception ignored) {}
    }

    public Usuario getUsuarioActual() { return usuarioActual; }

    @FXML
    private void handleVerLibros() throws Exception {
        pageTitle.setText("Catalogo de Libros");
        resaltarBoton(btnCatalogo);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/libros.fxml"));
        Node node = loader.load();
        LibrosController lc = loader.getController();
        lc.setUsuarioActual(usuarioActual);
        contentArea.getChildren().setAll(node);
    }

    @FXML
    private void handleMisPrestamos() throws Exception {
        pageTitle.setText("Mis Prestamos");
        resaltarBoton(btnMisPrestamos);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/prestamos.fxml"));
        Node node = loader.load();
        PrestamosController pc = loader.getController();
        pc.setUsuarioActual(usuarioActual);
        contentArea.getChildren().setAll(node);
    }

    @FXML
    private void handleLogout() {
        // Fix doble clic: verificar stage activo
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        if (!stage.isShowing()) return;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/welcome.fxml"));
            Scene scene = new Scene(loader.load());
            aplicarCSS(scene);
            stage.setMaximized(false);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void resaltarBoton(Button activo) {
        for (Button b : new Button[]{btnCatalogo, btnMisPrestamos}) {
            b.getStyleClass().remove("nav-button-active");
            if (!b.getStyleClass().contains("nav-button"))
                b.getStyleClass().add("nav-button");
        }
        activo.getStyleClass().add("nav-button-active");
    }

    private void aplicarCSS(Scene scene) {
        var css = getClass().getResource("/css/style.css");
        if (css != null) scene.getStylesheets().add(css.toExternalForm());
    }
}