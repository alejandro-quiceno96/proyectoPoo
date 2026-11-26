package Vistas;

import Controlador.GestorAudio;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.io.InputStream;

public class    PanelBienvenida extends JPanel {

    //Fuentes
    private Font fuentePixel;

    // Dimensiones del panel
    private static final int ANCHO = 770;
    private static final int ALTO = 425;

    // Dimensiones de los botones
    private static final int ANCHO_BOTON = 260;
    private static final int ALTO_BOTON = 48;

    // Paleta de colores
    private static final Color DORADO = new Color(234, 195, 68);
    private static final Color CREMA = new Color(245, 243, 220);
    private static final Color VERDE = new Color(20, 45, 35);
    private static final Color BLANCO = new Color(233, 238, 229);

    private Image fondo;
    private Oso oso;
    private Timer timer;

    private JButton btnIniciar;
    private JButton btnInstrucciones;

    public PanelBienvenida() {

        setPreferredSize(new Dimension(ANCHO, ALTO));
        setLayout(null);

        cargarFondo();
        cargarFuente();

        oso = new Oso(115, 260);

        crearBotones();
        iniciarAnimacion();
        // Arranca la música del menú en su hilo
        GestorAudio.getInstancia().reproducirMusica("MENU", -5.0f);
    }

    // Cargar imagen de fondo
    private void cargarFondo() {

        URL ruta = getClass().getResource(
                "/resources/images/PanelBienvenida.png"
        );

        if (ruta != null) {
            fondo = new ImageIcon(ruta).getImage();
        } else {
            System.out.println("No se encontró la imagen de fondo");
        }
    }

    // Cargar fuente personalizada
    private void cargarFuente() {
        try {
            InputStream is = getClass().getResourceAsStream(
                    "/resources/fonts/PixelifySans-VariableFont_wght.ttf"
            );
            if (is != null) {
                fuentePixel = Font.createFont(Font.TRUETYPE_FONT, is);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(fuentePixel);
            } else {
                System.out.println("No se encontró la fuente");
                fuentePixel = new Font("SansSerif", Font.BOLD, 14);
            }
        } catch (Exception e) {
            e.printStackTrace();
            fuentePixel = new Font("SansSerif", Font.BOLD, 14);
        }
    }

    // Crear botones del menú
    private void crearBotones() {

        btnIniciar = crearBoton("INICIAR", DORADO, 185);

        btnInstrucciones = crearBoton(
                "INSTRUCCIONES", BLANCO, 246
        );

        add(btnIniciar);
        add(btnInstrucciones);
    }

    // Método reutilizable para crear botones
    private JButton crearBoton(String texto, Color color, int y) {

        JButton boton = new JButton(texto) {

            @Override
            protected void paintComponent(Graphics g) {

                Graphics2D dibujo = (Graphics2D) g.create();

                configurarCalidad(dibujo);

                Color colorActual = color;

                // Efecto al presionar
                if (getModel().isPressed()) {
                    colorActual = color.darker();

                    // Efecto al pasar el cursor
                } else if (getModel().isRollover()) {
                    colorActual = color.brighter();
                }

                // Sombra del botón
                dibujo.setColor(new Color(0, 0, 0, 70));

                dibujo.fillRoundRect(
                        2, 4,
                        getWidth() - 4,
                        getHeight() - 4,
                        15, 15
                );

                // Fondo del botón
                dibujo.setColor(colorActual);

                dibujo.fillRoundRect(
                        0, 0,
                        getWidth() - 3,
                        getHeight() - 5,
                        15, 15
                );

                dibujo.dispose();

                // Dibujar el texto del botón
                super.paintComponent(g);
            }
        };

        boton.setBounds(
                (ANCHO - ANCHO_BOTON) / 2,
                y,
                ANCHO_BOTON,
                ALTO_BOTON
        );

        boton.setFont(new Font("SansSerif", Font.BOLD, 15));
        boton.setForeground(VERDE);

        boton.setContentAreaFilled(false);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setOpaque(false);
        boton.setRolloverEnabled(true);

        boton.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        return boton;
    }

    // Configuración gráfica reutilizable
    private void configurarCalidad(Graphics2D dibujo) {

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
    }

    // Dibujar el fondo del paisaje
    private void dibujarFondo(Graphics2D dibujo) {

        if (fondo != null) {

            dibujo.drawImage(
                    fondo,
                    0, 0,
                    getWidth(), getHeight(),
                    this
            );

        } else {

            dibujo.setColor(VERDE);
            dibujo.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    // Rectángulo semitransparente del menú
    private void dibujarContenedorMenu(Graphics2D dibujo) {

        int anchoMenu = 400;
        int altoMenu = 310;

        int x = (getWidth() - anchoMenu) / 2;
        int y = 21;

        // Sombra exterior
        dibujo.setColor(new Color(0, 0, 0, 60));

        dibujo.fillRoundRect(
                x + 4, y + 5,
                anchoMenu, altoMenu,
                25, 25
        );

        // Fondo verde semitransparente
        dibujo.setColor(new Color(16, 41, 30, 105));

        dibujo.fillRoundRect(
                x, y,
                anchoMenu, altoMenu,
                25, 25
        );

        // Borde decorativo
        dibujo.setColor(new Color(180, 185, 139, 160));
        dibujo.setStroke(new BasicStroke(1.5f));

        dibujo.drawRoundRect(
                x, y,
                anchoMenu, altoMenu,
                25, 25
        );
    }

    // Dibujar los títulos
    private void dibujarTitulos(Graphics2D dibujo) {

        // Texto superior
        dibujarTextoCentrado(
                dibujo,
                "UN JUEGO DEL PÁRAMO COLOMBIANO",
                new Font("SansSerif", Font.BOLD, 12),
                DORADO,
                54,
                0
        );

        // Título principal
        dibujarTextoCentrado(
                dibujo,
                "PÁRAMO",
                fuentePixel.deriveFont(Font.BOLD, 68f),
                CREMA,
                119,
                4
        );

        // Subtítulo
        dibujarTextoCentrado(
                dibujo,
                "El Guardián del Frailejón",
                new Font("SansSerif", Font.BOLD, 19),
                Color.WHITE,
                149,
                2
        );

        // Línea decorativa
        dibujo.setColor(DORADO);
        dibujo.setStroke(new BasicStroke(1.5f));

        int centro = getWidth() / 2;

        dibujo.drawLine(
                centro - 85, 166,
                centro + 85, 166
        );
    }

    // Método reutilizable para textos centrados con sombra
    private void dibujarTextoCentrado(
            Graphics2D dibujo,
            String texto,
            Font fuente,
            Color color,
            int y,
            int sombra
    ) {

        dibujo.setFont(fuente);

        FontMetrics medidas = dibujo.getFontMetrics();

        int x = (getWidth() - medidas.stringWidth(texto)) / 2;

        // Dibujar sombra si se requiere
        if (sombra > 0) {

            dibujo.setColor(new Color(0, 0, 0, 170));

            dibujo.drawString(
                    texto,
                    x + sombra,
                    y + sombra
            );
        }

        // Dibujar texto principal
        dibujo.setColor(color);
        dibujo.drawString(texto, x, y);
    }

    // Animación del oso
    private void iniciarAnimacion() {

        timer = new Timer(16, e -> {

            oso.mover(getWidth());

            repaint();
        });

        timer.start();
    }

    // Dibujar los elementos del panel
    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D dibujo = (Graphics2D) g.create();

        configurarCalidad(dibujo);

        dibujarFondo(dibujo);

        oso.dibujar(dibujo);

        dibujarContenedorMenu(dibujo);

        dibujarTitulos(dibujo);

        dibujo.dispose();
    }

    public JButton getBtnIniciar() {
        return btnIniciar;
        
    }

    public JButton getBtnInstrucciones() {
        return btnInstrucciones;
    }

    public void detenerAnimacion() {
        timer.stop();
    }
}