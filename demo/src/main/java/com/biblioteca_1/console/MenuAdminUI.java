package com.biblioteca_1.console;

import java.util.Scanner;

import com.biblioteca_1.core.AppContext;
import com.biblioteca_1.model.Usuario;

public class MenuAdminUI {
    public static void menuUsuarioAdministrador(Scanner sc, Usuario admin){

        String opcion;

        do {
			
	        System.out.println("===========================\n"
					          +"|       BIBLIOTECA        |\n"
    				          +"===========================\n"
                              +"1.-LIBROS\n"
                              +"2.-USUARIOS\n"
                              +"3.-PRESTAMOS\n"
                              +"4.-RESUMEN BIBLIOTECA\n"
                              +"5.-SALIR\n"
                              +"Opcion del Usuario: ");
		    opcion = sc.nextLine();
			
			switch(opcion) {
			
			case "1":
				MenuLibrosUI.menuLibros(sc);
			break;
			
			case "2":
				MenuUsuariosUI.menuUsuario(sc, AppContext.usuarioService);
			break;
			
			case "3":
				MenuPrestamosUI.menuPrestamo(sc, admin);
			break;

            case "4":
                BibliotecaMain.mostrarResumenBiblioteca();
            break;
			
			case "5":
				System.out.println("SALIENDO DEL PROGRAMA. . .");
			break;
			
			default:
				System.out.println("=======================\n"
			  		  			  +"    OPCION INVALIDA\n"
			  		  			  +"=======================");
			}
		} while(!opcion.equals("5"));
    }
}
