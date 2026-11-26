package Vistas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URL;

public class PanelJuego extends JPanel {

    private static final int ANCHO = 770;
    private static final int ALTO = 425;
    private static final int SUELO_Y = 260;

    private Image fondo;
    private Oso oso;
    private Timer gameLoop;

    // Física vertical (salto y gravedad)
    private int osoY = SUELO_Y;
    private double velocidadY = 0;
    private final double GRAVEDAD = 0.6;
    private final double FUERZA_SALTO = -12;

    // Banderas de teclas presionadas
    private boolean izquierda = false;
    private boolean derecha = false;
    private boolean enElAire = false;

    public PanelJuego() {
        setPreferredSize(new Dimension(ANCHO, ALTO));
        setFocusable(true);

        cargarFondo();
        oso = new Oso(115, osoY);

        configurarTeclado();
        iniciarBucleJuego();
    }

    private void cargarFondo() {
        URL ruta = getClass().getResource("/resources/images/PanelBienvenida.png");
        if (ruta != null) {
            fondo = new ImageIcon(ruta).getImage();
        } else {
            System.out.println("No se encontró la imagen de fondo en PanelJuego");
        }
    }

    private void configurarTeclado() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int tecla = e.getKeyCode();

                // Izquierda (A o Flecha Izquierda)
                if (tecla == KeyEvent.VK_A || tecla == KeyEvent.VK_LEFT) {
                    izquierda = true;
                }

                // Derecha (D o Flecha Derecha)
                if (tecla == KeyEvent.VK_D || tecla == KeyEvent.VK_RIGHT) {
                    derecha = true;
                }

                // Saltar (W, Flecha Arriba o Espacio)
                if ((tecla == KeyEvent.VK_W || tecla == KeyEvent.VK_UP || tecla == KeyEvent.VK_SPACE) && !enElAire) {
                    velocidadY = FUERZA_SALTO;
                    enElAire = true;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int tecla = e.getKeyCode();

                if (tecla == KeyEvent.VK_A || tecla == KeyEvent.VK_LEFT) {
                    izquierda = false;
                }

                if (tecla == KeyEvent.VK_D || tecla == KeyEvent.VK_RIGHT) {
                    derecha = false;
                }
            }
        });
    }

    private void iniciarBucleJuego() {
        gameLoop = new Timer(16, e -> {
            actualizarMovimiento();
            repaint();
        });
        gameLoop.start();
    }

    private void actualizarMovimiento() {
        int direccion = 0;

        if (izquierda) direccion -= 1;
        if (derecha) direccion += 1;

        // El Oso usa su propia velocidad interna y sincroniza sus sprites
        oso.moverManual(direccion, getWidth());

        // Física de salto y caída
        if (enElAire) {
            osoY += (int) velocidadY;
            velocidadY += GRAVEDAD;

            if (osoY >= SUELO_Y) {
                osoY = SUELO_Y;
                velocidadY = 0;
                enElAire = false;
            }
            oso.setY(osoY);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D dibujo = (Graphics2D) g.create();

        // Dibujar fondo
        if (fondo != null) {
            dibujo.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
        }

        // Dibujar oso
        oso.dibujar(dibujo);

        dibujo.dispose();
    }

    public void detenerJuego() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
    }
}
