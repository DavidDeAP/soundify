import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;

public class PanelConEfectoBrillo {
    
    // Constante para controlar la intensidad máxima del brillo
    // 1.0 = 100% (doble brillo), 2.0 = 200% (triple brillo), etc.
    private static final float INTENSIDAD_MAXIMA = 10.0f; // 200% de brillo
    
    public static void animarBrillo(JPanel panel) {
        // Buscar el label con la imagen
        JLabel labelImagen = encontrarLabelImagen(panel);
        if (labelImagen == null) return;
        
        ImageIcon iconoOriginal = (ImageIcon) labelImagen.getIcon();
        if (iconoOriginal == null) return;
        
        // Convertir a BufferedImage
        Image imagen = iconoOriginal.getImage();
        BufferedImage bufferedImg = toBufferedImage(imagen);
        
        // Guardar una referencia a la imagen original
        final BufferedImage imagenOriginal = bufferedImg;
        final ImageIcon iconoOriginalFinal = iconoOriginal;
        
        // Duración total de la animación (3 segundos)
        final int DURACION_TOTAL = 1500; // ms
        final int INTERVALO = 50; // ms entre frames
        
        // Timer para la animación
        Timer timer = new Timer(INTERVALO, null);
        final long[] startTime = {System.currentTimeMillis()};
        
        timer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long elapsed = System.currentTimeMillis() - startTime[0];
                float progress = Math.min(elapsed / (float) DURACION_TOTAL, 1.0f);
                
                // Crear curva de brillo: sube, se mantiene, baja
                float factorBrillo;
                if (progress < 0.33f) {
                    // Subida: 0 a 100% en el primer tercio
                    factorBrillo = progress / 0.33f; // 0.0 a 1.0
                } else if (progress < 0.66f) {
                    // Mantenimiento en 100%
                    factorBrillo = 1.0f;
                } else {
                    // Bajada: 100% a 0 en el último tercio
                    factorBrillo = 1.0f - ((progress - 0.66f) / 0.34f);
                }
                
                // Aplicar brillo con intensidad máxima del 200%
                BufferedImage imagenBrillo = aplicarBrillo200PorCiento(imagenOriginal, factorBrillo);
                labelImagen.setIcon(new ImageIcon(imagenBrillo));
                
                // Detener la animación cuando termine
                if (progress >= 1.0f) {
                    timer.stop();
                    // Restaurar imagen original
                    labelImagen.setIcon(iconoOriginalFinal);
                }
            }
        });
        
        timer.start();
    }
    
    private static BufferedImage aplicarBrillo200PorCiento(BufferedImage imagen, float factorBrillo) {
        // Crear una copia de la imagen
        BufferedImage resultado = new BufferedImage(
            imagen.getWidth(), imagen.getHeight(), imagen.getType());
        
        // Para 200% de brillo máximo:
        // - factorBrillo = 0.0 → escala = 1.0 (brillo normal)
        // - factorBrillo = 1.0 → escala = 3.0 (200% más brillo = 300% del original)
        // Fórmula: escala = 1.0 + (factorBrillo * INTENSIDAD_MAXIMA)
        float escala = 1.0f + (factorBrillo * INTENSIDAD_MAXIMA);
        
        // Aplicar filtro de rescale (brillo)
        RescaleOp op = new RescaleOp(escala, 0, null);
        op.filter(imagen, resultado);
        
        return resultado;
    }
    
    // Versión alternativa con control más fino del brillo
    private static BufferedImage aplicarBrilloExtremo(BufferedImage imagen, float factorBrillo) {
        // Crear una copia de la imagen
        BufferedImage resultado = new BufferedImage(
            imagen.getWidth(), imagen.getHeight(), BufferedImage.TYPE_INT_ARGB);
        
        // Obtener arrays de píxeles para mayor eficiencia
        int[] pixels = imagen.getRGB(0, 0, imagen.getWidth(), imagen.getHeight(), null, 0, imagen.getWidth());
        int[] pixelsResultado = new int[pixels.length];
        
        // Para 200% de brillo: multiplicar por 3 en el pico máximo
        float intensidad = 1.0f + (factorBrillo * 2.0f); // Va de 1.0 a 3.0
        
        for (int i = 0; i < pixels.length; i++) {
            int pixel = pixels[i];
            
            // Extraer componentes ARGB
            int alpha = (pixel >> 24) & 0xFF;
            int red = (pixel >> 16) & 0xFF;
            int green = (pixel >> 8) & 0xFF;
            int blue = pixel & 0xFF;
            
            // Aplicar brillo extremo
            red = Math.min(255, (int)(red * intensidad));
            green = Math.min(255, (int)(green * intensidad));
            blue = Math.min(255, (int)(blue * intensidad));
            
            // Reconstruir pixel
            pixelsResultado[i] = (alpha << 24) | (red << 16) | (green << 8) | blue;
        }
        
        // Establecer píxeles en la imagen resultado
        resultado.setRGB(0, 0, imagen.getWidth(), imagen.getHeight(), pixelsResultado, 0, imagen.getWidth());
        
        return resultado;
    }
    
    // Versión con curva de brillo más dramática
    public static void animarBrilloDramatico(JPanel panel) {
        JLabel labelImagen = encontrarLabelImagen(panel);
        if (labelImagen == null) return;
        
        ImageIcon iconoOriginal = (ImageIcon) labelImagen.getIcon();
        if (iconoOriginal == null) return;
        
        Image imagen = iconoOriginal.getImage();
        BufferedImage bufferedImg = toBufferedImage(imagen);
        
        final BufferedImage imagenOriginal = bufferedImg;
        final ImageIcon iconoOriginalFinal = iconoOriginal;
        
        final int DURACION_TOTAL = 3000;
        final int INTERVALO = 40; // Más frames para animación más suave
        final float BRILLO_MAXIMO = 3.0f; // 200% más = 300% del brillo original
        
        Timer timer = new Timer(INTERVALO, null);
        final long[] startTime = {System.currentTimeMillis()};
        
        timer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long elapsed = System.currentTimeMillis() - startTime[0];
                float progress = Math.min(elapsed / (float) DURACION_TOTAL, 1.0f);
                
                // Curva de brillo más dramática
                float factorBrillo;
                
                // Primer 40%: subida rápida al máximo
                if (progress < 0.4f) {
                    factorBrillo = easeInQuad(progress / 0.4f);
                }
                // Siguiente 20%: mantener en el máximo
                else if (progress < 0.6f) {
                    factorBrillo = 1.0f;
                }
                // Último 40%: bajada más lenta
                else {
                    factorBrillo = easeOutQuad(1.0f - ((progress - 0.6f) / 0.4f));
                }
                
                // Aplicar brillo extremo (hasta 300% del brillo original)
                float escala = 1.0f + (factorBrillo * (BRILLO_MAXIMO - 1.0f));
                
                BufferedImage imagenBrillo = new BufferedImage(
                    imagenOriginal.getWidth(), imagenOriginal.getHeight(), imagenOriginal.getType());
                
                RescaleOp op = new RescaleOp(escala, 0, null);
                op.filter(imagenOriginal, imagenBrillo);
                
                labelImagen.setIcon(new ImageIcon(imagenBrillo));
                
                if (progress >= 1.0f) {
                    timer.stop();
                    labelImagen.setIcon(iconoOriginalFinal);
                }
            }
            
            // Funciones de easing para animaciones más naturales
            private float easeInQuad(float t) {
                return t * t;
            }
            
            private float easeOutQuad(float t) {
                return 1 - (1 - t) * (1 - t);
            }
        });
        
        timer.start();
    }
    
        // Busca y devuelve el primer JLabel que contenga una imagen dentro de un panel
    private static JLabel encontrarLabelImagen(JPanel panel) {
        // Recorre todos los componentes del panel
        for (Component comp : panel.getComponents()) {
            // Verifica si el componente es un JLabel
            if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
                // Comprueba si el JLabel tiene un icono asignado
                if (label.getIcon() != null) {
                    return label; // Retorna el primer JLabel con imagen encontrado
                }
            }
        }
        return null; // Retorna null si no encuentra ningún JLabel con imagen
    }

    // Convierte una Image genérica a BufferedImage (formato manejable para procesamiento)
    private static BufferedImage toBufferedImage(Image img) {
        // Si ya es BufferedImage, lo retorna directamente (sin conversión)
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        // Crea un BufferedImage nuevo con las mismas dimensiones que la imagen original
        BufferedImage bimage = new BufferedImage(
            img.getWidth(null),  // Ancho de la imagen
            img.getHeight(null), // Alto de la imagen
            BufferedImage.TYPE_INT_ARGB); // Formato ARGB (con canal alfa para transparencias)

        // Obtiene el contexto gráfico para dibujar sobre el BufferedImage
        Graphics2D bGr = bimage.createGraphics();
        // Dibuja la imagen original en el BufferedImage
        bGr.drawImage(img, 0, 0, null);
        // Libera los recursos gráficos
        bGr.dispose();

        return bimage; // Retorna la imagen convertida
    }
    
}