package com.biblioteca_1.ui;

//import com.biblioteca_1.core.AppContext;
import com.biblioteca_1.model.Usuario;
//import com.biblioteca_1.model.usuarioAdministrador;

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

public class AdminController implements Initializable {

    @FXML private Button    logoutButton;
    @FXML private StackPane contentArea;
    @FXML private Label     welcomeLabel;
    @FXML private Label     pageTitle;
    @FXML private Button    btnResumen;
    @FXML private Button    btnLibros;
    @FXML private Button    btnUsuarios;
    @FXML private Button    btnPrestamos;

    private Usuario usuarioActual;

    @Override
    public void initialize(URL url, ResourceBundle rb) {}

    public void setUsuarioActual(Usuario usuario) {
        this.usuarioActual = usuario;
        if (welcomeLabel != null)
            welcomeLabel.setText(usuario.getNombre() + " " + usuario.getApellidos());
        try { goResumen(); } catch (Exception ignored) {}
    }

    @FXML
    private void goResumen() throws Exception {
        pageTitle.setText("Resumen General");
        resaltarBoton(btnResumen);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/resumen.fxml"));
        Node node = loader.load();
        contentArea.getChildren().setAll(node);
    }

    @FXML
    private void goLibros() throws Exception {
        pageTitle.setText("Gestion de Libros");
        resaltarBoton(btnLibros);
        loadView("libros.fxml");
    }

    @FXML
    private void goUsuarios() throws Exception {
        pageTitle.setText("Gestion de Usuarios");
        resaltarBoton(btnUsuarios);
        // Pasar usuarioActual al UsuariosController para proteger auto-eliminacion
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/usuarios.fxml"));
        Node node = loader.load();
        Object controller = loader.getController();
        if (controller instanceof UsuariosController)
            ((UsuariosController) controller).setUsuarioActual(usuarioActual);
        contentArea.getChildren().setAll(node);
    }

    @FXML
    private void goPrestamos() throws Exception {
        pageTitle.setText("Gestion de Prestamos");
        resaltarBoton(btnPrestamos);
        // Pasar usuarioActual al PrestamosController
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/prestamos.fxml"));
        Node node = loader.load();
        Object controller = loader.getController();
        if (controller instanceof PrestamosController)
            ((PrestamosController) controller).setUsuarioActual(usuarioActual);
        contentArea.getChildren().setAll(node);
    }

    @FXML
    private void handleLogout() {
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

    private void loadView(String view) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/" + view));
        Node node = loader.load();
        contentArea.getChildren().setAll(node);
    }

    private void resaltarBoton(Button activo) {
        for (Button b : new Button[]{btnResumen, btnLibros, btnUsuarios, btnPrestamos}) {
            if (b == null) continue;
            b.getStyleClass().remove("nav-button-active");
            if (!b.getStyleClass().contains("nav-button"))
                b.getStyleClass().add("nav-button");
        }
        if (activo != null) activo.getStyleClass().add("nav-button-active");
    }

    private void aplicarCSS(Scene scene) {
        var css = getClass().getResource("/css/style.css");
        if (css != null) scene.getStylesheets().add(css.toExternalForm());
    }
}