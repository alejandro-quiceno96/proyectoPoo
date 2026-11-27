package Modelo;

import java.awt.*;

public class Recolectable extends Sprite {

    // Tipos de objetos que el oso puede recoger
    public static final int GOTA = 0;
    public static final int FLOR = 1;
    public static final int PLUMA = 2;
    public static final int ESCUDO = 3;

    private int tipo;
    private int contador = 0;
    private int yInicial;

    public Recolectable(int tipo, int x, int y) {
        super(x, y);
        this.tipo = tipo;
        this.yInicial = y;
        this.ancho = 26;
        this.alto = 26;

        // La flor es más grande para que se note que da vida
        if (tipo == FLOR) {
            this.ancho = 36;
            this.alto = 36;
        }
    }

    public void mover(int velocidad) {
        contador++;
        x -= velocidad;
        // Flota un poquito para que se note que se puede recoger
        y = yInicial + (int) (Math.sin(contador * 0.1) * 3);
    }

    public boolean salioDePantalla() {
        return x + ancho < 0;
    }

    public int getTipo() {
        return tipo;
    }

    public void dibujar(Graphics2D g) {
        // Los poderes tienen un brillo alrededor
        if (tipo == PLUMA || tipo == ESCUDO) {
            int brillo = 60 + (int) (Math.sin(contador * 0.15) * 40);
            g.setColor(new Color(255, 240, 150, brillo));
            g.fillOval(x - 8, y - 8, ancho + 16, alto + 16);
        }

        switch (tipo) {
            case GOTA:
                dibujarGota(g);
                break;
            case FLOR:
                dibujarFlor(g);
                break;
            case PLUMA:
                dibujarPluma(g);
                break;
            case ESCUDO:
                dibujarEscudo(g);
                break;
        }
    }

    private void dibujarGota(Graphics2D g) {
        int cx = x + ancho / 2;

        // Punta de la gota y parte redonda
        g.setColor(new Color(60, 150, 230));
        int[] px = {cx, cx - 9, cx + 9};
        int[] py = {y, y + 13, y + 13};
        g.fillPolygon(px, py, 3);
        g.fillOval(cx - 10, y + 6, 20, 20);

        // Borde
        g.setColor(new Color(25, 80, 150));
        g.drawOval(cx - 10, y + 6, 20, 20);

        // Brillo
        g.setColor(new Color(255, 255, 255, 200));
        g.fillOval(cx - 5, y + 11, 5, 7);
    }

    private void dibujarFlor(Graphics2D g) {
        int cx = x + ancho / 2;
        int cy = y + alto / 2;

        // Hojas plateadas del frailejón en forma de roseta
        Graphics2D copia = (Graphics2D) g.create();
        copia.translate(cx, cy);
        for (int i = 0; i < 10; i++) {
            copia.setColor(new Color(175, 195, 150));
            copia.fillOval(-4, -18, 8, 18);
            copia.setColor(new Color(120, 145, 95));
            copia.drawOval(-4, -18, 8, 18);
            copia.rotate(Math.PI / 5);
        }
        copia.dispose();

        // Flores amarillas del centro
        g.setColor(new Color(245, 200, 40));
        g.fillOval(cx - 7, cy - 7, 14, 14);
        g.setColor(new Color(255, 230, 110));
        g.fillOval(cx - 3, cy - 4, 5, 5);

        // Corazón para mostrar que da vida
        int hx = x + ancho - 8;
        int hy = y - 6;
        g.setColor(new Color(220, 50, 60));
        g.fillOval(hx, hy, 7, 7);
        g.fillOval(hx + 6, hy, 7, 7);
        int[] px = {hx, hx + 13, hx + 6};
        int[] py = {hy + 4, hy + 4, hy + 11};
        g.fillPolygon(px, py, 3);
    }

    private void dibujarPluma(Graphics2D g) {
        Graphics2D copia = (Graphics2D) g.create();
        copia.translate(x + ancho / 2, y + alto / 2);
        copia.rotate(-Math.PI / 5);

        // Pluma negra con base blanca (como el cóndor)
        copia.setColor(new Color(25, 25, 30));
        copia.fillOval(-5, -14, 10, 24);
        copia.setColor(Color.WHITE);
        copia.fillOval(-4, 4, 8, 8);

        // Raquis (la línea del centro)
        copia.setColor(new Color(180, 180, 180));
        copia.drawLine(0, -12, 0, 15);
        copia.dispose();
    }

    private void dibujarEscudo(Graphics2D g) {
        // Esfera de niebla
        g.setColor(new Color(220, 235, 245, 200));
        g.fillOval(x, y, ancho, alto);
        g.setColor(new Color(120, 180, 220));
        g.setStroke(new BasicStroke(2));
        g.drawOval(x, y, ancho, alto);

        // Ondas de niebla
        g.setColor(new Color(150, 175, 195));
        g.drawArc(x + 5, y + 7, 8, 6, 0, 180);
        g.drawArc(x + 13, y + 7, 8, 6, 180, 180);
        g.drawArc(x + 5, y + 14, 8, 6, 0, 180);
        g.drawArc(x + 13, y + 14, 8, 6, 180, 180);
        g.setStroke(new BasicStroke(1));
    }
}
