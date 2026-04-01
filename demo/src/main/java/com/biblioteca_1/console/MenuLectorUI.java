package com.biblioteca_1.console;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

import com.biblioteca_1.core.AppContext;
import com.biblioteca_1.model.Libro;
import com.biblioteca_1.model.Usuario;

public class MenuLectorUI {
    public static void menuUsuarioLector(Scanner sc, Usuario lector){

        String opcion;
        String titulo;
        try{
            do {

                System.out.println("===========================\n"
                                +"|       BIBLIOTECA        |\n"
                                +"===========================\n"
                                +"1.-VER LIBROS\n"
                                +"2.-MIS PRESTAMOS\n"
                                +"3.-LEER LIBRO\n"
                                +"4.-DEVOLVER LIBRO\n"
                                +"5.-SALIR\n"
                                +"Opcion del Usuario: ");
                opcion = sc.nextLine();

                switch (opcion) {
                    case "1":
                        AppContext.libroService.obtenerTodos().forEach(System.out::println);
                    break;

                    case "2":
                        AppContext.prestamoService.obtenerPorUsuario(lector.getId()).forEach(System.out::println);
                    break;

                    case "3":
                        System.out.println("Ingrese el título del libro:");
                        titulo = sc.nextLine();

                        Libro libro = AppContext.libroService.buscarPorTitulo(titulo);

                        if(libro == null){
                            System.out.println("El libro no existe");
                            break;
                        }

                        if(!AppContext.prestamoService.usuarioTienePrestamoActivo(lector.getId(),libro.getIsbn())){
                            System.out.println("No tienes este prestamo activo.\n"
                                            + "Debe solicitarlo antes de poder leerlo.");
                            break;
                        }

                        System.out.println("CONTENIDO DEL LIBRO: ");
                        System.out.println(AppContext.libroService.leerContenidoLibro(libro));
                    break;

                    case "4":
                        System.out.println("ID Prestamo a devolver: ");
                        int idPrestamo = Integer.parseInt(sc.nextLine());

                        System.out.println("ISBN del libro: ");
                        String isbn = sc.nextLine();

                        AppContext.prestamoService.devolverPrestamo(idPrestamo, isbn);
                        System.out.println("Libro devuelto correctamente.");
                    break;

                    case "5":
                        System.out.println("Cerrando Sesion...");
                    break;
                
                    default:
                        System.out.println("=======================\n"
                                        +"    OPCION INVALIDA\n"
                                        +"=======================");
                }
            } while(!opcion.equals("5"));
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
    }
}