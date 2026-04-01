package com.biblioteca_1.console;

import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import com.biblioteca_1.core.AppContext;
import com.biblioteca_1.model.Libro;

public class MenuLibrosUI {
    public static void menuLibros(Scanner sc) {

		String opcion;
		try{
			do {
				System.out.println("===========================\n"
								+"        MENU LIBROS\n"
								+"===========================\n"
								+"1.-Registrar Libro\n"
								+"2.-Consultar Libros\n"
								+"3.-Buscar Libro\n"
								+"4.-Salir del menu Libros\n"
								+"Opcion del Usuario:");
				opcion = sc.nextLine();

				switch(opcion) {
			
					case "1":
						//REGISTRAR LIBRO
						try {

							System.out.println("\n-------------------------------\n");
							String isbn = BibliotecaMain.leerTextoNoVacio(sc, "Ingrese ISBN (Codigo del libro)");
							
							String titulo = BibliotecaMain.leerTextoNoVacio(sc, "Ingrese titulo del libro:");
							
							String autor = BibliotecaMain.leerTextoNoVacio(sc, "Ingrese autor del libro: ");
							
							String sinopsis = BibliotecaMain.leerTextoNoVacio(sc, "Ingrese la sinopsis del libro: ");
							
							System.out.println("Ingrese la cantidad de ejemplares totales: ");
							int ejemplaresTotales = sc.nextInt();
							sc.nextLine();

							if(ejemplaresTotales <= 0){
								System.out.println("Error, no puede estar vacia la cantidad de ejemplares totales");
								break;
							}

							String rutaContenido = BibliotecaMain.leerTextoNoVacio(sc, "Ingrese la ruta de contenido del libro (archivo.txt)");

							Libro l = new Libro(isbn,titulo,autor,sinopsis,ejemplaresTotales,rutaContenido);
							
							AppContext.libroService.registrarLibro(l);
							System.out.println("Libro agregado correctamente");
						
						} catch(InputMismatchException e) {
							System.out.println("Error, no ingreso los datos correctamente\n");
						} finally {
							sc.nextLine(); //LIMPIAR BUFFER
						}
						
					break;
					
					case "2":
						//CONSULTAR LIBROS
						System.out.println("===========================\n"
										+"     Libros Agregados\n"
										+"===========================\n");
						List<Libro> libros = AppContext.libroService.obtenerTodos();
						libros.forEach(System.out::println);
					break;
					
					case "3":
						//BUSCAR LIBRO
						System.out.println("===========================\n"
										+"Ingrese el titulo del Libro:");
						Libro buscar = AppContext.libroService.buscarPorTitulo(sc.nextLine());

						if(buscar != null){
							System.out.println("===========================\n"
												+"     Libro encontrado\n"
												+"===========================\n"
												+ buscar);
							System.out.println(buscar);
						} 
						else {
							System.out.println("===========================\n"
											+"    Libro no encontrado\n"
											+"===========================\n");
						} 
					break;
					
					case "4":
						//SALIR DEL MENU LIBROS
						System.out.println("===========================\n"
										+"    Saliendo de Libros\n"
										+"===========================\n");
					break;
					
					default: System.out.println("Opcion no valida, Intentelo de nuevo.");
				}
			
			} while(!opcion.equals("4"));
		} catch(SQLException e){
			System.out.println(e.getMessage());
		}
	}
}
