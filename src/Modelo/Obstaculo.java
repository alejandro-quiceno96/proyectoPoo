package Modelo;

import java.awt.*;

public class Obstaculo extends Sprite {

    // Tipos de obstáculo (según las instrucciones del juego)
    public static final int TRAMPA = 0;
    public static final int BOLSA = 1;
    public static final int HUMO = 2;

    private int tipo;

    // Contador para las animaciones pequeñas
    private int contador = 0;

    // La nube de humo se mueve con decimales para que persiga suave
    private double posX;
    private double posY;

    public Obstaculo(int tipo, int x, int y, int ancho, int alto) {
        super(x, y);
        this.tipo = tipo;
        this.ancho = ancho;
        this.alto = alto;
        this.posX = x;
        this.posY = y;
    }

    // Trampas y bolsas están en el suelo: se mueven con el camino
    public void mover(int velocidad) {
        contador++;
        x -= velocidad;
    }

    // La nube de humo llega por detrás persiguiendo al oso: mientras está lejos
    // busca su altura, y cuando ya está cerca sigue derecho (hay que saltarla)
    public void perseguir(int objetivoX, int objetivoY, double rapidez) {
        contador++;

        posX += rapidez;

        double centroY = posY + alto / 2.0;
        if (posX + ancho < objetivoX - 150) {
            if (centroY < objetivoY - 2) posY += 1.2;
            if (centroY > objetivoY + 2) posY -= 1.2;
        }

        // Pequeño movimiento de arriba a abajo
        x = (int) posX;
        y = (int) (posY + Math.sin(contador * 0.08) * 4);
    }

    // En el suelo la nube avanza derecho a la altura en que salió
    public void avanzar(double rapidez) {
        contador++;
        posX += rapidez;

        x = (int) posX;
        y = (int) (posY + Math.sin(contador * 0.08) * 4);
    }

    // Caja de colisión un poco más pequeña que el dibujo para que sea justo
    @Override
    public Rectangle getBounds() {
        return new Rectangle(x + 6, y + 6, ancho - 12, alto - 6);
    }

    public boolean salioDePantalla() {
        if (tipo == HUMO) {
            return x > 850;
        }
        return x + ancho < 0;
    }

    public int getTipo() {
        return tipo;
    }

    public void dibujar(Graphics2D g) {
        switch (tipo) {
            case TRAMPA:
                dibujarTrampa(g);
                break;
            case BOLSA:
                dibujarBolsa(g);
                break;
            case HUMO:
                dibujarHumo(g);
                break;
        }
    }

    private void dibujarTrampa(Graphics2D g) {
        // Base de metal
        g.setColor(new Color(80, 80, 85));
        g.fillRoundRect(x, y + alto - 6, ancho, 6, 4, 4);

        // Dientes de la trampa
        g.setColor(new Color(150, 150, 160));
        int dientes = 6;
        int anchoDiente = ancho / dientes;
        for (int i = 0; i < dientes; i++) {
            int dx = x + i * anchoDiente;
            int[] px = {dx, dx + anchoDiente / 2, dx + anchoDiente};
            int[] py = {y + alto - 6, y, y + alto - 6};
            g.fillPolygon(px, py, 3);
        }

        // Cadena
        g.setColor(new Color(110, 110, 115));
        g.drawOval(x + ancho, y + alto - 5, 5, 4);
        g.drawOval(x + ancho + 5, y + alto - 5, 5, 4);
    }

    private void dibujarBolsa(Graphics2D g) {
        // Bolsa tirada en el suelo, un poco aplastada
        int[] px = {x + 2, x + ancho - 6, x + ancho, x + ancho - 8, x + ancho / 2, x + 8, x};
        int[] py = {y + 6, y + 2, y + alto - 3, y + alto, y + alto - 2, y + alto, y + alto - 4};
        g.setColor(new Color(225, 235, 240, 220));
        g.fillPolygon(px, py, px.length);
        g.setStroke(new BasicStroke(1.5f));
        g.setColor(new Color(150, 165, 180));
        g.drawPolygon(px, py, px.length);

        // Asas caídas
        g.setColor(new Color(225, 235, 240, 220));
        g.setStroke(new BasicStroke(3));
        g.drawArc(x + 4, y - 6, 12, 14, 20, 160);
        g.drawArc(x + ancho - 18, y - 8, 12, 14, 0, 160);

        // Arrugas y logo
        g.setStroke(new BasicStroke(1.5f));
        g.setColor(new Color(150, 165, 180));
        g.drawLine(x + 10, y + 8, x + 12, y + alto - 4);
        g.setColor(new Color(210, 70, 60, 180));
        g.fillOval(x + ancho / 2 - 4, y + 8, 8, 8);
        g.setStroke(new BasicStroke(1));
    }

    private void dibujarHumo(Graphics2D g) {
        // Nube de humo gris formada por varios círculos
        g.setColor(new Color(70, 70, 75, 215));
        g.fillOval(x, y + 12, 30, 28);
        g.fillOval(x + 18, y, 34, 34);
        g.fillOval(x + 40, y + 10, 30, 30);
        g.fillOval(x + 12, y + 20, 44, 24);

        g.setColor(new Color(110, 110, 115, 200));
        g.fillOval(x + 22, y + 5, 16, 12);
        g.fillOval(x + 44, y + 14, 12, 10);

        // Ojos enojados porque te persigue
        g.setColor(new Color(255, 90, 60));
        g.fillOval(x + 22, y + 18, 7, 6);
        g.fillOval(x + 38, y + 18, 7, 6);
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(2));
        g.drawLine(x + 20, y + 14, x + 29, y + 17);
        g.drawLine(x + 47, y + 14, x + 38, y + 17);
        g.setStroke(new BasicStroke(1));
    }
}
