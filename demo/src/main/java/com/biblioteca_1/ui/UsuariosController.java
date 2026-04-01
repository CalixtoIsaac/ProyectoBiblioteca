package com.biblioteca_1.ui;

import com.biblioteca_1.core.AppContext;
import com.biblioteca_1.model.Usuario;
import com.biblioteca_1.model.usuarioAdministrador;
import com.biblioteca_1.model.usuarioLector;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class UsuariosController implements Initializable {

    // Tabla
    @FXML private TableView<Usuario>           tablaUsuarios;
    @FXML private TableColumn<Usuario,Integer> colId;
    @FXML private TableColumn<Usuario,String>  colNombre;
    @FXML private TableColumn<Usuario,String>  colApellidos;
    @FXML private TableColumn<Usuario,String>  colUsername;
    @FXML private TableColumn<Usuario,String>  colTipo;
    @FXML private TableColumn<Usuario,Void>    colAccionU;
    @FXML private Label                        lblTotal;

    // Formulario nuevo usuario
    @FXML private VBox          formUsuarioPanel;
    @FXML private TextField     uNombre;
    @FXML private TextField     uApellidos;
    @FXML private TextField     uUsername;
    @FXML private DatePicker    uFecha;
    @FXML private PasswordField uPassword;
    @FXML private ComboBox<String> uTipo;
    @FXML private Label         uMensaje;

    private final ObservableList<Usuario> listaUsuarios = FXCollections.observableArrayList();
    private Usuario usuarioActual;

    public void setUsuarioActual(Usuario usuario) {
        this.usuarioActual = usuario;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Configurar tabla
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));

        colTipo.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                    setText(null); setStyle(""); return;
                }
                boolean esAdmin = getTableView().getItems().get(getIndex()) instanceof usuarioAdministrador;
                setText(esAdmin ? "Administrador" : "Lector");
                setStyle(esAdmin ? "-fx-text-fill:#B8860B;-fx-font-weight:bold;"
                                 : "-fx-text-fill:#4A6741;-fx-font-weight:bold;");
            }
        });

        // Columna acciones: botón eliminar
        colAccionU.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null); return;
                }
                Button btnEliminar = new Button("Eliminar");
                btnEliminar.setStyle("-fx-background-color:#8B1A1A;-fx-text-fill:white;-fx-background-radius:4;" +
                        "-fx-cursor:hand;-fx-font-size:11px;-fx-padding:4 8 4 8;");
                Usuario u = getTableView().getItems().get(getIndex());
                btnEliminar.setOnAction(e -> confirmarEliminar(u));
                setGraphic(new HBox(btnEliminar));
            }
        });

        tablaUsuarios.setItems(listaUsuarios);
        listaUsuarios.addListener((javafx.collections.ListChangeListener<Usuario>) c -> actualizarContador());

        // Configurar ComboBox tipo
        if (uTipo != null) uTipo.setItems(FXCollections.observableArrayList("Lector", "Administrador"));

        handleCargar();
    }

    @FXML
    public void handleCargar() {
        Task<List<Usuario>> task = new Task<>() {
            @Override protected List<Usuario> call() throws Exception {
                return AppContext.usuarioService.obtenerTodos();
            }
        };
        task.setOnSucceeded(e -> listaUsuarios.setAll(task.getValue()));
        task.setOnFailed(e -> Platform.runLater(() ->
            new Alert(Alert.AlertType.ERROR, "Error al cargar usuarios: " + task.getException().getMessage()).showAndWait()));
        new Thread(task, "carga-usuarios").start();
    }

    // ── Formulario nuevo usuario ─────────────────────────────────────────────

    @FXML
    private void handleNuevoUsuario() {
        formUsuarioPanel.setVisible(true);
        formUsuarioPanel.setManaged(true);
        uTipo.getSelectionModel().selectFirst();
        uMensaje.setText("");
    }

    @FXML
    private void handleCancelarUsuario() {
        formUsuarioPanel.setVisible(false);
        formUsuarioPanel.setManaged(false);
        limpiarFormUsuario();
    }

    @FXML
    private void handleGuardarUsuario() {
        uMensaje.setText("");
        String nombre    = uNombre.getText().trim();
        String apellidos = uApellidos.getText().trim();
        String username  = uUsername.getText().trim();
        String password  = uPassword.getText();
        LocalDate fecha  = uFecha.getValue();
        String tipo      = uTipo.getValue();

        if (nombre.isEmpty() || apellidos.isEmpty() || username.isEmpty() || password.isEmpty() || fecha == null) {
            mostrarUError("Completa todos los campos."); return;
        }
        if (password.length() < 6) {
            mostrarUError("La contrasena debe tener al menos 6 caracteres."); return;
        }

        try {
            Usuario usuario = "Administrador".equals(tipo)
                    ? new usuarioAdministrador(nombre, apellidos, fecha, username, password)
                    : new usuarioLector(nombre, apellidos, fecha, username, password);
            AppContext.usuarioService.registrarUsuario(usuario);
            mostrarUExito("Usuario creado exitosamente.");
            handleCargar();
            limpiarFormUsuario();
        } catch (Exception e) {
            mostrarUError("Error: " + e.getMessage());
        }
    }

    private void confirmarEliminar(Usuario u) {
        // No permitir eliminar al propio usuario logueado
        if (usuarioActual != null && usuarioActual.getId() == u.getId()) {
            new Alert(Alert.AlertType.WARNING,
                    "No puedes eliminar tu propia cuenta.").showAndWait();
            return;
        }
        Alert conf = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Eliminar al usuario '" + u.getUsername() + "'?\n" +
                "Sus prestamos activos seran marcados como devueltos.",
                ButtonType.YES, ButtonType.NO);
        conf.setHeaderText("Confirmar eliminacion");
        conf.showAndWait().ifPresent(r -> {
            if (r == ButtonType.YES) {
                try {
                    AppContext.usuarioService.eliminarUsuario(u.getId());
                    handleCargar();
                    new Alert(Alert.AlertType.INFORMATION,
                            "Usuario '" + u.getUsername() + "' eliminado correctamente.").showAndWait();
                } catch (Exception ex) {
                    new Alert(Alert.AlertType.ERROR,
                            "Error al eliminar: " + ex.getMessage()).showAndWait();
                }
            }
        });
    }

    // ── Util ─────────────────────────────────────────────────────────────────

    private void actualizarContador() {
        if (lblTotal == null) return;
        long admins  = listaUsuarios.stream().filter(u -> u instanceof usuarioAdministrador).count();
        long lectores = listaUsuarios.size() - admins;
        lblTotal.setText("Total: " + listaUsuarios.size()
                + "  |  Administradores: " + admins
                + "  |  Lectores: " + lectores);
    }

    private void limpiarFormUsuario() {
        uNombre.clear(); uApellidos.clear(); uUsername.clear();
        uPassword.clear(); uFecha.setValue(null);
        if (uTipo != null) uTipo.getSelectionModel().selectFirst();
        uMensaje.setText("");
    }

    private void mostrarUError(String m) {
        uMensaje.setStyle("-fx-text-fill:#8B1A1A;-fx-font-size:12px;-fx-background-color:#F8E8E8;" +
                "-fx-padding:6 10;-fx-background-radius:4;");
        uMensaje.setText(m);
    }
    private void mostrarUExito(String m) {
        uMensaje.setStyle("-fx-text-fill:#2E5E2E;-fx-font-size:12px;-fx-background-color:#E8F5E8;" +
                "-fx-padding:6 10;-fx-background-radius:4;");
        uMensaje.setText(m);
    }
}