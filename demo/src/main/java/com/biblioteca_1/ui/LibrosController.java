package com.biblioteca_1.ui;

import com.biblioteca_1.core.AppContext;
import com.biblioteca_1.model.Libro;
import com.biblioteca_1.model.Usuario;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class LibrosController implements Initializable {

    @FXML private TextField   searchField;
    @FXML private Button      btnNuevoLibro;

    @FXML private TableView<Libro>           tablaLibros;
    @FXML private TableColumn<Libro,Void>    colPortada;
    @FXML private TableColumn<Libro,String>  colIsbn;
    @FXML private TableColumn<Libro,String>  colTitulo;
    @FXML private TableColumn<Libro,String>  colAutor;
    @FXML private TableColumn<Libro,Integer> colStock;
    @FXML private TableColumn<Libro,Integer> colTotal;
    @FXML private TableColumn<Libro,Void>    colAccion;

    @FXML private ImageView imgPortada;
    @FXML private Label     lblDetalleTitulo;
    @FXML private Label     lblDetalleAutor;
    @FXML private Label     lblDetalleSinopsis;

    @FXML private VBox      formPanel;
    @FXML private TextField formIsbn;
    @FXML private TextField formTitulo;
    @FXML private TextField formAutor;
    @FXML private TextField formGenero;
    @FXML private TextField formEjemplares;
    @FXML private TextArea  formSinopsis;
    @FXML private TextField formRutaImagen;
    @FXML private TextField formRutaPdf;
    @FXML private ImageView previewPortada;
    @FXML private Label     lblPdfSeleccionado;
    @FXML private Label     formMensaje;

    private Usuario usuarioActual;
    private final ObservableList<Libro> listaLibros = FXCollections.observableArrayList();

    // ── Caché de imágenes: evita recargar la misma imagen miles de veces ──
    private static final Map<String, Image> CACHE_IMAGENES = new HashMap<>();
    private static Image PLACEHOLDER_IMG = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        configurarDragAndDrop();
        configurarSeleccionTabla();
        cargarLibrosAsync(); // Carga asíncrona para no bloquear la UI
    }

    public void setUsuarioActual(Usuario usuario) {
        this.usuarioActual = usuario;
        if (btnNuevoLibro != null && usuarioActual != null) {
            btnNuevoLibro.setVisible(false);
            btnNuevoLibro.setManaged(false);
        }
        configurarTabla();
        cargarLibrosAsync();
    }

    // ── CARGA ASÍNCRONA (mejora rendimiento) ────────────────────────────────

    private void cargarLibrosAsync() {
        Task<List<Libro>> task = new Task<>() {
            @Override protected List<Libro> call() throws Exception {
                return AppContext.libroService.obtenerTodos();
            }
        };
        task.setOnSucceeded(e -> listaLibros.setAll(task.getValue()));
        task.setOnFailed(e -> mostrarAlerta("Error al cargar libros: " + task.getException().getMessage()));
        new Thread(task, "carga-libros").start();
    }

    // ── TABLA ────────────────────────────────────────────────────────────────

    private void configurarTabla() {
        // Portada con caché
        colPortada.setCellFactory(col -> new TableCell<>() {
            private final ImageView iv = new ImageView();
            { iv.setFitWidth(36); iv.setFitHeight(48); iv.setPreserveRatio(true); }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null); return;
                }
                iv.setImage(obtenerImagenCacheada(getTableView().getItems().get(getIndex())));
                setGraphic(iv);
            }
        });

        colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("ejemplaresDisponibles"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("ejemplaresTotales"));

        colAccion.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null); return;
                }
                Libro libro = getTableView().getItems().get(getIndex());
                HBox box = new HBox(5);

                Button btnLeer = botonTabla("Leer", "#2C1810");
                btnLeer.setOnAction(e -> handleLeerLibro(libro));
                box.getChildren().add(btnLeer);

                if (usuarioActual != null) {
                    Button btnPedir = botonTabla("Pedir", "#4A6741");
                    btnPedir.setDisable(libro.getEjemplaresDisponibles() <= 0);
                    btnPedir.setOnAction(e -> handlePedirPrestamo(libro));
                    box.getChildren().add(btnPedir);
                } else {
                    Button btnStock = botonTabla("Stock", "#5C3317");
                    btnStock.setOnAction(e -> handleActualizarStock(libro));
                    box.getChildren().add(btnStock);
                }

                setGraphic(box);
            }
        });

        tablaLibros.setItems(listaLibros);
    }

    private Button botonTabla(String txt, String color) {
        Button b = new Button(txt);
        b.setStyle("-fx-background-color:" + color + ";-fx-text-fill:white;-fx-background-radius:4;" +
                   "-fx-cursor:hand;-fx-font-size:11px;-fx-padding:4 10 4 10;");
        return b;
    }

    private void configurarSeleccionTabla() {
        tablaLibros.getSelectionModel().selectedItemProperty().addListener((obs, old, libro) -> {
            if (libro == null) return;
            imgPortada.setImage(obtenerImagenCacheada(libro));
            lblDetalleTitulo.setText(libro.getTitulo());
            lblDetalleAutor.setText("— " + libro.getAutor());
            lblDetalleSinopsis.setText(libro.getSinopsis() != null && !libro.getSinopsis().isBlank()
                    ? libro.getSinopsis() : "Sin sinopsis disponible.");
        });
    }

    // ── CACHÉ DE IMÁGENES (clave para el rendimiento) ───────────────────────

    private Image obtenerImagenCacheada(Libro libro) {
        String ruta = libro.getRutaContenido();
        String rutaImg = null;

        if (ruta != null && ruta.startsWith("imagen:")) {
            int sep = ruta.indexOf("|pdf:");
            rutaImg = sep > 0 ? ruta.substring(7, sep) : ruta.substring(7);
        }

        if (rutaImg != null && !rutaImg.isBlank()) {
            // Buscar en caché primero
            if (CACHE_IMAGENES.containsKey(rutaImg)) return CACHE_IMAGENES.get(rutaImg);
            File f = new File(rutaImg);
            if (f.exists()) {
                try {
                    Image img = new Image(f.toURI().toString(), 60, 80, true, false); // smooth=false = más rápido
                    CACHE_IMAGENES.put(rutaImg, img);
                    return img;
                } catch (Exception ignored) {}
            }
        }
        return getPlaceholder();
    }

    private static Image getPlaceholder() {
        if (PLACEHOLDER_IMG == null) {
            WritableImage wi = new WritableImage(60, 80);
            var pw = wi.getPixelWriter();
            Color fondo  = Color.web("#D0C0A8");
            Color border = Color.web("#B8A890");
            for (int x = 0; x < 60; x++)
                for (int y = 0; y < 80; y++)
                    pw.setColor(x, y, (x == 0 || x == 59 || y == 0 || y == 79) ? border : fondo);
            PLACEHOLDER_IMG = wi;
        }
        return PLACEHOLDER_IMG;
    }

    // ── DRAG AND DROP ────────────────────────────────────────────────────────

    private void configurarDragAndDrop() {
        Platform.runLater(() -> {
            if (formRutaImagen != null) configurarDrop(formRutaImagen, true);
            if (formRutaPdf   != null) configurarDrop(formRutaPdf,    false);
        });
    }

    private void configurarDrop(TextField campo, boolean esImagen) {
        campo.setOnDragOver(e -> { if (e.getDragboard().hasFiles()) e.acceptTransferModes(TransferMode.COPY); e.consume(); });
        campo.setOnDragDropped(e -> {
            var db = e.getDragboard();
            if (db.hasFiles() && !db.getFiles().isEmpty()) {
                File f = db.getFiles().get(0);
                campo.setText(f.getAbsolutePath());
                if (esImagen) actualizarPreview(f.getAbsolutePath());
                else lblPdfSeleccionado.setText("Seleccionado: " + f.getName());
            }
            e.setDropCompleted(true); e.consume();
        });
    }

    private void actualizarPreview(String ruta) {
        File f = new File(ruta);
        if (f.exists()) {
            try { previewPortada.setImage(new Image(f.toURI().toString(), 60, 80, true, false)); }
            catch (Exception ignored) {}
        }
    }

    // ── FORMULARIO ───────────────────────────────────────────────────────────

    @FXML private void handleNuevoLibro()   { formPanel.setVisible(true);  formPanel.setManaged(true);  formMensaje.setText(""); }
    @FXML private void handleCancelarForm() { formPanel.setVisible(false); formPanel.setManaged(false); limpiarFormulario(); }

    @FXML
    private void handleBuscarImagen() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Seleccionar imagen de portada");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imagenes", "*.png","*.jpg","*.jpeg","*.gif","*.bmp","*.webp"));
        File f = fc.showOpenDialog(formRutaImagen.getScene().getWindow());
        if (f != null) { formRutaImagen.setText(f.getAbsolutePath()); actualizarPreview(f.getAbsolutePath()); }
    }

    @FXML
    private void handleBuscarPdf() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Seleccionar archivo del libro");
        fc.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Documentos", "*.pdf","*.txt"),
            new FileChooser.ExtensionFilter("PDF", "*.pdf"),
            new FileChooser.ExtensionFilter("Texto", "*.txt")
        );
        File f = fc.showOpenDialog(formRutaPdf.getScene().getWindow());
        if (f != null) { formRutaPdf.setText(f.getAbsolutePath()); lblPdfSeleccionado.setText("Seleccionado: " + f.getName()); }
    }

    @FXML
    private void handleGuardarLibro() {
        formMensaje.setText("");
        String isbn     = formIsbn.getText().trim();
        String titulo   = formTitulo.getText().trim();
        String autor    = formAutor.getText().trim();
        String sinopsis = formSinopsis.getText().trim();
        String ejStr    = formEjemplares.getText().trim();
        String rutaImg  = formRutaImagen.getText().trim();
        String rutaPdf  = formRutaPdf.getText().trim();

        if (isbn.isEmpty() || titulo.isEmpty() || autor.isEmpty() || ejStr.isEmpty()) {
            mostrarFormError("Completa ISBN, Titulo, Autor y Ejemplares."); return;
        }
        int ejemplares;
        try { ejemplares = Integer.parseInt(ejStr); if (ejemplares < 1) throw new NumberFormatException(); }
        catch (NumberFormatException ex) { mostrarFormError("Ejemplares: numero entero positivo."); return; }

        String rutaContenido = "";
        if (!rutaImg.isEmpty() && !rutaPdf.isEmpty())
            rutaContenido = "imagen:" + rutaImg + "|pdf:" + rutaPdf;
        else if (!rutaImg.isEmpty())
            rutaContenido = "imagen:" + rutaImg;
        else if (!rutaPdf.isEmpty())
            rutaContenido = rutaPdf;

        try {
            Libro libro = new Libro(isbn, titulo, autor, sinopsis.isEmpty() ? "Sin sinopsis" : sinopsis, ejemplares, rutaContenido);
            AppContext.libroService.registrarLibro(libro);
            mostrarFormExito("Libro registrado exitosamente.");
            CACHE_IMAGENES.clear(); // Limpiar caché al agregar nuevo libro
            cargarLibrosAsync();
            limpiarFormulario();
        } catch (Exception e) { mostrarFormError("Error al guardar: " + e.getMessage()); }
    }

    // ── LEER LIBRO ───────────────────────────────────────────────────────────

    private void handleLeerLibro(Libro libro) {
        String ruta = libro.getRutaContenido();
        if (ruta == null || ruta.isBlank()) { mostrarSinopsis(libro); return; }

        String rutaPdf = ruta;
        if (ruta.contains("|pdf:"))     rutaPdf = ruta.substring(ruta.indexOf("|pdf:") + 5);
        if (ruta.startsWith("imagen:") && !ruta.contains("|pdf:")) { mostrarSinopsis(libro); return; }

        File archivo = new File(rutaPdf);
        if (!archivo.exists()) { mostrarSinopsis(libro); return; }

        if (rutaPdf.toLowerCase().endsWith(".pdf")) abrirPdfExterno(archivo);
        else abrirVisorTexto(libro, archivo);
    }

    private void abrirPdfExterno(File pdf) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN))
                Desktop.getDesktop().open(pdf);
            else mostrarAlerta("No se puede abrir automaticamente.\nRuta: " + pdf.getAbsolutePath());
        } catch (IOException e) { mostrarAlerta("Error al abrir PDF: " + e.getMessage()); }
    }

    private void abrirVisorTexto(Libro libro, File archivo) {
        try {
            String contenido = Files.readString(archivo.toPath());
            Stage ventana = new Stage();
            ventana.setTitle("Leyendo: " + libro.getTitulo());
            ventana.initModality(Modality.APPLICATION_MODAL);
            VBox root = new VBox(10);
            root.setPadding(new Insets(20));
            root.setStyle("-fx-background-color:#FEFCF7;");
            Label lbl = new Label(libro.getTitulo() + "  —  " + libro.getAutor());
            lbl.setStyle("-fx-font-family:'Georgia';-fx-font-size:16px;-fx-font-weight:bold;-fx-text-fill:#2C1810;");
            TextArea area = new TextArea(contenido);
            area.setEditable(false); area.setWrapText(true); area.setPrefHeight(500);
            area.setStyle("-fx-font-size:13px;-fx-font-family:'Georgia';");
            VBox.setVgrow(area, Priority.ALWAYS);
            Button cerrar = new Button("Cerrar");
            cerrar.setStyle("-fx-background-color:#2C1810;-fx-text-fill:white;-fx-padding:9 22;-fx-background-radius:6;-fx-cursor:hand;");
            cerrar.setOnAction(e -> ventana.close());
            root.getChildren().addAll(lbl, new Separator(), area, cerrar);
            ventana.setScene(new Scene(root, 700, 600));
            ventana.show();
        } catch (IOException e) { mostrarAlerta("Error al leer: " + e.getMessage()); }
    }

    private void mostrarSinopsis(Libro libro) {
        Stage ventana = new Stage();
        ventana.setTitle("Info del libro");
        ventana.initModality(Modality.APPLICATION_MODAL);
        VBox root = new VBox(14);
        root.setPadding(new Insets(28, 36, 28, 36));
        root.setStyle("-fx-background-color:#FEFCF7;");
        root.setPrefWidth(420);
        ImageView iv = new ImageView(obtenerImagenCacheada(libro));
        iv.setFitWidth(120); iv.setFitHeight(160); iv.setPreserveRatio(true);
        Label titulo = new Label(libro.getTitulo());
        titulo.setStyle("-fx-font-family:'Georgia';-fx-font-size:20px;-fx-font-weight:bold;-fx-text-fill:#2C1810;");
        titulo.setWrapText(true);
        Label autor = new Label("— " + libro.getAutor());
        autor.setStyle("-fx-font-size:13px;-fx-text-fill:#7A6652;-fx-font-style:italic;");
        Label sin = new Label(libro.getSinopsis() != null && !libro.getSinopsis().isBlank() ? libro.getSinopsis() : "Sin sinopsis.");
        sin.setWrapText(true); sin.setStyle("-fx-font-size:13px;-fx-text-fill:#2C1810;");
        Button cerrar = new Button("Cerrar");
        cerrar.setStyle("-fx-background-color:#2C1810;-fx-text-fill:white;-fx-padding:10 24;-fx-background-radius:6;-fx-cursor:hand;");
        cerrar.setOnAction(e -> ventana.close());
        root.getChildren().addAll(iv, titulo, autor, new Separator(), sin, cerrar);
        ventana.setScene(new Scene(root));
        ventana.showAndWait();
    }

    // ── BUSCAR / STOCK / PRESTAMO ────────────────────────────────────────────

    @FXML
    private void handleBuscar() {
        String t = searchField.getText().trim().toLowerCase();
        if (t.isEmpty()) { cargarLibrosAsync(); return; }
        Task<List<Libro>> task = new Task<>() {
            @Override protected List<Libro> call() throws Exception { return AppContext.libroService.obtenerTodos(); }
        };
        task.setOnSucceeded(e -> listaLibros.setAll(
            task.getValue().stream()
                .filter(l -> l.getTitulo().toLowerCase().contains(t)
                          || l.getAutor().toLowerCase().contains(t)
                          || l.getIsbn().toLowerCase().contains(t))
                .toList()
        ));
        new Thread(task, "buscar-libros").start();
    }

    private void handlePedirPrestamo(Libro libro) {
        if (usuarioActual == null) return;
        try {
            AppContext.prestamoService.realizarPrestamo(usuarioActual.getId(), libro.getIsbn());
            mostrarAlerta("Prestamo realizado. Devolucion en 2 semanas.");
            cargarLibrosAsync();
        } catch (Exception e) { mostrarAlerta("Error: " + e.getMessage()); }
    }

    private void handleActualizarStock(Libro libro) {
        TextInputDialog d = new TextInputDialog("1");
        d.setTitle("Actualizar Stock"); d.setHeaderText("Libro: " + libro.getTitulo());
        d.setContentText("Cantidad a agregar (negativo para reducir):");
        d.showAndWait().ifPresent(val -> {
            try { Integer.parseInt(val.trim()); mostrarAlerta("Funcionalidad via BD."); cargarLibrosAsync(); }
            catch (NumberFormatException ex) { mostrarAlerta("Valor invalido."); }
        });
    }

    private void limpiarFormulario() {
        formIsbn.clear(); formTitulo.clear(); formAutor.clear();
        formGenero.clear(); formEjemplares.clear(); formSinopsis.clear();
        formRutaImagen.clear(); formRutaPdf.clear();
        if (previewPortada != null) previewPortada.setImage(null);
        lblPdfSeleccionado.setText(""); formMensaje.setText("");
    }

    private void mostrarFormError(String m) { formMensaje.setStyle("-fx-text-fill:#8B1A1A;-fx-font-size:12px;"); formMensaje.setText(m); }
    private void mostrarFormExito(String m) { formMensaje.setStyle("-fx-text-fill:#2E5E2E;-fx-font-size:12px;"); formMensaje.setText(m); }
    private void mostrarAlerta(String m)    { Platform.runLater(() -> new Alert(Alert.AlertType.INFORMATION, m, ButtonType.OK).showAndWait()); }
}