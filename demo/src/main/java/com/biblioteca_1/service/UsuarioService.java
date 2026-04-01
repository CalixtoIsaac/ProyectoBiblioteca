package com.biblioteca_1.service;

import com.biblioteca_1.config.DatabaseManager;
import com.biblioteca_1.dao.UsuarioDAO;
import com.biblioteca_1.model.Usuario;
import com.biblioteca_1.security.PasswordUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class UsuarioService {
    
    public Usuario login(String username, String password) throws SQLException{
        
        try(Connection conn = DatabaseManager.getConnection()){
            UsuarioDAO usuarioDAO = new UsuarioDAO(conn);
            Usuario usuario = usuarioDAO.buscarUsuarioPorUsername(username);
            if (usuario == null){
                throw new RuntimeException("Usuario no encontrado.");
            }
            if (!PasswordUtils.checkPassword(password, usuario.getContraseña())){
                throw new RuntimeException("Contraseña incorrecta.");
            }
            return usuario;
        }
    }

    public void registrarUsuario(Usuario usuario) throws SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String hashed = PasswordUtils.hashPassword(usuario.getContraseña());
            usuario.setContraseña(hashed);
            new UsuarioDAO(conn).guardar(usuario);
        }
    }

    public void eliminarUsuario(int idUsuario) throws SQLException {
        try (Connection conn = DatabaseManager.getConnection()){
            conn.setAutoCommit(false);
            try {
                new UsuarioDAO(conn).eliminar(idUsuario);
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public Usuario buscarPorUsername(String username) throws SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            UsuarioDAO usuarioDAO = new UsuarioDAO(conn);
            return usuarioDAO.buscarUsuarioPorUsername(username);
        }
    }

    public List<Usuario> obtenerTodos() throws SQLException {
        try (Connection conn = DatabaseManager.getConnection()){
            UsuarioDAO usuarioDAO = new UsuarioDAO(conn);
            return usuarioDAO.obtenerTodos();
        }
    }
}