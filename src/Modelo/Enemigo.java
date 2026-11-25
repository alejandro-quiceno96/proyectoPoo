package Modelo;

import java.util.Random;

public class Enemigo extends Sprite{

    private int velocidad;
    private Random generador = new Random();

    public Enemigo(int x, int y) {
        super(x, y);
        initEnemigo();
    }

    private void initEnemigo(){
        cargarImagen("enemigo.png");
        reposicionar();
    }

    public void caer(){
        y += velocidad;
        if(y > 600){
            reposicionar();
        }
    }

    public void reposicionar(){
        x = generador.nextInt(750);
        y = generador.nextInt(500);
        velocidad = generador.nextInt(3) + 2;
    }

}
