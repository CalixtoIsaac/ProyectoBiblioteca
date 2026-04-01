package com.biblioteca_1.dao;

import com.biblioteca_1.model.Prestamo;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDAO {
	private final Connection connection;

	public PrestamoDAO(Connection connection){
		this.connection = connection;
	}
    
    public int contarPrestamosActivos(int idUsuario) throws SQLException {
		String sql = "SELECT COUNT(*) FROM prestamos WHERE id_usuario=? AND activo=true";

		try (PreparedStatement ps = connection.prepareStatement(sql)){
			ps.setInt(1, idUsuario);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) return rs.getInt(1);
		}
		return 0;
	}

    public void registrar(int idUsuario, String isbn, LocalDate fechaPrestamo, LocalDate fechaDevolucion) throws SQLException{
		String sql = """
			INSERT INTO prestamos
			(id_usuario, isbn_libro, fecha_prestamo, fecha_devolucion, activo)
			VALUES (?, ? ,? ,? , true)
		""";

		try(PreparedStatement ps = connection.prepareStatement(sql)){

			ps.setInt(1, idUsuario);
			ps.setString(2, isbn);
			ps.setDate(3, Date.valueOf(fechaPrestamo));
			ps.setDate(4, Date.valueOf(fechaDevolucion));

			ps.executeUpdate();
		}
	}

	public void marcarComoDevuelto(int idPrestamo) throws SQLException{
		String sql = """
			UPDATE prestamos
			SET activo = false
			WHERE id = ?
		""";

		try(PreparedStatement ps = connection.prepareStatement(sql)){
			ps.setInt(1, idPrestamo);
			ps.executeUpdate();
		}
	}

    public List<Prestamo> obtenerPorUsuario(int idUsuario) throws SQLException{
		String sql = "SELECT * FROM prestamos WHERE id_usuario = ?";
		List<Prestamo> lista = new ArrayList<>();

		try(PreparedStatement ps = connection.prepareStatement(sql)){
			ps.setInt(1, idUsuario);
			ResultSet rs = ps.executeQuery();

			while (rs.next()){
				lista.add(mapPrestamo(rs));
			}
		}
		return lista;
	}

	private Prestamo mapPrestamo(ResultSet rs) throws SQLException{
		return new Prestamo(
			rs.getInt("id"),
			rs.getInt("id_usuario"),
			rs.getString("isbn_libro"),
			rs.getDate("fecha_prestamo").toLocalDate(),
			rs.getDate("fecha_devolucion").toLocalDate(),
			rs.getBoolean("activo")
		);
	}

	public boolean usuarioTienePrestamoActivo(int idUsuario, String isbn) throws SQLException {

		String sql = "SELECT COUNT(*) FROM prestamos " +
					"WHERE id_usuario = ? AND isbn_libro = ? AND activo = true";

		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, idUsuario);
			ps.setString(2, isbn);

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt(1) > 0;
			}
		}
		return false;
	}

	public List<Prestamo> obtenerTodos() throws SQLException {
		List<Prestamo> lista = new ArrayList<>();
		String sql = "SELECT * FROM prestamos";

		try(PreparedStatement ps = connection.prepareStatement(sql)){
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				lista.add(mapPrestamo(rs));
			}
		}
		return lista;
	}

}