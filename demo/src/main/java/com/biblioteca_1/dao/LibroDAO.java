package com.biblioteca_1.dao;

import com.biblioteca_1.model.Libro;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibroDAO {
	private final Connection connection;

	public LibroDAO(Connection connection){
		this.connection = connection;
	}

	public Libro buscarLibroPorTitulo(String titulo) throws SQLException{
		String sql = "SELECT * FROM libros WHERE titulo LIKE ?";
		try (PreparedStatement ps = connection.prepareStatement(sql)){
			ps.setString(1, "%" + titulo + "%");
			ResultSet rs = ps.executeQuery();
			if (rs.next()) return mapLibro(rs);
		}
		return null;
	}

	public Libro buscarPorIsbn(String isbn) throws SQLException{
		String sql = "SELECT * FROM libros WHERE isbn = ?";
		try(PreparedStatement ps = connection.prepareStatement(sql)){
			ps.setString(1, isbn);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) return mapLibro(rs);
		}
		return null;
	}

	public List<Libro> obtenerTodos() throws SQLException{
		String sql = "SELECT * FROM libros";
		List<Libro> libros = new ArrayList<>();
		try (PreparedStatement ps = connection.prepareStatement(sql);
		ResultSet rs = ps.executeQuery()){
			while (rs.next()) {
				libros.add(mapLibro(rs));
			}
		}
		return libros;
	}

	public void guardar(Libro libro) throws SQLException{
		String sql = """
			INSERT INTO libros 
			(isbn, titulo, autor, sinopsis, ejemplares_totales, ejemplares_disponibles, ruta_archivo)
			VALUES (?, ?, ?, ?, ?, ?, ?)
		""";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, libro.getIsbn());
			ps.setString(2, libro.getTitulo());
			ps.setString(3, libro.getAutor());
			ps.setString(4, libro.getSinopsis());
			ps.setInt(5, libro.getEjemplaresTotales());
			ps.setInt(6, libro.getEjemplaresDisponibles());
			ps.setString(7, libro.getRutaContenido());
			ps.executeUpdate();
		}
	}

	public void actualizarStock(String isbn, int cantidad) throws SQLException{
		String sql = """
				UPDATE libros
				SET ejemplares_disponibles = ejemplares_disponibles + ?
				WHERE isbn = ?
				""";
		try (PreparedStatement ps = connection.prepareStatement(sql)){
			ps.setInt(1, cantidad);
			ps.setString(2, isbn);
			ps.executeUpdate();
		}
	}

	private Libro mapLibro(ResultSet rs) throws SQLException{
		return new Libro(
			rs.getString("isbn"),
			rs.getString("titulo"),
			rs.getString("autor"),
			rs.getString("sinopsis"),
			rs.getInt("ejemplares_totales"),
			rs.getInt("ejemplares_disponibles"),
			rs.getString("ruta_archivo")
		);
	}
}