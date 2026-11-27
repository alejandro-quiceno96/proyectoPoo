package Vistas;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class PanelInstrucciones extends JPanel {

    // Dimensiones del panel (iguales a las del panel de bienvenida)
    private static final int ANCHO = 770;
    private static final int ALTO = 425;

    // Margen alrededor de la imagen de instrucciones
    private static final int MARGEN = 6;

    // Paleta de colores
    private static final Color DORADO = new Color(234, 195, 68);
    private static final Color VERDE = new Color(20, 45, 35);

    private Image fondo;
    private Image instrucciones;

    private JButton btnVolver;

    public PanelInstrucciones() {

        setPreferredSize(new Dimension(ANCHO, ALTO));
        setLayout(null);
        setBackground(VERDE);

        fondo = cargarImagen("/resources/images/PanelBienvenida.png");
        instrucciones = cargarImagen("/resources/images/instrucciones.png");

        crearBoton();
    }

    // Cargar una imagen desde la carpeta resources
    private Image cargarImagen(String rutaImagen) {

        URL ruta = getClass().getResource(rutaImagen);

        if (ruta != null) {
            return new ImageIcon(ruta).getImage();
        }

        System.out.println("No se encontró la imagen: " + rutaImagen);
        return null;
    }

    // Botón para volver al menú, ubicado en el espacio libre de la derecha
    private void crearBoton() {

        btnVolver = new JButton("VOLVER");
        btnVolver.setFont(new Font("Arial", Font.BOLD, 14));
        btnVolver.setBackground(DORADO);
        btnVolver.setForeground(VERDE);
        btnVolver.setFocusPainted(false);
        btnVolver.setOpaque(true);
        btnVolver.setBorderPainted(false);
        btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnVolver.setBounds(ANCHO - 155, ALTO - 55, 140, 38);

        add(btnVolver);
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D dibujo = (Graphics2D) g.create();

        dibujo.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC
        );

        dibujo.setRenderingHint(
                RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY
        );

        // Fondo del páramo oscurecido para que resalten las instrucciones
        if (fondo != null) {
            dibujo.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
        }

        dibujo.setColor(new Color(20, 45, 35, 200));
        dibujo.fillRect(0, 0, getWidth(), getHeight());

        // Imagen de instrucciones escalada para que quepa completa en el panel
        if (instrucciones != null) {

            int anchoOriginal = instrucciones.getWidth(this);
            int altoOriginal = instrucciones.getHeight(this);

            int anchoDisponible = getWidth() - MARGEN * 2;
            int altoDisponible = getHeight() - MARGEN * 2;

            // Usar la escala más pequeña para conservar las proporciones
            double escala = Math.min(
                    (double) anchoDisponible / anchoOriginal,
                    (double) altoDisponible / altoOriginal
            );

            int anchoImagen = (int) (anchoOriginal * escala);
            int altoImagen = (int) (altoOriginal * escala);

            // Centrar la imagen en el panel
            int x = (getWidth() - anchoImagen) / 2;
            int y = (getHeight() - altoImagen) / 2;

            dibujo.drawImage(instrucciones, x, y, anchoImagen, altoImagen, this);
        }

        dibujo.dispose();
    }

    public JButton getBtnVolver() {
        return btnVolver;
    }
}
