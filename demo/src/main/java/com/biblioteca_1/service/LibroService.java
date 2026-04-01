package com.biblioteca_1.service;

import com.biblioteca_1.config.DatabaseManager;
import com.biblioteca_1.dao.LibroDAO;
import com.biblioteca_1.model.Libro;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class LibroService {

    public List<Libro> obtenerTodos() throws SQLException {
		try (Connection conn = DatabaseManager.getConnection()){
			LibroDAO libroDAO = new LibroDAO(conn);
			return libroDAO.obtenerTodos(); 
		}
	}

	public Libro buscarPorTitulo(String titulo) throws SQLException{
		try(Connection conn = DatabaseManager.getConnection()){
			LibroDAO libroDAO = new LibroDAO(conn);
			return libroDAO.buscarLibroPorTitulo(titulo);
		}
	}

	public void registrarLibro(Libro libro) throws SQLException{

		try(Connection conn = DatabaseManager.getConnection()){
			LibroDAO libroDAO = new LibroDAO(conn);
			libroDAO.guardar(libro);
		}
	}

	public String leerContenidoLibro(Libro libro) throws IOException {
		Path path = Paths.get("resources/libros/" + libro.getRutaContenido());
		return Files.readString(path);
	}
}