# Sistema de Gestión de Biblioteca

Aplicación desarrollada en Java que permite administrar una biblioteca mediante operaciones CRUD, gestionando usuarios, libros y préstamos.

<img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/38dffdbd-d983-4d6c-aed5-0bedc58dc4e9" />

---

## Características

- Gestión de libros (agregar, editar, eliminar, consultar)
- Gestión de usuarios
- Control de préstamos y devoluciones
- Sistema de login
- Arquitectura basada en MVC

---

## Tecnologías utilizadas

- Java
- JavaFX
- JDBC / SQL
- Workbench
- CSS (interfaz)
- Arquitectura MVC

---

## Estructura del proyecto

src/
├── config/
├── controller/
├── dao/
├── model/
├── service/
├── ui/


---

## Instalación y uso

1. Clonar el repositorio:
```bash
git clone https://github.com/CalixtoIsaac/ProyectoBiblioteca.git
```
2. Configurar la base de datos:
- Ejecutar el script en database/schema.sql
- Utilizar de preferencia la configuracion especificada en config/DatabaseManager.java
```bash
    private static final String URL = "jdbc:mysql://localhost:3306/biblioteca_db";
    private static final String USER = "root";
    private static final String PASS = "1234"; //Cambiar la contraseña de mysql
```
3. Ejecutar el proyecto desde:
  BibliotecaFXApp.java

---

## Archivo ejecutable

Si se desea instalar el programa localmente:
- target/build-installer.bat
La base de datos debe de estar configurada localmente

---

## Diagrama UML

<img width="921" height="408" alt="image" src="https://github.com/user-attachments/assets/697e997a-ddfa-4d6d-b91a-0714808fef87" />

---

## Capturas GUI

Estética "biblioteca clásica moderna": El CSS usa crema/marrón oscuro/dorado como paleta. Los paneles de login tienen fondo oscuro con tarjeta blanca flotante, y los paneles de admin/lector tienen sidebar lateral oscuro con área de contenido clara.

# Iniciar sesion

<img width="921" height="691" alt="image" src="https://github.com/user-attachments/assets/8a42f8d6-e4b8-4661-a829-96bfab3a285a" />

# Crear Usuario

Usuario Lector
<img width="921" height="698" alt="image" src="https://github.com/user-attachments/assets/ccf0a520-bf95-4c42-8115-b9ad411188ba" />
Usuario Administrador: Clave adicional requerida (BIBLIOTECA123)
<img width="921" height="780" alt="image" src="https://github.com/user-attachments/assets/51bb314f-fa37-4a1c-a377-6d852711a3ef" />

# Menu Admin

Panel General
<img width="921" height="660" alt="image" src="https://github.com/user-attachments/assets/9dfa43d0-1bcb-435c-8bcf-cdd264732d32" />

Seccion Libros
<img width="921" height="488" alt="image" src="https://github.com/user-attachments/assets/849402fa-af88-4df9-9a34-dc27326a54a5" />

Seccion Usuarios
<img width="921" height="489" alt="image" src="https://github.com/user-attachments/assets/1f6acd14-3573-4f6e-8996-f268644c2bc8" />

Seccion Prestamos
<img width="921" height="487" alt="image" src="https://github.com/user-attachments/assets/926ff113-69da-4aab-9228-96598f6637f8" />

# Menu Usuario

Libros disponibles
<img width="921" height="489" alt="image" src="https://github.com/user-attachments/assets/faffdddc-55d9-4ade-9eb0-043a8e8ab553" />

Prestamos solicitados
<img width="921" height="489" alt="image" src="https://github.com/user-attachments/assets/9c8e93d0-4df5-43de-abf7-ac655196da7e" />

---

## Capturas BD (Workbench)

<img width="921" height="601" alt="image" src="https://github.com/user-attachments/assets/fe2078f0-50f6-48d2-85d3-e73de47edcea" />
<img width="921" height="603" alt="image" src="https://github.com/user-attachments/assets/c172a1ea-8e2a-487f-bc66-c21a9a1170a8" />
<img width="921" height="620" alt="image" src="https://github.com/user-attachments/assets/2ef0ed3b-9058-44e6-9827-9c22cf73b491" />

---

## Autor
Calixto Isaac
Estudiante de Ingeniería en Desarrollo de Software

## Estado del proyecto
En desarrollo / Proyecto académico con potencial de mejora

