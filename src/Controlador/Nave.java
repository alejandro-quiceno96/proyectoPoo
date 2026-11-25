package Controlador;

import Modelo.Sprite;

import java.awt.event.KeyEvent;

public class Nave extends Sprite {
    private int dx, dy;
    public Nave(int x, int y){
        super(x,y);
        cargarImagen("nave.png");
    }

    public void mover(){
        x += dx;
        y += dy;

        if(x < 1)x = 1;
        if (y < 1) y = 1;

    }

    public void keyPressed(KeyEvent e){
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) dx = -3;
        if (key == KeyEvent.VK_RIGHT) dx = 3;
        if (key == KeyEvent.VK_UP) dy = -3;
        if (key == KeyEvent.VK_DOWN) dy = -3;
    }

    public void keyReleased(KeyEvent e){
        int key = e.getKeyCode();
        if(key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT) dx = 0;
        if(key == KeyEvent.VK_UP || key == KeyEvent.VK_DOWN) dy = 0;
    }
}
