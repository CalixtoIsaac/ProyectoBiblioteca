package com.biblioteca_1.service;

import com.biblioteca_1.config.DatabaseManager;
import com.biblioteca_1.dao.LibroDAO;
import com.biblioteca_1.dao.PrestamoDAO;
import com.biblioteca_1.model.Prestamo;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class PrestamoService {

    private static final int MAX_PRESTAMOS = 2;

    public void realizarPrestamo(int idUsuario, String isbn) throws SQLException {

        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false);

            PrestamoDAO prestamoDAO = new PrestamoDAO(conn);
            LibroDAO    libroDAO    = new LibroDAO(conn);

            // REGLA 1: LIMITE DE PRESTAMOS ACTIVOS
            int prestamosActivos = prestamoDAO.contarPrestamosActivos(idUsuario);
            if (prestamosActivos >= MAX_PRESTAMOS) {
                throw new RuntimeException(
                    "Limite alcanzado: ya tienes " + MAX_PRESTAMOS + " prestamos activos.");
            }

            // REGLA 2: NO PEDIR EL MISMO LIBRO DOS VECES
            if (prestamoDAO.usuarioTienePrestamoActivo(idUsuario, isbn)) {
                throw new RuntimeException("Ya tienes ese libro en prestamo activo.");
            }

            // REGLA 3: VERIFICAR STOCK
            // (libroDAO.actualizarStock ya descuenta, pero verificamos antes)

            // REGISTRAR PRESTAMO
            LocalDate hoy       = LocalDate.now();
            LocalDate devolucion = hoy.plusWeeks(2);

            prestamoDAO.registrar(idUsuario, isbn, hoy, devolucion);
            libroDAO.actualizarStock(isbn, -1);

            conn.commit();
        }
    }

    public void devolverPrestamo(int idPrestamo, String isbn) throws SQLException {

        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false);

            PrestamoDAO prestamoDAO = new PrestamoDAO(conn);
            LibroDAO    libroDAO    = new LibroDAO(conn);

            prestamoDAO.marcarComoDevuelto(idPrestamo);
            libroDAO.actualizarStock(isbn, 1);

            conn.commit();
        }
    }

    public boolean usuarioTienePrestamoActivo(int idUsuario, String isbn) throws SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            PrestamoDAO prestamoDAO = new PrestamoDAO(conn);
            return prestamoDAO.usuarioTienePrestamoActivo(idUsuario, isbn);
        }
    }

    public List<Prestamo> obtenerPorUsuario(int idUsuario) throws SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            PrestamoDAO prestamoDAO = new PrestamoDAO(conn);
            return prestamoDAO.obtenerPorUsuario(idUsuario);
        }
    }

    public List<Prestamo> obtenerTodos() throws SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            PrestamoDAO prestamoDAO = new PrestamoDAO(conn);
            return prestamoDAO.obtenerTodos();
        }
    }
}