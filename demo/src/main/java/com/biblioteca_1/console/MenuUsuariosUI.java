package com.biblioteca_1.console;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import com.biblioteca_1.core.AppContext;
import com.biblioteca_1.model.Usuario;
import com.biblioteca_1.model.usuarioAdministrador;
import com.biblioteca_1.model.usuarioLector;
import com.biblioteca_1.service.UsuarioService;

public class MenuUsuariosUI {

	//===================================================================================================================================================
    // MENU USUARIO
    public static void menuUsuario(Scanner sc, UsuarioService usuarioService) {
		String opcion;
		try{
            do {
                System.out.println("===========================\n"
                                +"      MENU USUARIOS\n"
                                +"===========================\n"
                                +"1.-Agregar Usuario\n"
                                +"2.-Consultar Usuarios\n"
                                +"3.-Buscar Usuario\n"
                                +"4.-Salir del menu Usuarios\n"
                                +"Opcion del usuario:");
                opcion = sc.nextLine();
                switch(opcion) {
                
                    case "1":
                        crearUsuario(sc, usuarioService);
                    break;
                    
                    case "2":
                        System.out.println("=======================\n"
                                        +"   Usuarios Agregados\n"
                                        +"=======================\n");
                        AppContext.usuarioService.obtenerTodos().forEach(System.out::println);
                    break;
                    
                    case "3":
                        System.out.println("=======================\n"
                                        +"Ingrese username:");
                        String username = sc.nextLine();

                        Usuario u = usuarioService.buscarPorUsername(username);
                        if(u != null){
                            System.out.println("=======================\n"
                                            +"   Usuario encontrado\n"
                                            +"=======================\n"
                                            + u);
                        } else {
                            System.out.println("=======================\n"
                                            +"  Usuario no encontrado\n"
                                            +"=======================\n");
                        }
                    break;
                    
                    case "4":
                        System.out.println("=======================\n"
                                        +"  Saliendo de Usuarios\n"
                                        +"=======================\n");
                    break;
                    
                    default: System.out.println("Opcion no valida, Intentelo de nuevo.");
			    }
		    } while(!opcion.equals("4"));
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }catch(RuntimeException e){
            System.out.println(e.getMessage());
        }
	}

	//===================================================================================================================================================
    //CREAR USUARIO
	public static void crearUsuario(Scanner sc, UsuarioService usuarioService){

        try{
            String nombre = BibliotecaMain.leerTextoNoVacio(sc, "Ingrese Nombre: ");

            String apellidos = BibliotecaMain.leerTextoNoVacio(sc, "Ingrese apellidos: ");

            System.out.println("Ingrese su fecha de nacimiento (dd/MM/yyyy): ");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");            
            LocalDate fechaNacimiento;

            try{
                fechaNacimiento = LocalDate.parse(sc.nextLine(), formatter);
            } catch(Exception e){
                System.out.println("Formato de fecha inválido");
                return;
            } 

            String username = BibliotecaMain.leerTextoNoVacio(sc, "Ingrese Username: ");

            if(AppContext.usuarioService.buscarPorUsername(username) != null){
                System.out.println("El usuario ya existe en la base de datos.");
                return;
            }

            String pass1, pass2;
            do{

                System.out.println("Ingrese contraseña (minimo 6 caracteres): ");
                pass1 = sc.nextLine();

                System.out.println("Confirme contraseña: ");
                pass2 = sc.nextLine();

                if(!pass1.equals(pass2)){
                    System.out.println("Las contraseñas no coinciden.");
                } else if(pass1.length() < 6){
                    System.out.println("Contraseña demasiado corta.");
                }

            } while(!pass1.equals(pass2) || pass1.length() < 6);

            System.out.println("Tipo de usuario\n"
                            +"1.-Lector\n"
                            +"2.-Administrador\n"
                            +"Opcion: ");
            int tipo;

            try{
                tipo = Integer.parseInt(sc.nextLine());
            }catch(NumberFormatException e){
                System.out.println("Opcion invalida.");
                return;
            }

            Usuario nuevUsuario = null;

            if(tipo == 1){
                nuevUsuario = new usuarioLector(nombre, apellidos, fechaNacimiento, username, pass1);   
            } else if(tipo == 2){
                System.out.println("Ingrese clave de administrador:");
                String claveAdmin = sc.nextLine();

                if(!claveAdmin.equals("BIBLIOTECA123")){
                    System.out.println("Clave de administrador incorrecta.");
                    return;
                }

                nuevUsuario = new usuarioAdministrador(nombre, apellidos, fechaNacimiento, username, pass1);
            } else {
                System.out.println("Tipo de usuario invalido.");
                return;
            }

            AppContext.usuarioService.registrarUsuario(nuevUsuario);
            System.out.println("Usuario guardado correctamente.");
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }catch(RuntimeException e){
            System.out.println(e.getMessage());
        }
    }

	//===================================================================================================================================================
    //INICIAR SESION
	public static void iniciarSesion(Scanner sc, UsuarioService usuarioService) {
        
        System.out.println("\n--- INICIO DE SESIÓN ---");
        String username = BibliotecaMain.leerTextoNoVacio(sc, "Ingrese Username: ");

        String pass = BibliotecaMain.leerTextoNoVacio(sc, "Ingrese contraseña: ");

        try {
            Usuario usuario = usuarioService.login(username, pass);

            if(usuario instanceof usuarioAdministrador){
                MenuAdminUI.menuUsuarioAdministrador(sc, usuario);
            } else {
                MenuLectorUI.menuUsuarioLector(sc, usuario);
            }
        } catch(RuntimeException e){
            System.out.println("Error:"+ e.getMessage());
        } catch(SQLException e){
            System.out.println("Erron en Base de Datos:"+e.getMessage());
        }
    }
}