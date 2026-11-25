package Modelo;

import java.io.Serializable;

public class Jugador implements Serializable {
    private String nombre;
    private int puntaje;

    public Jugador(String nombre, int puntaje){
        this.nombre = nombre;
        this.puntaje = puntaje;
    }

    public String getNombre() {
        return nombre;
    }

    public int getPuntaje() {
        return puntaje;
    }
}
