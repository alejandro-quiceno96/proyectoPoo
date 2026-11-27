package Controlador;

import Modelo.ZonaContaminada;

import java.io.*;
import java.util.ArrayList;

public class GestorZonas {

    private static final String ARCHIVO = "zonas.txt";

    // La primera zona cuesta 250 gotas, la segunda 500, y así sucesivamente
    public static final int GOTAS_PRIMERA_ZONA = 250;

    // Resultados posibles al intentar limpiar una zona
    public static final int NO_SE_PUDO = 0;
    public static final int ZONA_LIMPIA = 1;
    public static final int PARAMO_LIMPIO = 2;

    private static int gotas = 0;
    private static ArrayList<ZonaContaminada> zonas;

    // Lee el archivo la primera vez que se necesita
    private static void cargar() {
        if (zonas != null) {
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ARCHIVO))) {
            gotas = ois.readInt();
            zonas = (ArrayList<ZonaContaminada>) ois.readObject();
        } catch (Exception e) {
            // Si no existe el archivo, se empieza con todas las zonas contaminadas
            gotas = 0;
            zonas = crearZonas();
        }
    }

    private static ArrayList<ZonaContaminada> crearZonas() {
        ArrayList<ZonaContaminada> lista = new ArrayList<>();
        lista.add(new ZonaContaminada("Laguna del Otún", "Botellas en el agua"));
        lista.add(new ZonaContaminada("Nevado del Ruiz", "Basura de turistas"));
        lista.add(new ZonaContaminada("Páramo de Santurbán", "Agua sucia de minería"));
        lista.add(new ZonaContaminada("Chingaza", "Bolsas plásticas"));
        lista.add(new ZonaContaminada("Sumapaz", "Humo de quemas"));
        lista.add(new ZonaContaminada("Páramo del Kumanday", "Latas y empaques"));
        return lista;
    }

    private static void guardar() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO))) {
            oos.writeInt(gotas);
            oos.writeObject(zonas);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Suma las gotas que se recogieron en una partida
    public static void agregarGotas(int cantidad) {
        cargar();
        gotas += cantidad;
        guardar();
    }

    // Limpia una zona si hay gotas suficientes.
    // Cuando se limpian todas, el páramo se vuelve a contaminar
    public static int descontaminar(int indice) {
        cargar();
        ZonaContaminada zona = zonas.get(indice);

        if (zona.isLimpia() || gotas < getCosto(indice)) {
            return NO_SE_PUDO;
        }

        gotas -= getCosto(indice);
        zona.setLimpia(true);

        int resultado = ZONA_LIMPIA;
        if (getZonasLimpias() == zonas.size()) {
            for (ZonaContaminada z : zonas) {
                z.setLimpia(false);
            }
            resultado = PARAMO_LIMPIO;
        }

        guardar();
        return resultado;
    }

    // Gotas que se necesitan para limpiar la zona en esa posición
    public static int getCosto(int indice) {
        return GOTAS_PRIMERA_ZONA * (indice + 1);
    }

    // Empieza de cero: todas las zonas contaminadas y sin gotas guardadas
    public static void empezarDeCero() {
        cargar();
        gotas = 0;
        for (ZonaContaminada z : zonas) {
            z.setLimpia(false);
        }
        guardar();
    }

    public static int getGotas() {
        cargar();
        return gotas;
    }

    public static ArrayList<ZonaContaminada> getZonas() {
        cargar();
        return zonas;
    }

    public static int getZonasLimpias() {
        cargar();
        int cuenta = 0;
        for (ZonaContaminada z : zonas) {
            if (z.isLimpia()) {
                cuenta++;
            }
        }
        return cuenta;
    }
}
