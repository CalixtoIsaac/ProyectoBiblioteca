package com.biblioteca_1.model;

import java.time.LocalDate;

/*import java.util.Scanner;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;*/

public abstract class Usuario{
    //===================================================================================================================================================
    //ATRIBUTOS
    public abstract int getMaxPrestamos();
    protected static int contadorid = 1;
    protected int id;
    protected String nombre;
    protected String apellidos;
    protected LocalDate fechaNacimiento;
    protected String username;
    protected String contraseña;
    protected LocalDate fechaRegistro;

    public abstract boolean esAdministrador();

    //===================================================================================================================================================
    //CONSTRUCTOR
    public Usuario() {

    }
    
    public Usuario(
        String nombre, 
        String apellidos, 
        LocalDate fechaNacimiento, 
        String username,
        String contraseña) {
        this.id = contadorid++;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.fechaNacimiento = fechaNacimiento;
        this.username = username;
        this.contraseña = contraseña;
        this.fechaRegistro = LocalDate.now();
    }

    //===================================================================================================================================================
    //GETTERS AND SETTERS

    public String getUsername(){
        return username;
    }

    public static int getContadorid() {
        return contadorid;
    }

    public static void setContadorid(int contadorid) {
        Usuario.contadorid = contadorid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    //===================================================================================================================================================
    //TOSTRING

    public String toString() {
		return "Id Usuario: "+ id
			  +"\nNombre: "+ nombre
			  +"\n=======================\n";
	}

} //PUBLIC CLASS USUARIO
