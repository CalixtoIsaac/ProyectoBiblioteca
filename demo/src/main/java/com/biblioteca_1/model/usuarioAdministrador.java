package com.biblioteca_1.model;

import java.time.LocalDate;

public class usuarioAdministrador extends Usuario{
    public usuarioAdministrador(
        String nombre, 
        String apellidos, 
        LocalDate fechaNacimiento, 
        String username, 
        String contraseña) {
            super(nombre, apellidos, fechaNacimiento, username, contraseña);
        }
        
    @Override
    public boolean esAdministrador(){
        return true;
    }

    @Override
    public int getMaxPrestamos() {
        return Integer.MAX_VALUE;
    }
} //PUBLIC CLASS USUARIOADMINISTRADOR
