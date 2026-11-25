package Vistas;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class VentanaPrincipal extends JFrame {

    private Image logoUAM;

    public VentanaPrincipal() {

        setTitle("PÁRAMO - El Guardián del Frailejón");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

        cargarLogo();

        // Panel de bienvenida
        add(new PanelBienvenida(), BorderLayout.CENTER);

        // Panel inferior de créditos
        add(crearPanelCreditos(), BorderLayout.SOUTH);

        setResizable(false);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void cargarLogo() {

        URL ruta = getClass().getResource(
                "/resources/images/logoUAM.png"
        );

        if (ruta != null) {
            logoUAM = new ImageIcon(ruta).getImage();
        } else {
            System.out.println("No se encontró el logo UAM");
        }
    }

    private JPanel crearPanelCreditos() {

        JPanel panel = new JPanel() {

            @Override
            protected void paintComponent(Graphics g) {

                super.paintComponent(g);

                Graphics2D dibujo = (Graphics2D) g.create();

                dibujo.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                dibujo.setRenderingHint(
                        RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON
                );

                dibujo.setRenderingHint(
                        RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BICUBIC
                );

                // Rectángulo de fondo
                dibujo.setColor(new Color(20, 45, 35));
                dibujo.fillRect(0, 0, getWidth(), getHeight());

                // Línea decorativa superior
                dibujo.setColor(new Color(155, 175, 119));
                dibujo.fillRect(0, 0, getWidth(), 2);

                // Fondo blanco para el logo
                dibujo.setColor(Color.WHITE);
                dibujo.fillRoundRect(
                        10, 5,
                        125, getHeight() - 10,
                        12, 12
                );

                // Logo de la universidad
                if (logoUAM != null) {

                    // Tamaño máximo del logo
                    int anchoMaximo = 200;
                    int altoMaximo = 120;

                    // Conservar las proporciones originales
                    double escala = Math.min(
                            (double) anchoMaximo / logoUAM.getWidth(this),
                            (double) altoMaximo / logoUAM.getHeight(this)
                    );

                    int anchoLogo = (int) (
                            logoUAM.getWidth(this) * escala
                    );

                    int altoLogo = (int) (
                            logoUAM.getHeight(this) * escala
                    );

                    // Centrar horizontal y verticalmente
                    int xLogo = 10 + (125 - anchoLogo) / 2;
                    int yLogo = (getHeight() - altoLogo) / 2;

                    // Dibujar el logo
                    dibujo.drawImage(
                            logoUAM,
                            xLogo, yLogo,
                            anchoLogo, altoLogo,
                            this
                    );

                } else {

                    dibujo.setColor(Color.DARK_GRAY);
                    dibujo.setFont(new Font("Arial", Font.BOLD, 13));
                    dibujo.drawString("LOGO UAM", 35, 45);
                }

                // Espacio disponible para los textos
                int inicioTexto = 145;
                int anchoTexto = getWidth() - inicioTexto - 15;

                // Nombre de la materia
                String materia = "Programación Orientada a Objetos";

                dibujo.setFont(new Font("Arial", Font.BOLD, 16));
                dibujo.setColor(new Color(234, 195, 68));

                FontMetrics fm = dibujo.getFontMetrics();

                int xMateria = inicioTexto
                        + (anchoTexto - fm.stringWidth(materia)) / 2;

                dibujo.drawString(materia, xMateria, 28);

                // Nombres de los integrantes
                String[] integrantes = {
                        "Michel Dahiana Rivera Cardona",
                        "Jacobo Castañeda Alarcon",
                        "Diego Alejandro Quiceno Hernandez"
                };

                dibujo.setFont(new Font("Arial", Font.PLAIN, 11));
                dibujo.setColor(Color.WHITE);

                fm = dibujo.getFontMetrics();

                // Calcular el ancho total de los nombres
                int anchoNombres = 0;
                int separacion = 18;

                for (String nombre : integrantes) {
                    anchoNombres += fm.stringWidth(nombre);
                }

                anchoNombres += separacion * (integrantes.length - 1);

                // Centrar los nombres horizontalmente
                int x = inicioTexto
                        + (anchoTexto - anchoNombres) / 2;

                for (int i = 0; i < integrantes.length; i++) {

                    dibujo.setColor(Color.WHITE);
                    dibujo.drawString(integrantes[i], x, 55);

                    x += fm.stringWidth(integrantes[i]);

                    // Separador vertical entre integrantes
                    if (i < integrantes.length - 1) {

                        dibujo.setColor(new Color(155, 175, 119));

                        dibujo.drawLine(
                                x + separacion / 2, 44,
                                x + separacion / 2, 59
                        );

                        x += separacion;
                    }
                }

                dibujo.dispose();
            }
        };

        panel.setPreferredSize(new Dimension(770, 80));

        return panel;
    }
}