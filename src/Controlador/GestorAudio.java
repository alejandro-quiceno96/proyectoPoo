package Controlador;

import java.util.HashMap;
import java.util.Map;

public class GestorAudio {
    private static GestorAudio instancia;

    private final Map<String, String> rutasSonidos;
    private TareaAudio tareaMusicaActual;
    private Thread hiloMusicaActual;
    private String musicaActualNombre = "";
    private boolean silenciado = false;

    private GestorAudio() {
        rutasSonidos = new HashMap<>();
        registrarAudios();
    }

    public static synchronized GestorAudio getInstancia() {
        if (instancia == null) {
            instancia = new GestorAudio();
        }
        return instancia;
    }

    private void registrarAudios() {
        String base = "/resources/sounds/";

        rutasSonidos.put("MENU", base + "menu_paramo_loop.wav");
        rutasSonidos.put("GAMEPLAY", base + "gameplay_fusion_sabrosa_loop.wav");
        rutasSonidos.put("INICIAR", base + "sfx_iniciar_juego.wav");
        rutasSonidos.put("BOTON", base + "sfx_boton.wav");
        rutasSonidos.put("GOTA", base + "sfx_gota_agua.wav");
        rutasSonidos.put("LIMPIAR", base + "sfx_limpiar_zona.wav");
        rutasSonidos.put("FLOR", base + "sfx_flor_vida.wav");
        rutasSonidos.put("CONDOR", base + "sfx_pluma_condor.wav");
        rutasSonidos.put("NIEBLA", base + "sfx_escudo_niebla.wav");
        rutasSonidos.put("NIVEL", base + "sfx_subir_nivel.wav");
        rutasSonidos.put("DANO", base + "sfx_perder_vida.wav");
        rutasSonidos.put("GAME_OVER", base + "sfx_game_over.wav");
        rutasSonidos.put("VICTORIA", base + "sfx_victoria.wav");
    }

    public void reproducirMusica(String clave, float volumenDecibelios) {
        if (silenciado || clave.equals(musicaActualNombre)) return;

        detenerMusica();
        String ruta = rutasSonidos.get(clave);
        if (ruta == null) return;

        musicaActualNombre = clave;

        // 1. Instanciamos la clase que implementa Runnable
        tareaMusicaActual = new TareaAudio(clave, ruta, true, volumenDecibelios);
        // 2. Creamos el Thread pasándole el Runnable
        hiloMusicaActual = new Thread(tareaMusicaActual);
        hiloMusicaActual.setDaemon(true);
        // 3. Iniciamos el hilo con start()
        hiloMusicaActual.start();
    }

    public void detenerMusica() {
        musicaActualNombre = "";
        if (tareaMusicaActual != null) {
            tareaMusicaActual.detener();
            tareaMusicaActual = null;
        }
        if (hiloMusicaActual != null && hiloMusicaActual.isAlive()) {
            hiloMusicaActual.interrupt();
            hiloMusicaActual = null;
        }
    }

    public void reproducirEfecto(String clave) {
        if (silenciado) return;

        String ruta = rutasSonidos.get(clave);
        if (ruta == null) return;

        // Cada efecto usa su propia instancia de TareaAudio en un Thread independiente
        TareaAudio tareaEfecto = new TareaAudio(clave, ruta, false, 0.0f);
        Thread hiloEfecto = new Thread(tareaEfecto);
        hiloEfecto.setDaemon(true);
        hiloEfecto.start();
    }
}
