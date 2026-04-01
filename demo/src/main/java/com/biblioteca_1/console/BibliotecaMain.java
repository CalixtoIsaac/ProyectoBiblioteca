package com.biblioteca_1.console;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import com.biblioteca_1.core.AppContext;
import com.biblioteca_1.config.DatabaseManager;

public class BibliotecaMain {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
		String opcion;

        //================================================================================================================================================
        //MENU PRINCIPAL
        do {
            System.out.println("===========================\n"
                              +"           LOGIN\n"
                              +"===========================\n"
                              +"1.-INICIAR SESION\n"
                              +"2.-CREAR CUENTA\n"
                              +"3.-SALIR\n"
                              +"OPCION:");
            opcion = sc.nextLine();


            switch (opcion) {
                case "1":
                    MenuUsuariosUI.iniciarSesion(sc, AppContext.usuarioService);
                break;

                case "2":
                    MenuUsuariosUI.crearUsuario(sc, AppContext.usuarioService);
                break;

                case "3":
                    System.out.println("Saliendo del sistema...");
                break;
            
                default:
                    System.out.println("Opcion Invalida");
            }
        } while(!opcion.equals("3"));
		
		sc.close();
	}

    //===================================================================================================================================================
    //METODOS 
    public static String leerTextoNoVacio(Scanner sc, String mensaje){
        String input;
        do{
            System.out.println(mensaje);
            input = sc.nextLine().trim();
            if(input.isEmpty()){
                System.out.println("El campo no puede estar vacio");
            }
        } while(input.isEmpty());
        return input;
    }

    public static void mostrarResumenBiblioteca(){

        String sqlLibros = "SELECT COUNT(*) AS total FROM libros";
        String sqlUsuarios = "SELECT COUNT(*) AS total FROM usuarios";
        String sqlPrestamos = "SELECT COUNT(*) AS total FROM prestamos WHERE activo = true";

        System.out.println("==== RESUMEN GENERAL DE LA BIBLIOTECA ====");

        try (Connection conn = DatabaseManager.getConnection();
        Statement stmt = conn.createStatement()) {
                
            //Contar Libros 
            ResultSet rsLibros = stmt.executeQuery(sqlLibros);
            if(rsLibros.next()) System.out.println("Libros Registrados: "+ rsLibros.getInt("total"));

            //Contar Usuarios
            ResultSet rsUsuarios = stmt.executeQuery(sqlUsuarios);
            if(rsUsuarios.next()) System.out.println("Usuarios Registrados: "+ rsUsuarios.getInt("total"));

            //Contar Prestamos
            ResultSet rsPrestamos = stmt.executeQuery(sqlPrestamos);
            if(rsPrestamos.next()) System.out.println("Prestamos Registrados: "+ rsPrestamos.getInt("total"));


        }catch(SQLException e){
            System.out.println("Error al obtener el resumen: " + e.getMessage());
        }catch(RuntimeException e){
            System.out.println(e.getMessage());
        }

        System.out.println("==========================================\n");
    }
} //PUBLIC CLASS BIBLIOTECA MAIN