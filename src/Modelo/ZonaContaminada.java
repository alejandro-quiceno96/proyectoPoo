package Modelo;

import java.io.Serializable;

public class ZonaContaminada implements Serializable {

    private String nombre;
    private String descripcion;
    private boolean limpia;

    public ZonaContaminada(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.limpia = false;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public boolean isLimpia() {
        return limpia;
    }

    public void setLimpia(boolean limpia) {
        this.limpia = limpia;
    }
}
