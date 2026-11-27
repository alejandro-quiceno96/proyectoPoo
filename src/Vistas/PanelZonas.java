package Vistas;

import Controlador.GestorZonas;
import Controlador.Historial;
import Modelo.ZonaContaminada;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class PanelZonas extends JPanel {

    private static final int ANCHO = 770;
    private static final int ALTO = 425;

    // Tamaño de cada tarjeta de zona
    private static final int ANCHO_TARJETA = 230;
    private static final int ALTO_TARJETA = 138;
    private static final int SEPARACION = 15;
    private static final int INICIO_X = 25;
    private static final int INICIO_Y = 102;

    // Paleta de colores (la misma del menú)
    private static final Color DORADO = new Color(234, 195, 68);
    private static final Color CREMA = new Color(245, 243, 220);
    private static final Color VERDE = new Color(20, 45, 35);
    private static final Color AZUL = new Color(150, 205, 245);

    private Image fondo;
    private Font fuentePixel;

    private JButton btnVolver;
    private JButton btnReiniciar;
    private ArrayList<JButton> botonesLimpiar = new ArrayList<>();

    private String mensaje = "Usa las gotas de agua que recoges para limpiar el páramo";

    // Contraseña para reiniciar todo el juego a cero
    private static final String CONTRASENA = "1234";

    public PanelZonas() {
        setPreferredSize(new Dimension(ANCHO, ALTO));
        setLayout(null);
        setBackground(VERDE);

        cargarFondo();
        cargarFuente();
        crearBotones();
    }

    // Antes de borrar todo (zonas, gotas y ranking) se pide la contraseña
    private void pedirContrasena() {
        JPasswordField campo = new JPasswordField(12);

        int opcion = JOptionPane.showConfirmDialog(
                this,
                campo,
                "Escribe la contraseña",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (opcion != JOptionPane.OK_OPTION) {
            return;
        }

        String escrita = new String(campo.getPassword());

        if (escrita.equals(CONTRASENA)) {
            GestorZonas.empezarDeCero();
            Historial.borrarTodo();
            mensaje = "Usa las gotas de agua que recoges para limpiar el páramo";
            actualizar();
        } else {
            JOptionPane.showMessageDialog(this, "Contraseña incorrecta");
        }
    }

    private void cargarFondo() {
        URL ruta = getClass().getResource("/resources/images/PanelBienvenida.png");
        if (ruta != null) {
            fondo = new ImageIcon(ruta).getImage();
        }
    }

    private void cargarFuente() {
        try {
            InputStream is = getClass().getResourceAsStream(
                    "/resources/fonts/PixelifySans-VariableFont_wght.ttf"
            );
            if (is != null) {
                fuentePixel = Font.createFont(Font.TRUETYPE_FONT, is);
            } else {
                fuentePixel = new Font("SansSerif", Font.BOLD, 14);
            }
        } catch (Exception e) {
            fuentePixel = new Font("SansSerif", Font.BOLD, 14);
        }
    }

    private void crearBotones() {
        ArrayList<ZonaContaminada> zonas = GestorZonas.getZonas();

        // Un botón "Limpiar" dentro de cada tarjeta
        for (int i = 0; i < zonas.size(); i++) {
            int columna = i % 3;
            int fila = i / 3;
            int x = INICIO_X + columna * (ANCHO_TARJETA + SEPARACION);
            int y = INICIO_Y + fila * (ALTO_TARJETA + SEPARACION);

            JButton boton = new JButton();
            boton.setFont(new Font("SansSerif", Font.BOLD, 12));
            boton.setForeground(VERDE);
            boton.setOpaque(true);
            boton.setBorderPainted(false);
            boton.setFocusPainted(false);
            boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            boton.setBounds(x + 15, y + ALTO_TARJETA - 32, ANCHO_TARJETA - 30, 24);

            final int indice = i;
            boton.addActionListener(e -> limpiarZona(indice));

            botonesLimpiar.add(boton);
            add(boton);
        }

        btnVolver = new JButton("VOLVER");
        btnVolver.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnVolver.setBackground(DORADO);
        btnVolver.setForeground(VERDE);
        btnVolver.setOpaque(true);
        btnVolver.setBorderPainted(false);
        btnVolver.setFocusPainted(false);
        btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVolver.setBounds(ANCHO - 155, ALTO - 36, 130, 28);
        add(btnVolver);

        // Botón pequeño para reiniciar todo el juego a cero (pide contraseña)
        btnReiniciar = new JButton("REINICIAR");
        btnReiniciar.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnReiniciar.setMargin(new Insets(0, 0, 0, 0));
        btnReiniciar.setBackground(new Color(180, 70, 60));
        btnReiniciar.setForeground(Color.WHITE);
        btnReiniciar.setOpaque(true);
        btnReiniciar.setBorderPainted(false);
        btnReiniciar.setFocusPainted(false);
        btnReiniciar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnReiniciar.setBounds(25, ALTO - 33, 90, 24);
        btnReiniciar.addActionListener(e -> pedirContrasena());
        add(btnReiniciar);

        actualizar();
    }

    private void limpiarZona(int indice) {
        ZonaContaminada zona = GestorZonas.getZonas().get(indice);

        int resultado = GestorZonas.descontaminar(indice);

        if (resultado == GestorZonas.PARAMO_LIMPIO) {
            mensaje = "¡Limpiaste todo el páramo! Pero se volvió a contaminar...";
        } else if (resultado == GestorZonas.ZONA_LIMPIA) {
            mensaje = "¡Limpiaste " + zona.getNombre() + "!";
        } else {
            int faltan = GestorZonas.getCosto(indice) - GestorZonas.getGotas();
            mensaje = "Te faltan " + faltan + " gotas. ¡Juega para recoger más agua!";
        }

        actualizar();
    }

    // Se llama cada vez que se muestra el panel (las gotas pueden haber cambiado)
    public void actualizar() {
        ArrayList<ZonaContaminada> zonas = GestorZonas.getZonas();

        for (int i = 0; i < zonas.size(); i++) {
            JButton boton = botonesLimpiar.get(i);

            // Si la zona ya está limpia el botón se esconde y se dibuja un letrero
            if (zonas.get(i).isLimpia()) {
                boton.setVisible(false);
            } else {
                boton.setText("LIMPIAR  (" + GestorZonas.getCosto(i) + " gotas)");
                boton.setVisible(true);
                if (GestorZonas.getGotas() >= GestorZonas.getCosto(i)) {
                    boton.setBackground(AZUL);
                } else {
                    boton.setBackground(new Color(180, 180, 175));
                }
            }
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D dibujo = (Graphics2D) g.create();

        dibujo.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        dibujo.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        dibujo.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        // Fondo del páramo oscurecido
        if (fondo != null) {
            dibujo.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
        }
        dibujo.setColor(new Color(20, 45, 35, 205));
        dibujo.fillRect(0, 0, getWidth(), getHeight());

        dibujarEncabezado(dibujo);

        ArrayList<ZonaContaminada> zonas = GestorZonas.getZonas();
        for (int i = 0; i < zonas.size(); i++) {
            int columna = i % 3;
            int fila = i / 3;
            int x = INICIO_X + columna * (ANCHO_TARJETA + SEPARACION);
            int y = INICIO_Y + fila * (ALTO_TARJETA + SEPARACION);
            dibujarTarjeta(dibujo, zonas.get(i), x, y);
        }

        dibujo.dispose();
    }

    private void dibujarEncabezado(Graphics2D dibujo) {
        dibujarTextoCentrado(dibujo, "ZONAS CONTAMINADAS", fuentePixel.deriveFont(Font.BOLD, 30f), CREMA, 40);

        dibujarTextoCentrado(dibujo, mensaje, new Font("SansSerif", Font.PLAIN, 13), Color.WHITE, 62);

        // Recuadro con las gotas y las zonas limpias
        int anchoInfo = 330;
        int x = (ANCHO - anchoInfo) / 2;
        dibujo.setColor(new Color(0, 0, 0, 90));
        dibujo.fillRoundRect(x, 70, anchoInfo, 26, 14, 14);

        dibujarGota(dibujo, x + 16, 74);
        dibujo.setFont(fuentePixel.deriveFont(Font.BOLD, 16f));
        dibujo.setColor(AZUL);
        dibujo.drawString("GOTAS: " + GestorZonas.getGotas(), x + 36, 89);

        dibujo.setColor(DORADO);
        dibujo.drawString("ZONAS LIMPIAS: " + GestorZonas.getZonasLimpias() + " / "
                + GestorZonas.getZonas().size(), x + 160, 89);
    }

    private void dibujarTarjeta(Graphics2D dibujo, ZonaContaminada zona, int x, int y) {
        // Fondo de la tarjeta
        dibujo.setColor(new Color(16, 41, 30, 230));
        dibujo.fillRoundRect(x, y, ANCHO_TARJETA, ALTO_TARJETA, 16, 16);

        Color borde = zona.isLimpia() ? new Color(120, 190, 100) : new Color(150, 110, 70);
        dibujo.setColor(borde);
        dibujo.setStroke(new BasicStroke(2));
        dibujo.drawRoundRect(x, y, ANCHO_TARJETA, ALTO_TARJETA, 16, 16);
        dibujo.setStroke(new BasicStroke(1));

        // Paisaje pequeño de la zona
        Shape recorteAnterior = dibujo.getClip();
        dibujo.clipRect(x + 8, y + 8, ANCHO_TARJETA - 16, 58);
        if (zona.isLimpia()) {
            dibujarPaisajeLimpio(dibujo, x + 8, y + 8, ANCHO_TARJETA - 16, 58);
        } else {
            dibujarPaisajeContaminado(dibujo, x + 8, y + 8, ANCHO_TARJETA - 16, 58);
        }
        dibujo.setClip(recorteAnterior);

        // Nombre y descripción
        dibujo.setFont(new Font("SansSerif", Font.BOLD, 14));
        dibujo.setColor(CREMA);
        dibujo.drawString(zona.getNombre(), x + 12, y + 84);

        dibujo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        if (zona.isLimpia()) {
            dibujo.setColor(new Color(170, 230, 150));
            dibujo.drawString("Agua limpia y frailejones sanos", x + 12, y + 100);

            // Letrero en lugar del botón
            dibujo.setColor(new Color(120, 190, 100));
            dibujo.fillRoundRect(x + 15, y + ALTO_TARJETA - 32, ANCHO_TARJETA - 30, 24, 10, 10);
            dibujo.setFont(new Font("SansSerif", Font.BOLD, 12));
            dibujo.setColor(VERDE);
            String texto = "¡ZONA LIMPIA!";
            int anchoTexto = dibujo.getFontMetrics().stringWidth(texto);
            dibujo.drawString(texto, x + (ANCHO_TARJETA - anchoTexto) / 2, y + ALTO_TARJETA - 15);
        } else {
            dibujo.setColor(new Color(225, 170, 130));
            dibujo.drawString(zona.getDescripcion(), x + 12, y + 100);
        }
    }

    private void dibujarPaisajeContaminado(Graphics2D dibujo, int x, int y, int ancho, int alto) {
        // Cielo café por el humo
        GradientPaint cielo = new GradientPaint(0, y, new Color(110, 95, 75), 0, y + alto, new Color(150, 130, 100));
        dibujo.setPaint(cielo);
        dibujo.fillRect(x, y, ancho, alto);

        // Montaña gris
        dibujo.setColor(new Color(95, 90, 85));
        int[] mx = {x + 40, x + 110, x + 180};
        int[] my = {y + alto - 18, y + 10, y + alto - 18};
        dibujo.fillPolygon(mx, my, 3);

        // Humo
        dibujo.setColor(new Color(70, 70, 70, 170));
        dibujo.fillOval(x + 150, y + 4, 34, 22);
        dibujo.fillOval(x + 170, y + 10, 30, 20);

        // Suelo seco y agua sucia
        dibujo.setColor(new Color(110, 85, 55));
        dibujo.fillRect(x, y + alto - 18, ancho, 18);
        dibujo.setColor(new Color(80, 70, 45));
        dibujo.fillOval(x + 60, y + alto - 14, 90, 10);

        // Basura
        dibujo.setColor(new Color(225, 235, 240));
        dibujo.fillRect(x + 20, y + alto - 14, 10, 8);
        dibujo.setColor(new Color(170, 60, 50));
        dibujo.fillRect(x + 160, y + alto - 12, 8, 6);
        dibujo.setColor(new Color(90, 150, 90));
        dibujo.fillRoundRect(x + 185, y + alto - 12, 14, 5, 3, 3);
    }

    private void dibujarPaisajeLimpio(Graphics2D dibujo, int x, int y, int ancho, int alto) {
        // Cielo azul
        GradientPaint cielo = new GradientPaint(0, y, new Color(90, 160, 230), 0, y + alto, new Color(180, 220, 245));
        dibujo.setPaint(cielo);
        dibujo.fillRect(x, y, ancho, alto);

        // Montaña con nieve
        dibujo.setColor(new Color(80, 120, 90));
        int[] mx = {x + 40, x + 110, x + 180};
        int[] my = {y + alto - 18, y + 10, y + alto - 18};
        dibujo.fillPolygon(mx, my, 3);
        dibujo.setColor(Color.WHITE);
        int[] nx = {x + 96, x + 110, x + 124};
        int[] ny = {y + 19, y + 10, y + 19};
        dibujo.fillPolygon(nx, ny, 3);

        // Pasto y laguna
        dibujo.setColor(new Color(120, 175, 60));
        dibujo.fillRect(x, y + alto - 18, ancho, 18);
        dibujo.setColor(new Color(70, 150, 220));
        dibujo.fillOval(x + 60, y + alto - 14, 90, 10);

        // Frailejones
        dibujarFrailejon(dibujo, x + 25, y + alto - 18);
        dibujarFrailejon(dibujo, x + 180, y + alto - 18);
    }

    private void dibujarFrailejon(Graphics2D dibujo, int x, int base) {
        dibujo.setColor(new Color(100, 80, 50));
        dibujo.fillRect(x - 2, base - 16, 5, 16);
        dibujo.setColor(new Color(185, 200, 150));
        dibujo.fillOval(x - 9, base - 24, 19, 11);
        dibujo.setColor(new Color(245, 200, 40));
        dibujo.fillOval(x - 2, base - 22, 5, 5);
    }

    private void dibujarGota(Graphics2D dibujo, int x, int y) {
        dibujo.setColor(new Color(60, 150, 230));
        int[] px = {x + 7, x + 2, x + 12};
        int[] py = {y, y + 8, y + 8};
        dibujo.fillPolygon(px, py, 3);
        dibujo.fillOval(x + 1, y + 4, 12, 12);
    }

    private void dibujarTextoCentrado(Graphics2D dibujo, String texto, Font fuente, Color color, int y) {
        dibujo.setFont(fuente);
        int x = (getWidth() - dibujo.getFontMetrics().stringWidth(texto)) / 2;

        dibujo.setColor(new Color(0, 0, 0, 170));
        dibujo.drawString(texto, x + 2, y + 2);

        dibujo.setColor(color);
        dibujo.drawString(texto, x, y);
    }

    public JButton getBtnVolver() {
        return btnVolver;
    }
}
