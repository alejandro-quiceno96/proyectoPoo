package Controlador;

import Modelo.Jugador;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Historial {

    private static final String ARCHIVO = "puntajes.txt";

    public static void guardarPuntaje(Jugador j){
        List<Jugador> lista = leerTodos();

        lista.add(j);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO))){
            oos.writeObject(lista);

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static List<Jugador> leerTodos(){
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ARCHIVO))){
            return (List<Jugador>) ois.readObject();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static List<Jugador> getTop3(){
        return leerTodos().stream()
                .sorted((j1, j2) -> Integer.compare(j2.getPuntaje(), j1.getPuntaje()))
                .limit(3)
                .collect(Collectors.toList());
    }

}
