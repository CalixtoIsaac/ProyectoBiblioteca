package com.biblioteca_1.model;

import java.time.LocalDate;

public class Prestamo {
    //===================================================================================================================================================
    //ATRIBUTOS
	private int id;
	private int idUsuario;
	private String isbnLibro;
	private LocalDate fechaPrestamo; 
	private LocalDate fechaDevolucion;
	private boolean activo; // true = activo, false = devuelto
    
    //===================================================================================================================================================
    //CONSTRUCTOR
	public Prestamo() {

	}
	
	public Prestamo(int id, int idUsuario, String isbnLibro, LocalDate fechaPrestamo, LocalDate fechaDevolucion, boolean activo) {
		this.id = id;
        this.idUsuario = idUsuario;
        this.isbnLibro = isbnLibro;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucion = fechaDevolucion;
        this.activo = activo;
    }

    //===================================================================================================================================================
    //GETTERS AND SETTERS
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}

	public String getIsbnLibro() {
		return isbnLibro;
	}

	public void setIsbnLibro(String isbnLibro) {
		this.isbnLibro = isbnLibro;
	}

	public LocalDate getFechaPrestamo() {
		return fechaPrestamo;
	}

	public void setFechaPrestamo(LocalDate fechaPrestamo) {
		this.fechaPrestamo = fechaPrestamo;
	}

	public LocalDate getFechaDevolucion() {
		return fechaDevolucion;
	}

	public void setFechaDevolucion(LocalDate fechaDevolucion) {
		this.fechaDevolucion = fechaDevolucion;
	}

	public boolean isActivo() {
		return activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
	}

    //===================================================================================================================================================
    //TOSTRING
	
	public String toString() {
        return 
			"\nID: " + id +
			"\nID Usuario: " + idUsuario +
            "\nISBN Libro: " + isbnLibro +
            "\nFecha Prestamo: " + fechaPrestamo +
        	"\nFecha Devolucion: " + fechaDevolucion +
        	"\nEstado: "+ (activo ? "Activo" : "Devuelto") +
        	"\n-----------------------";
    }
} //PUBLIC CLASS PRESTAMO