package com.biblioteca_1.console;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import com.biblioteca_1.core.AppContext;
import com.biblioteca_1.model.Prestamo;
import com.biblioteca_1.model.Usuario;

public class MenuPrestamosUI {
    public static void menuPrestamo(Scanner sc, Usuario admin) {
		
		String opcion;

		try{
			do {
				System.out.println("=====================================\n"
								+"           MENU PRESTAMOS\n"
								+"=====================================\n"
								+"1.-Crear Prestamo\n"
								+"2.-Consultar Prestamos\n"
								+"3.-Devolver Libro\n"
								+"4.-Historial de prestamos por usuario\n"
								+"5.-Salir del menu Prestamos\n"
								+"Opcion del usuario:");
				opcion = sc.nextLine();
				switch(opcion) {
				
				case "1":
					System.out.println("Id del usuario: ");
					int idUsuario = Integer.parseInt(sc.nextLine());

					System.out.println("ISBN del libro: ");
					String isbn = sc.nextLine();
					
					AppContext.prestamoService.realizarPrestamo(idUsuario, isbn);
					System.out.println("Prestamo Creado correctamente.");
				break;
				
				case "2":
					List<Prestamo> lista = AppContext.prestamoService.obtenerTodos();
					if(lista.isEmpty()){
						System.out.println("No hay prestamos registrados.");
					} else{
						lista.forEach(System.out::println);
					}
				break;
				
				case "3":
					System.out.println("Id del prestamo: ");
					int idPrestamo = Integer.parseInt(sc.nextLine());

					System.out.println("ISBN del libro: ");
					String isbnDev = sc.nextLine();

					AppContext.prestamoService.devolverPrestamo(idPrestamo, isbnDev);
					System.out.println("Libro devuelto correctamente.");
				break;
				
				case "4":
					System.out.println("Ingrese username: ");
					String userHistorial = sc.nextLine();

					Usuario usuario= AppContext.usuarioService.buscarPorUsername(userHistorial);

					if(usuario == null){
						System.out.println("Usuario no encontrado.");
						break;
					}

					List<Prestamo> prestamos = AppContext.prestamoService.obtenerPorUsuario(usuario.getId());
					prestamos.forEach(System.out::println);

				break;
				
				case "5":
					System.out.println("=====================================\n"
									  +"        Saliendo de Prestamos\n"
									  +"=====================================\n");
				break;
				
				default:
					System.out.println("Opcion no valida, Intentelo de nuevo.");
				}
				
			} while(!opcion.equals("5"));
		}catch(SQLException e){
			System.out.println(e.getMessage());
		}
	}
}