package com.biblioteca_1.model;

public class Libro{
    //===================================================================================================================================================
    //ATRIBUTOS
	private static int contadorid;
	private int id;
	private String isbn;
	private String titulo;
	private String autor;
	private String sinopsis;
	private String rutaContenido;
	private int ejemplaresTotales;
	private int ejemplaresDisponibles;

    //===================================================================================================================================================
    //CONSTRUCTOR
    public Libro() {
		
	}

	public Libro(String isbn, String titulo, String autor, String sinopsis,
			 int ejemplaresTotales, int ejemplaresDisponibles, String rutaContenido) {
		this.id = ++contadorid;
		this.isbn = isbn;
		this.titulo = titulo;
		this.autor = autor;
		this.sinopsis = sinopsis;
		this.ejemplaresTotales = ejemplaresTotales;
		this.ejemplaresDisponibles = ejemplaresDisponibles;
		this.rutaContenido = rutaContenido;
	}

	public Libro(String isbn, String titulo, String autor, String sinopsis, int ejemplaresTotales, String rutaContenido){
		this(isbn, titulo, autor, sinopsis, ejemplaresTotales, ejemplaresTotales, rutaContenido);
	}

	public Libro(String titulo, String autor, String rutaContenido){
		this("N/A", titulo, autor,"Sin sinopsis disponible",1,rutaContenido);
	}

    //===================================================================================================================================================
    //GETTERS AND SETTERS
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

    public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getAutor() {
		return autor;
	}

	public void setAutor(String autor) {
		this.autor = autor;
	}

	public String getSinopsis() {
		return sinopsis;
	}

	public void setSinopsis(String sinopsis) {
		this.sinopsis = sinopsis;
	}

	public int getEjemplaresTotales() {
		return ejemplaresTotales;
	}

	public void setEjemplaresTotales(int ejemplaresTotales) {
		this.ejemplaresTotales = ejemplaresTotales;
	}

	public int getEjemplaresDisponibles() {
		return ejemplaresDisponibles;
	}

	public void setEjemplaresDisponibles(int ejemplaresDisponibles) {
		this.ejemplaresDisponibles = ejemplaresDisponibles;
	}

	public String getRutaContenido() { 
		return rutaContenido; 
	}

	//===================================================================================================================================================
    //TOSTRING
		
	@Override
	public String toString() {
		return """
		-----------------------
		ID: %d
		ISBN: %s
		Título: %s
		Autor: %s
		Ejemplares disponibles: %d/%d
		-----------------------
		""".formatted(id, isbn, titulo, autor,ejemplaresDisponibles, ejemplaresTotales);
	}
} //PUBLIC CLASS LIBROS