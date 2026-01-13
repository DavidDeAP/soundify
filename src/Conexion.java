
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author FP
 */
public class Conexion {
    
    private Connection conexion;
    
    String urlDB = "jdbc:mysql://10.11.209.79/Spotify";
    String user = "root"; // Tu usuario de MySQL (normalmente root)
    String pass = "mysql_123";     // Tu contraseña (a veces vacía en XAMPP)
    
    public void conectar() throws SQLException{
     conexion = DriverManager.getConnection("jdbc:mysql://10.11.209.79/Spotify?user=root&password=mysql_123"); 
     // conexion = DriverManager.getConnection("jdbc:mysql://192.168.1.138/Spotify?user=root&password=mysql_123");      
    }
    
public void cargarImagenesEstilos(int idEstilo, JButton botonDestino) {
    // Cambiar la consulta para usar id_estilo en lugar de id_album
    String query = "SELECT imagen_url FROM Estilo WHERE id_estilo = ?";
    
    try {
        // 1. Conexión a la base de datos
        Connection conn = DriverManager.getConnection(urlDB, user, pass);
        PreparedStatement pst = conn.prepareStatement(query);
        pst.setInt(1, idEstilo);
        
        ResultSet rs = pst.executeQuery();
        
        if (rs.next()) {
            // 2. Obtener el nombre del archivo de imagen desde la BD
            String nombreArchivo = rs.getString("imagen_url");
            
            // 3. Construir la ruta del recurso dentro del paquete "estilo"
            String rutaImagen = "/estilos/" + nombreArchivo;
            
            // 4. Cargar la imagen desde recursos usando getResourceAsStream
            InputStream imgStream = getClass().getResourceAsStream(rutaImagen);
            
            if (imgStream != null) {
                // 5. Leer la imagen desde el InputStream
                Image image = ImageIO.read(imgStream);
                imgStream.close(); // Cerrar el stream después de leer
                
                if (image != null) {
                    // 6. Redimensionar la imagen
                    int anchoBoton = botonDestino.getWidth();
                    int altoBoton = botonDestino.getHeight();
                    
                    // Si el botón aún no tiene dimensiones (porque no se ha mostrado), usar valores por defecto
                    if (anchoBoton == 0) anchoBoton = 150; // Tamaño por defecto
                    if (altoBoton == 0) altoBoton = 150;   // Tamaño por defecto
                    
                    // Escalar la imagen manteniendo la proporción
                    Image imagenEscalada = image.getScaledInstance(anchoBoton, altoBoton, Image.SCALE_SMOOTH);
                    botonDestino.setIcon(new ImageIcon(imagenEscalada));
                    botonDestino.setText(""); // Eliminar cualquier texto
                } else {
                    System.out.println("No se pudo cargar la imagen: " + nombreArchivo);
                    botonDestino.setText("Sin imagen");
                }
            } else {
                // Si no se encuentra la imagen, mostrar mensaje de error
                System.out.println("No se encontró la imagen: " + rutaImagen);
                botonDestino.setText("Error: IMG");
            }
        } else {
            System.out.println("No se encontró el estilo con ID: " + idEstilo);
            botonDestino.setText("Sin estilo");
        }
        
        // 7. Cerrar recursos
        rs.close();
        pst.close();
        conn.close();
        
    } catch (SQLException e) {
        System.err.println("Error de base de datos: " + e.getMessage());
        botonDestino.setText("Error DB");
    } catch (IOException e) {
        System.err.println("Error al leer imagen: " + e.getMessage());
        botonDestino.setText("Error IMG");
    } catch (Exception e) {
        System.err.println("Error inesperado: " + e.getMessage());
        botonDestino.setText("Error");
    }
}

public void cargarImagenFondoPanel(int idEstilo, JPanel panelDestino) {
    String query = "SELECT imagen_url FROM Estilo WHERE id_estilo = ?";
    
    try {
        Connection conn = DriverManager.getConnection(urlDB, user, pass);
        PreparedStatement pst = conn.prepareStatement(query);
        pst.setInt(1, idEstilo);
        
        ResultSet rs = pst.executeQuery();
        
        if (rs.next()) {
            String nombreArchivo = rs.getString("imagen_url");
            String rutaImagen = "/fondos/" + nombreArchivo;
            
            // Usar ImageIcon directamente
            ImageIcon iconoOriginal = new ImageIcon(getClass().getResource(rutaImagen));
            
            if (iconoOriginal != null && iconoOriginal.getImage() != null) {
                // Obtener dimensiones
                int ancho = panelDestino.getWidth();
                int alto = panelDestino.getHeight();
                
                if (ancho == 0) ancho = panelDestino.getPreferredSize().width;
                if (alto == 0) alto = panelDestino.getPreferredSize().height;
                if (ancho == 0) ancho = 150;
                if (alto == 0) alto = 150;
                
                // Escalar la imagen
                Image imagenEscalada = iconoOriginal.getImage().getScaledInstance(
                    ancho, alto, Image.SCALE_SMOOTH);
                ImageIcon iconoEscalado = new ImageIcon(imagenEscalada);
                
                // Crear o actualizar un JLabel
                JLabel labelImagen = null;
                
                // Buscar si ya existe un JLabel en el panel
                for (Component comp : panelDestino.getComponents()) {
                    if (comp instanceof JLabel) {
                        labelImagen = (JLabel) comp;
                        break;
                    }
                }
                
                if (labelImagen == null) {
                    // Crear nuevo JLabel
                    labelImagen = new JLabel(iconoEscalado);
                    
                    // Configurar para que ocupe todo el espacio
                    labelImagen.setBounds(0, 0, ancho, alto);
                    
                    // Agregar al panel
                    panelDestino.add(labelImagen);
                    
                    // Mover al fondo
                    panelDestino.setComponentZOrder(labelImagen, panelDestino.getComponentCount() - 1);
                } else {
                    // Actualizar el icono existente
                    labelImagen.setIcon(iconoEscalado);
                    labelImagen.setBounds(0, 0, ancho, alto);
                }
                
                panelDestino.revalidate();
                panelDestino.repaint();
            }
        }
        
        rs.close();
        pst.close();
        conn.close();
        
    } catch (Exception e) {
        System.err.println("Error: " + e.getMessage());
        // No imprimir stack trace si es solo un error de imagen
    }
}
    
    public String conseguirCodigoCancion(int idCancion){
        
        String codigo = "";
        
        String query = "SELECT audio_url FROM Cancion WHERE id_cancion = ?";
        
        try {
        // 2. Conexión a la base de datos
        Connection conn = DriverManager.getConnection(urlDB, user, pass);
        PreparedStatement pst = conn.prepareStatement(query);
        pst.setInt(1, idCancion);

        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            // 3. Obtener el codigo en texto de la BD
            codigo = rs.getString("audio_url");

        } else {
            System.out.println("No se encontró la canción con ID: " + idCancion);
        }

        // Cerrar recursos
        rs.close();
        pst.close();
        conn.close();

    } catch (Exception e) {
        e.printStackTrace();
       
    }
     return codigo;   
    }
    
    public void cargarImagenesGrupos(int idEstilo, JButton botonDestino) {
    // Cambiar la consulta para usar id_estilo en lugar de id_album
    String query = "SELECT imagen_url FROM Grupo WHERE id_grupo = ?";
    
    try {
        // 1. Conexión a la base de datos
        Connection conn = DriverManager.getConnection(urlDB, user, pass);
        PreparedStatement pst = conn.prepareStatement(query);
        pst.setInt(1, idEstilo);
        
        ResultSet rs = pst.executeQuery();
        
        if (rs.next()) {
            // 2. Obtener el nombre del archivo de imagen desde la BD
            String nombreArchivo = rs.getString("imagen_url");
            
            // 3. Construir la ruta del recurso dentro del paquete "estilo"
            String rutaImagen = "/grupos/" + nombreArchivo;
            
            // 4. Cargar la imagen desde recursos usando getResourceAsStream
            InputStream imgStream = getClass().getResourceAsStream(rutaImagen);
            
            if (imgStream != null) {
                // 5. Leer la imagen desde el InputStream
                Image image = ImageIO.read(imgStream);
                imgStream.close(); // Cerrar el stream después de leer
                
                if (image != null) {
                    // 6. Redimensionar la imagen
                    int anchoBoton = botonDestino.getWidth();
                    int altoBoton = botonDestino.getHeight();
                    
                    // Si el botón aún no tiene dimensiones (porque no se ha mostrado), usar valores por defecto
                    if (anchoBoton == 0) anchoBoton = 150; // Tamaño por defecto
                    if (altoBoton == 0) altoBoton = 150;   // Tamaño por defecto
                    
                    // Escalar la imagen manteniendo la proporción
                    Image imagenEscalada = image.getScaledInstance(anchoBoton, altoBoton, Image.SCALE_SMOOTH);
                    botonDestino.setIcon(new ImageIcon(imagenEscalada));
                    botonDestino.setText(""); // Eliminar cualquier texto
                } else {
                    System.out.println("No se pudo cargar la imagen: " + nombreArchivo);
                    botonDestino.setText("Sin imagen");
                }
            } else {
                // Si no se encuentra la imagen, mostrar mensaje de error
                System.out.println("No se encontró la imagen: " + rutaImagen);
                botonDestino.setText("Error: IMG");
            }
        } else {
            System.out.println("No se encontró el estilo con ID: " + idEstilo);
            botonDestino.setText("Sin estilo");
        }
        
        // 7. Cerrar recursos
        rs.close();
        pst.close();
        conn.close();
        
    } catch (SQLException e) {
        System.err.println("Error de base de datos: " + e.getMessage());
        botonDestino.setText("Error DB");
    } catch (IOException e) {
        System.err.println("Error al leer imagen: " + e.getMessage());
        botonDestino.setText("Error IMG");
    } catch (Exception e) {
        System.err.println("Error inesperado: " + e.getMessage());
        botonDestino.setText("Error");
    }
}
    
    public void cargarImagenesAlbumes(int idEstilo, JButton botonDestino) {
    // Cambiar la consulta para usar id_estilo en lugar de id_album
    String query = "SELECT imagen_url FROM Album WHERE id_album = ?";
    
    try {
        // 1. Conexión a la base de datos
        Connection conn = DriverManager.getConnection(urlDB, user, pass);
        PreparedStatement pst = conn.prepareStatement(query);
        pst.setInt(1, idEstilo);
        
        ResultSet rs = pst.executeQuery();
        
        if (rs.next()) {
            // 2. Obtener el nombre del archivo de imagen desde la BD
            String nombreArchivo = rs.getString("imagen_url");
            
            // 3. Construir la ruta del recurso dentro del paquete "estilo"
            String rutaImagen = "/albumes/" + nombreArchivo;
            
            // 4. Cargar la imagen desde recursos usando getResourceAsStream
            InputStream imgStream = getClass().getResourceAsStream(rutaImagen);
            
            if (imgStream != null) {
                // 5. Leer la imagen desde el InputStream
                Image image = ImageIO.read(imgStream);
                imgStream.close(); // Cerrar el stream después de leer
                
                if (image != null) {
                    // 6. Redimensionar la imagen
                    int anchoBoton = botonDestino.getWidth();
                    int altoBoton = botonDestino.getHeight();
                    
                    // Si el botón aún no tiene dimensiones (porque no se ha mostrado), usar valores por defecto
                    if (anchoBoton == 0) anchoBoton = 150; // Tamaño por defecto
                    if (altoBoton == 0) altoBoton = 150;   // Tamaño por defecto
                    
                    // Escalar la imagen manteniendo la proporción
                    Image imagenEscalada = image.getScaledInstance(anchoBoton, altoBoton, Image.SCALE_SMOOTH);
                    botonDestino.setIcon(new ImageIcon(imagenEscalada));
                    botonDestino.setText(""); // Eliminar cualquier texto
                } else {
                    System.out.println("No se pudo cargar la imagen: " + nombreArchivo);
                    botonDestino.setText("Sin imagen");
                }
            } else {
                // Si no se encuentra la imagen, mostrar mensaje de error
                System.out.println("No se encontró la imagen: " + rutaImagen);
                botonDestino.setText("Error: IMG");
            }
        } else {
            System.out.println("No se encontró el estilo con ID: " + idEstilo);
            botonDestino.setText("Sin estilo");
        }
        
        // 7. Cerrar recursos
        rs.close();
        pst.close();
        conn.close();
        
    } catch (SQLException e) {
        System.err.println("Error de base de datos: " + e.getMessage());
        botonDestino.setText("Error DB");
    } catch (IOException e) {
        System.err.println("Error al leer imagen: " + e.getMessage());
        botonDestino.setText("Error IMG");
    } catch (Exception e) {
        System.err.println("Error inesperado: " + e.getMessage());
        botonDestino.setText("Error");
    }
}
}
