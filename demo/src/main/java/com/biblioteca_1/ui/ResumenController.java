package com.biblioteca_1.ui;

import com.biblioteca_1.core.AppContext;
import com.biblioteca_1.model.Libro;
import com.biblioteca_1.model.Prestamo;
import com.biblioteca_1.model.Usuario;
import com.biblioteca_1.model.usuarioAdministrador;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ResumenController implements Initializable {

    @FXML private Label lblTotalLibros;
    @FXML private Label lblLibrosDisponibles;
    @FXML private Label lblTotalUsuarios;
    @FXML private Label lblTotalLectores;
    @FXML private Label lblTotalAdmins;
    @FXML private Label lblPrestamosActivos;
    @FXML private Label lblPrestamosHistorico;
    @FXML private Label lblEstado;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarEstadisticas();
    }

    private void cargarEstadisticas() {
        try {
            // Libros
            List<Libro> libros = AppContext.libroService.obtenerTodos();
            int totalLibros = libros.size();
            int disponibles = libros.stream().mapToInt(Libro::getEjemplaresDisponibles).sum();
            int totalEjemplares = libros.stream().mapToInt(Libro::getEjemplaresTotales).sum();

            lblTotalLibros.setText(String.valueOf(totalLibros));
            lblLibrosDisponibles.setText(disponibles + " / " + totalEjemplares + " ejemplares");

            // Usuarios
            List<Usuario> usuarios = AppContext.usuarioService.obtenerTodos();
            long admins  = usuarios.stream().filter(u -> u instanceof usuarioAdministrador).count();
            long lectores = usuarios.size() - admins;

            lblTotalUsuarios.setText(String.valueOf(usuarios.size()));
            lblTotalLectores.setText(String.valueOf(lectores));
            lblTotalAdmins.setText(String.valueOf(admins));

            // Préstamos
            List<Prestamo> prestamos = AppContext.prestamoService.obtenerTodos();
            long activos   = prestamos.stream().filter(Prestamo::isActivo).count();
            long historico = prestamos.size();

            lblPrestamosActivos.setText(String.valueOf(activos));
            lblPrestamosHistorico.setText(String.valueOf(historico));

            // Estado general
            if (disponibles == 0 && totalLibros > 0) {
                lblEstado.setText("Todos los libros estan prestados.");
                lblEstado.setStyle("-fx-text-fill: #8B1A1A;");
            } else if (activos > 5) {
                lblEstado.setText("Alta actividad de prestamos.");
                lblEstado.setStyle("-fx-text-fill: #B8860B;");
            } else {
                lblEstado.setText("Sistema operando con normalidad.");
                lblEstado.setStyle("-fx-text-fill: #4A6741;");
            }

        } catch (Exception e) {
            lblEstado.setText("Error al cargar estadisticas: " + e.getMessage());
        }
    }
}