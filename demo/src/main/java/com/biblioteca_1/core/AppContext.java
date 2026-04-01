package com.biblioteca_1.core;

import com.biblioteca_1.service.LibroService;
import com.biblioteca_1.service.PrestamoService;
import com.biblioteca_1.service.UsuarioService;

public class AppContext {
    public static final UsuarioService usuarioService = new UsuarioService();
    public static final LibroService libroService = new LibroService();
    public static final PrestamoService prestamoService = new PrestamoService();

    private AppContext(){
        //Evita instanciacion
    }
}