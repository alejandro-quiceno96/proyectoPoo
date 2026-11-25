package Modelo;

import javax.swing.*;
import java.awt.*;

public class Sprite {
    protected int x, y, ancho, alto;
    protected boolean visible = true;
    protected Image imagen;

    public Sprite(int x, int y) {
        this.x = x;
        this.y = y;
    }

    protected void cargarImagen(String nombreImagen) {
        java.net.URL url = getClass().getResource("/resources/images/" + nombreImagen);
        if (url == null) {
            System.err.println("No se encontró la imagen: " + nombreImagen);
            ancho = 30; alto = 30;
            return;
        }
        Image original = new ImageIcon(url).getImage();
        imagen = original.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        ancho = 30;
        alto = 30;
    }

    public Rectangle getBounds() { return new Rectangle(x, y, ancho, alto); }

    public int getX() { return x; }
    public int getY() { return y; }
    public Image getImagen() { return imagen; }
    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }
}