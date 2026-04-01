package com.biblioteca_1.ui;

import com.biblioteca_1.core.AppContext;
import com.biblioteca_1.model.Libro;
import com.biblioteca_1.model.Prestamo;
import com.biblioteca_1.model.Usuario;
import com.biblioteca_1.model.usuarioAdministrador;

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
import java.util.List;
import java.util.ResourceBundle;

public class PrestamosController implements Initializable {

    @FXML private TableView<Prestamo>           tablaPrestamos;
    @FXML private TableColumn<Prestamo,Integer> colId;
    @FXML private TableColumn<Prestamo,Integer> colUsuario;
    @FXML private TableColumn<Prestamo,String>  colIsbn;
    @FXML private TableColumn<Prestamo,String>  colFechaPr;
    @FXML private TableColumn<Prestamo,String>  colFechaDev;
    @FXML private TableColumn<Prestamo,String>  colEstado;
    @FXML private TableColumn<Prestamo,Void>    colAccion;

    @FXML private VBox                  formPrestamoPanel;
    @FXML private ComboBox<UsuarioItem> pUsuarioBox;
    @FXML private ComboBox<LibroItem>   pLibroBox;
    @FXML private Label                 pMensaje;
    @FXML private Button                btnNuevoPrestamo;

    private final ObservableList<Prestamo> listaPrestamos = FXCollections.observableArrayList();
    private Usuario usuarioActual;

    record UsuarioItem(Usuario u) {
        @Override public String toString() {
            return u.getId() + " — " + u.getNombre() + " " + u.getApellidos() + " (" + u.getUsername() + ")";
        }
    }
    record LibroItem(Libro l) {
        @Override public String toString() {
            return l.getIsbn() + " — " + l.getTitulo() + " (disp: " + l.getEjemplaresDisponibles() + ")";
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("idUsuario"));
        colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbnLibro"));
        colFechaPr.setCellValueFactory(new PropertyValueFactory<>("fechaPrestamo"));
        colFechaDev.setCellValueFactory(new PropertyValueFactory<>("fechaDevolucion"));

        // Estado coloreado
        colEstado.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                    setText(null); setStyle(""); return;
                }
                boolean activo = getTableView().getItems().get(getIndex()).isActivo();
                setText(activo ? "Activo" : "Devuelto");
                setStyle(activo ? "-fx-text-fill:#4A6741;-fx-font-weight:bold;"
                                : "-fx-text-fill:#7A6652;");
            }
        });

        // Columna acción
        colAccion.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null); return;
                }
                Prestamo p = getTableView().getItems().get(getIndex());
                if (!p.isActivo()) { setGraphic(null); return; }

                // FIX: capturamos el id y el isbn en variables locales finales
                // para evitar que la referencia al objeto cambie tras el refresco
                final int    idPrestamo = p.getId();
                final String isbnLibro  = p.getIsbnLibro();

                Button btn = new Button("Devolver");
                btn.setStyle("-fx-background-color:#8B1A1A;-fx-text-fill:white;" +
                        "-fx-background-radius:4;-fx-cursor:hand;-fx-font-size:11px;-fx-padding:4 8;");
                btn.setOnAction(e -> handleDevolver(idPrestamo, isbnLibro));
                setGraphic(new HBox(btn));
            }
        });

        tablaPrestamos.setItems(listaPrestamos);
    }

    public void setUsuarioActual(Usuario usuario) {
        this.usuarioActual = usuario;
        if (btnNuevoPrestamo != null) {
            boolean esAdmin = (usuario == null || usuario instanceof usuarioAdministrador);
            btnNuevoPrestamo.setVisible(esAdmin);
            btnNuevoPrestamo.setManaged(esAdmin);
        }
        handleCargar();
    }

    @FXML
    public void handleCargar() {
        Task<List<Prestamo>> task = new Task<>() {
            @Override protected List<Prestamo> call() throws Exception {
                if (usuarioActual != null && !(usuarioActual instanceof usuarioAdministrador))
                    return AppContext.prestamoService.obtenerPorUsuario(usuarioActual.getId());
                return AppContext.prestamoService.obtenerTodos();
            }
        };
        // FIX: actualizar en FX thread y forzar refresco de la tabla
        task.setOnSucceeded(e -> Platform.runLater(() -> {
            listaPrestamos.setAll(task.getValue());
            tablaPrestamos.refresh(); // fuerza repintado de celdas
        }));
        task.setOnFailed(e -> Platform.runLater(() ->
            new Alert(Alert.AlertType.ERROR,
                "Error al cargar prestamos: " + task.getException().getMessage()).showAndWait()));
        new Thread(task, "carga-prestamos").start();
    }

    // ── Formulario nuevo préstamo ─────────────────────────────────────────────

    @FXML
    private void handleNuevoPrestamo() {
        formPrestamoPanel.setVisible(true);
        formPrestamoPanel.setManaged(true);
        pMensaje.setText("");
        cargarComboBoxes();
    }

    @FXML
    private void handleCancelarPrestamo() {
        formPrestamoPanel.setVisible(false);
        formPrestamoPanel.setManaged(false);
        pMensaje.setText("");
    }

    @FXML
    private void handleGuardarPrestamo() {
        pMensaje.setText("");
        UsuarioItem selUsuario = pUsuarioBox.getValue();
        LibroItem   selLibro   = pLibroBox.getValue();

        if (selUsuario == null || selLibro == null) {
            mostrarPError("Selecciona un usuario y un libro."); return;
        }
        if (selLibro.l().getEjemplaresDisponibles() <= 0) {
            mostrarPError("No hay ejemplares disponibles de ese libro."); return;
        }

        try {
            AppContext.prestamoService.realizarPrestamo(
                selUsuario.u().getId(), selLibro.l().getIsbn());
            mostrarPExito("Prestamo registrado correctamente.");
            handleCargar();
            pUsuarioBox.getSelectionModel().clearSelection();
            pLibroBox.getSelectionModel().clearSelection();
        } catch (Exception e) {
            mostrarPError("Error: " + e.getMessage());
        }
    }

    private void cargarComboBoxes() {
        new Thread(() -> {
            try {
                List<Usuario> usuarios = AppContext.usuarioService.obtenerTodos();
                List<Libro>   libros   = AppContext.libroService.obtenerTodos();
                Platform.runLater(() -> {
                    pUsuarioBox.setItems(FXCollections.observableArrayList(
                            usuarios.stream().map(UsuarioItem::new).toList()));
                    pLibroBox.setItems(FXCollections.observableArrayList(
                            libros.stream().map(LibroItem::new).toList()));
                });
            } catch (Exception ex) {
                Platform.runLater(() -> mostrarPError("Error cargando datos: " + ex.getMessage()));
            }
        }, "carga-combos").start();
    }

    // ── Devolver ─────────────────────────────────────────────────────────────

    // FIX: recibe id e isbn como parámetros en vez de objeto Prestamo
    // que podría haber cambiado de referencia tras un refresco previo
    private void handleDevolver(int idPrestamo, String isbnLibro) {
        try {
            AppContext.prestamoService.devolverPrestamo(idPrestamo, isbnLibro);
            // FIX: recargar ANTES de mostrar el alert para que la tabla
            // ya esté actualizada cuando el usuario cierre el mensaje
            handleCargar();
            Platform.runLater(() ->
                new Alert(Alert.AlertType.INFORMATION,
                    "Prestamo devuelto correctamente.", ButtonType.OK).showAndWait()
            );
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR,
                "Error al devolver: " + e.getMessage()).showAndWait();
        }
    }

    private void mostrarPError(String m) {
        pMensaje.setStyle("-fx-text-fill:#8B1A1A;-fx-font-size:12px;" +
                "-fx-background-color:#F8E8E8;-fx-padding:6 10;-fx-background-radius:4;");
        pMensaje.setText(m);
    }
    private void mostrarPExito(String m) {
        pMensaje.setStyle("-fx-text-fill:#2E5E2E;-fx-font-size:12px;" +
                "-fx-background-color:#E8F5E8;-fx-padding:6 10;-fx-background-radius:4;");
        pMensaje.setText(m);
    }
}