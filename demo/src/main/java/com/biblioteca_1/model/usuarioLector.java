package com.biblioteca_1.model;

import java.time.LocalDate;

public class usuarioLector extends Usuario{
    
    private static final int MAX_PRESTAMOS = 2;

    public usuarioLector(
        String nombre, 
        String apellidos, 
        LocalDate fechaNacimiento, 
        String username, 
        String contraseña){
        super(nombre, apellidos, fechaNacimiento, username, contraseña);
    }

    @Override
    public boolean esAdministrador(){
        return false;
    }

    @Override
    public int getMaxPrestamos(){
        return MAX_PRESTAMOS;
    }

} //PUBLIC CLASS USUARIOLECTOR
