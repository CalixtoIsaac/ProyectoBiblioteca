package com.biblioteca_1.dao;

import com.biblioteca_1.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    private final Connection connection;

    public UsuarioDAO(Connection connection){
        this.connection = connection;
    }

    public Usuario buscarUsuarioPorUsername(String username) throws SQLException{
        String sql = "SELECT * FROM usuarios WHERE username = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapUsuario(rs);
        }
        return null;
    }

    public List<Usuario> obtenerTodos() throws SQLException{
        String sql = "SELECT * FROM usuarios";
        List<Usuario> lista = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery()){
            while(rs.next()) lista.add(mapUsuario(rs));
        }
        return lista;
    }

    public void guardar(Usuario usuario) throws SQLException {

        String sql = """
            INSERT INTO usuarios
            (nombre, apellidos, fecha_nacimiento, username, password, tipo_usuario)
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getApellidos());
            ps.setDate(3, Date.valueOf(usuario.getFechaNacimiento()));
            ps.setString(4, usuario.getUsername());
            ps.setString(5, usuario.getContraseña());
            ps.setString(6, (usuario instanceof usuarioAdministrador) ? "ADMIN" : "LECTOR");
            ps.executeUpdate();
        }
    }

    public void eliminar(int idUsuario) throws SQLException{
        //1. Cerrar prestamos activos del usuario y restaurar Stock
        String sqlPrestamos = "SELECT isbn_libro FROM prestamos WHERE id_usuario = ? AND activo = true";
        try(PreparedStatement ps = connection.prepareStatement(sqlPrestamos)){
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                String isbn = rs.getString("isbn_libro");
                //Restaurar Stock
                String sqlStock = "UPDATE libros SET ejemplares_disponibles = ejemplares_disponibles + 1 WHERE isbn = ?";
                try (PreparedStatement psStock = connection.prepareStatement(sqlStock)){
                    psStock.setString(1, isbn);
                    psStock.executeUpdate();
                }
            }
        }
        //2. Marcar Prestamos como devueltos
        String sqlCerrar = "UPDATE prestamos SET activo = false WHERE id_usuario = ?";
        try (PreparedStatement ps = connection.prepareStatement(sqlCerrar)){
            ps.setInt(1, idUsuario);
            ps.executeUpdate();
        }
        //3. Eliminar el usuario
        String sqlDelete = "DELETE FROM usuarios WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sqlDelete)){
            ps.setInt(1, idUsuario);
            ps.executeUpdate();
        }
    }

    private Usuario mapUsuario(ResultSet rs) throws SQLException {
        String tipo = rs.getString("tipo_usuario");
        Usuario user;
        if (tipo.equals("ADMIN")){
            user = new usuarioAdministrador(
                rs.getString("nombre"), 
                rs.getString("apellidos"), 
                rs.getDate("fecha_nacimiento").toLocalDate(), 
                rs.getString("username"),
                rs.getString("password")
            );
        } else {
            user = new usuarioLector(
                rs.getString("nombre"), 
                rs.getString("apellidos"), 
                rs.getDate("fecha_nacimiento").toLocalDate(), 
                rs.getString("username"),
                rs.getString("password")
            );
        }
        user.setId(rs.getInt("id"));
        return user;
    }
}